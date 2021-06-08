package com.uparis.ppd.service;

import com.google.re2j.Pattern;
import com.uparis.ppd.exception.StorageException;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.model.Subscription;
import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.repository.MemberRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MemberService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ConstantProperties constantProperties;

    @Autowired
    private FormatService formatService;

    @Autowired
    private RegexService regexService;

    @Autowired
    private MemberRepository memberRepository;

    @Qualifier("getJavaMailSender")
    @Autowired
    private JavaMailSender javaMailSender;

    public Member create(String firstName, String lastName, String sex, String birthDate, String address, String city, String postalCode, String email, String phoneNumber, String password) {
        if (getByEmail(email) == null) {
            if (password == null) {
                password = createRandomPassword();
            }
            Member member = new Member(formatService.formatWord(firstName), formatService.formatWord(lastName), formatService.formatWord(sex), birthDate, formatService.formatWord(address), formatService.formatWord(city), postalCode, email, phoneNumber, bCryptPasswordEncoder.encode(password));
            update(member);
            return member;
        } else {
            return null;
        }
    }

    public void update(Member member) {
        memberRepository.save(member);
    }

    public List<Member> getAll() {
        return memberRepository.findAll();
    }

    public Member getByEmail(String email) {
        Optional<Member> member = Optional.ofNullable(memberRepository.findByEmail(email));
        return member.orElse(null);
    }

    public int getNumberOfPasswords(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            return worksheet.getPhysicalNumberOfRows() -1;
        } catch (IOException e) {
            throw new StorageException("impossible de lire le fichier " + file, e);
        }
    }

    public List<Member> createFromFile(MultipartFile file, List<String> passwords) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            List<Member> members = new ArrayList<>();
            for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
                if (i > 0) {
                    XSSFRow row = worksheet.getRow(i);
                    if (!Pattern.compile(regexService.getWord()).matcher(row.getCell(0).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getWord()).matcher(row.getCell(1).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getSex()).matcher(row.getCell(2).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getBirthDate()).matcher(row.getCell(3).getStringCellValue()).find()
                            || !checkBirthdate(row.getCell(3).getStringCellValue())
                    || !Pattern.compile(regexService.getAddress()).matcher(row.getCell(4).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getWord()).matcher(row.getCell(5).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getPostalCode()).matcher(String.valueOf(row.getCell(6).getNumericCellValue()).substring(0, String.valueOf(row.getCell(6).getNumericCellValue()).length() - 2)).find()
                    || !Pattern.compile(regexService.getEmail()).matcher(row.getCell(7).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getPhoneNumber()).matcher(row.getCell(8).getStringCellValue()).find()
                    || !Pattern.compile(regexService.getLevel()).matcher(row.getCell(9).getStringCellValue()).find()
                    || getByEmail(row.getCell(7).getStringCellValue()) != null) {
                        continue;
                    }
                    members.add(create(row.getCell(0).getStringCellValue(),
                            row.getCell(1).getStringCellValue(),
                            row.getCell(2).getStringCellValue(),
                            row.getCell(3).getStringCellValue(),
                            row.getCell(4).getStringCellValue(),
                            row.getCell(5).getStringCellValue(),
                            String.valueOf(row.getCell(6).getNumericCellValue()).substring(0, String.valueOf(row.getCell(6).getNumericCellValue()).length() - 2),
                            row.getCell(7).getStringCellValue(),
                            row.getCell(8).getStringCellValue(),
                            passwords.get(i-1)));
                }
            }
            return members;
        } catch (IOException e) {
            throw new StorageException("impossible de lire le fichier " + file, e);
        }
    }

    public Member connect(String email, String password) {
        Member member = getByEmail(email);
        if (member != null && bCryptPasswordEncoder.matches(password, member.getPassword())) {
            return member;
        } else {
            return null;
        }
    }

    public void resetPassword(String email) {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mimeMessage.setFrom(new InternetAddress(constantProperties.getOurassoEmail()));
            mimeMessage.setSubject("Ourasso : Votre nouveau mot de passe");

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            Member member = getByEmail(email);
            String newPassword = createRandomPassword();
            member.setPassword(bCryptPasswordEncoder.encode(newPassword));
            update(member);
            String customMessage = "<p style=\"text-align: center;\">Bonjour " + member.getFirstName() + " " + member.getLastName() + " !" + "\n"
                    + "\n" + "Voici votre nouveau mot de passe : " + newPassword + "\n" + "<br>"
                    + "<a href=\" https://ourasso.herokuapp.com/login\">Veuillez cliquer ici pour vous connecter</a></p>"
                    + "<p style=\"text-align: center;\">Cordialement, L’équipe Ourasso</p>";
            FormatService serviceF = new FormatService();
            String messageText = serviceF.mailTemplateGenerator(member.getFirstName(), customMessage,
                    "OurAsso");
            helper.setText(messageText, true);
        };
        javaMailSender.send(preparator);
    }

    public List<String> createRandomPasswords(int number) {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            passwords.add(createRandomPassword());
        }
        return passwords;
    }

    public String createRandomPassword() {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[8];
        assert random != null;
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    public boolean editPassword(String email, String oldPassword, String newPassword, String confirmNewPassword) {
        Member member = connect(email, oldPassword);
        if (member != null && newPassword.equals(confirmNewPassword)) {
            member.setPassword(bCryptPasswordEncoder.encode(newPassword));
            update(member);
            return true;
        }
        return false;
    }

    public boolean checkBirthdate(String birthDate) {
        Date dateNow = new Date();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
            return dateNow.after(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
