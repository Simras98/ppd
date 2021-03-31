package com.uparis.ppd.service;

import com.uparis.ppd.exception.StorageException;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.repository.MemberRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Qualifier("getJavaMailSender")
    @Autowired
    private JavaMailSender emailSender;

    public Member create(String firstName, String lastName, String address, String city, String postalCode, String email, String phoneNumber, String password) {
        if (checkEmail(email)) {
            Member member = new Member(capitalize(firstName), capitalize(lastName), capitalize(address), capitalize(city), postalCode, email, phoneNumber, bCryptPasswordEncoder.encode(password));
            memberRepository.save(member);
            return member;
        } else {
            return null;
        }
    }

    public void update(Member member) {
        memberRepository.save(member);
    }

    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public Member getById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.orElse(null);
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

    public List<Member> getAllUsers() {
        return memberRepository.findAll();
    }

    public Member connect(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        if (member != null && bCryptPasswordEncoder.matches(password, member.getPassword())) {
            return member;
        } else {
            return null;
        }
    }

    public void addMembers(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
                if (index > 0) {
                    XSSFRow row = worksheet.getRow(index);
                    create(row.getCell(0).getStringCellValue(),
                            row.getCell(1).getStringCellValue(),
                            row.getCell(2).getStringCellValue(),
                            row.getCell(3).getStringCellValue(),
                            row.getCell(4).getStringCellValue(),
                            row.getCell(5).getStringCellValue(),
                            row.getCell(6).getStringCellValue(),
                            createRandomPassword());
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to read file " + file, e);
        }
    }

    public boolean checkEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        return member == null;
    }

    public boolean checkSubscription(Member member) {
        if (member.getDelaySubscription() == 0 && member.getEndSubscription() == 0) {
            return false;
        }
        if (member.getDelaySubscription() != 0) {
            return member.getDelaySubscription() > System.currentTimeMillis();
        }
        return false;
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

    public void resetPassword(String email) {
        Member member = memberRepository.findByEmail(email);
        String newPassword = createRandomPassword();
        member.setPassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact.ourasso@gmail.com");
        message.setTo(email);
        message.setSubject("Votre nouveau mot de passe");
        message.setText(
                "Votre nouveau mot de passe : "
                        + newPassword
                        + "\n"
                        + "https://ppd-asso.herokuapp.com/login");
        emailSender.send(message);
    }

    public void notifyMember(String firstName, String lastName, String address, String city, String postalCode, String email, String phoneNumber) {
        String password = createRandomPassword();
        create(firstName, lastName, address, city, postalCode, email, phoneNumber, password);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact.ourasso@gmail.com");
        message.setTo(email);
        message.setSubject("Bienvenue chez Ourasso !");
        message.setText(
                "Bonjour "+ firstName + " " + lastName + " !" + "\n"
                        + "Bienvenue chez Ourasso !" + "\n"
                        + "Voici vos identifiants pour vous connecter : " + "\n"
                        + email + "\n"
                        + password + "\n"
                        + "https://ppd-asso.herokuapp.com/login");
        emailSender.send(message);
    }

    public String capitalize(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 && ch[i] != ' ' || ch[i] != ' ' && ch[i - 1] == ' ') {
                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    ch[i] = (char) (ch[i] - 'a' + 'A');
                }
            } else if (ch[i] >= 'A' && ch[i] <= 'Z') {
                ch[i] = (char) (ch[i] + 'a' - 'A');
            }
        }
        return new String(ch);
    }
}
