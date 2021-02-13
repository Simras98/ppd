package com.uparis.ppd.service;

import com.uparis.ppd.model.User;
import com.uparis.ppd.repository.UserRepository;
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
public class UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Qualifier("getJavaMailSender")
  @Autowired
  private JavaMailSender emailSender;

  public void save(User user) {
    userRepository.save(user);
  }

  public void delete(User user) {
    userRepository.delete(user);
  }

  public User getById(Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.orElse(null);
  }

  public User getByEmail(String email) {
    Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
    return user.orElse(null);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User connect(String email, String password) {
    User user = userRepository.findByEmail(email);
    if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
      return user;
    } else {
      return null;
    }
  }

  public List<User> getBySearch(String keyword) {
    if (keyword != null) {
      return userRepository.search(keyword);
    } else {
      return userRepository.findAll();
    }
  }

  public boolean testEmail(String email) {
    User user = userRepository.findByEmail(email);
    return user == null;
  }

  public void resetPassword(String email) throws NoSuchProviderException, NoSuchAlgorithmException {
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    byte[] bytes = new byte[8];
    random.nextBytes(bytes);
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    String newPassword = encoder.encodeToString(bytes);
    User user = userRepository.findByEmail(email);
    user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    userRepository.save(user);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("Asso");
    message.setTo(email);
    message.setSubject("Votre nouveau mot de passe");
    message.setText(
        "Votre nouveau mot de passe : "
            + newPassword
            + "\n"
            + "https://prag-qlf.herokuapp.com/login");
    emailSender.send(message);
  }
}
