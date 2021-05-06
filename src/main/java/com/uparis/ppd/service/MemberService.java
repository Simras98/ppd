package com.uparis.ppd.service;

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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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
    private MemberRepository memberRepository;

    @Qualifier("getJavaMailSender")
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String ourassoEmail;

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

    public List<Member> getBySearch(String keyword) {
        if (keyword != null) {
            return memberRepository.search(keyword);
        } else {
            return memberRepository.findAll();
        }
    }

    public int getNumberOfPasswords(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            return worksheet.getPhysicalNumberOfRows();
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
                    members.add(create(row.getCell(0).getStringCellValue(),
                            row.getCell(1).getStringCellValue(),
                            row.getCell(2).getStringCellValue(),
                            row.getCell(3).getStringCellValue(),
                            row.getCell(4).getStringCellValue(),
                            row.getCell(5).getStringCellValue(),
                            String.valueOf(row.getCell(6).getNumericCellValue()).substring(0, String.valueOf(row.getCell(6).getNumericCellValue()).length() - 2),
                            row.getCell(7).getStringCellValue(),
                            row.getCell(8).getStringCellValue(),
                            passwords.get(i)));
                }
            }
            return members;
        } catch (IOException e) {
            throw new StorageException("impossible de lire le fichier " + file, e);
        }
    }

    public void resetPassword(String email) {
        Member member = getByEmail(email);
        String newPassword = createRandomPassword();
        member.setPassword(bCryptPasswordEncoder.encode(newPassword));
        update(member);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(ourassoEmail);
        message.setTo(email);
        message.setSubject("Ourasso : Votre nouveau mot de passe");
        message.setText(
                "Bonjour " + member.getFirstName() + " " + member.getLastName() + " !" + "\n"
                        + "\n"
                        + "Voici votre nouveau mot de passe : " + newPassword + "\n"
                        + "\n"
                        + "Voici l'adresse pour vous connecter : " + "\n"
                        + "https://ppd-asso.herokuapp.com/login");
        emailSender.send(message);
    }

    public Member connect(String email, String password) {
        Member member = getByEmail(email);
        if (member != null && bCryptPasswordEncoder.matches(password, member.getPassword())) {
            return member;
        } else {
            return null;
        }
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

    public void sendEmail(String object, String body, Member member, Subscription subscription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(constantProperties.getOurassoEmail());
        message.setTo(member.getEmail());
        message.setSubject(object);
        message.setText("Bonjour " + member.getFirstName() + " " + member.getLastName() + ", " +
                subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + " vous a envoyé un message :" + "\n"
                + "\n"
                + body + "\n"
                + "\n"
                + "Cordialement, l'équipe Ourasso.");
        emailSender.send(message);
    }
}
