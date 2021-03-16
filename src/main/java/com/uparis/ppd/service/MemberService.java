package com.uparis.ppd.service;

import com.uparis.ppd.model.Member;
import com.uparis.ppd.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;

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

    public void resetPassword(String email) throws NoSuchProviderException, NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String newPassword = encoder.encodeToString(bytes);
        Member member = memberRepository.findByEmail(email);
        member.setPassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Asso");
        message.setTo(email);
        message.setSubject("Votre nouveau mot de passe");
        message.setText(
                "Votre nouveau mot de passe : "
                        + newPassword
                        + "\n"
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
