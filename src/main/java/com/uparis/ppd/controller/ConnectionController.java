package com.uparis.ppd.controller;

import com.uparis.ppd.model.User;
import com.uparis.ppd.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.re2j.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Duration;

@Controller
public class ConnectionController {

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired private UserService userService;

  @Value("${hCaptcha.secret.key}")
  private String hCaptchaSecretKey;

  private final HttpClient httpClient;

  private final ObjectMapper om = new ObjectMapper();

  @Value("${controller.index}")
  private String index;

  @Value("${controller.signup}")
  private String signup;

  @Value("${controller.login}")
  private String login;

  @Value("${controller.billing}")
  private String billing;

  @Value("${controller.dashboard}")
  private String dashboard;

  @Value("${controller.error}")
  private String error;

  ConnectionController() {
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  }

  @PostMapping("/hcaptcha")
  public boolean hcaptcha(@RequestParam("h-captcha-response") String captchaResponse)
      throws IOException, InterruptedException {
    if (StringUtils.hasText(captchaResponse)) {
      String sb = "response=" + captchaResponse + "&secret=" + this.hCaptchaSecretKey;
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create("https://hcaptcha.com/siteverify"))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .timeout(Duration.ofSeconds(10))
              .POST(BodyPublishers.ofString(sb))
              .build();
      HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
      JsonNode hCaptchaResponseObject = this.om.readTree(response.body());
      return hCaptchaResponseObject.get("success").asBoolean();
    }
    return false;
  }

  @GetMapping("/signup")
  public String signUp() {
    return signup;
  }
  
  @PostMapping("/signupconfirm")
  public String signUp(
      @RequestParam(name = "lastName") String lastName,
      @RequestParam(name = "firstName") String firstName,
      @RequestParam(name = "email") String email,
      @RequestParam(name = "password") String password,
      @RequestParam(name = "confirmPassword") String confirmPassword,
      @RequestParam(name = "address") String address,
      @RequestParam(name = "postalCode") String postalCode,
      @RequestParam(name = "city") String city,
      @RequestParam(name = "phoneNumber") String phoneNumber,
      @RequestParam(name = "type") String type,
      @RequestParam(name = "company") String company,
      HttpServletRequest request,
      Model model) {
    model.addAttribute("error", "");
    if (email.isEmpty()
        || password.isEmpty()
        || confirmPassword.isEmpty()
        || lastName.isEmpty()
        || firstName.isEmpty()
        || address.isEmpty()
        || postalCode.isEmpty()
        || city.isEmpty()
        || phoneNumber.isEmpty()
        || type.isEmpty()) {
      model.addAttribute("error", "Vous devez remplir les champs avant de valider !");
      return signup;
    }
    if (!Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
        .matcher(email)
        .find()) {
      model.addAttribute("error", "Vous devez rentrer une adresse email valide !");
      return signup;
    }
    if (!password.equals(confirmPassword)) {
      model.addAttribute("error", "Les mots de passe ne correspondent pas !");
      return signup;
    }
    if (!Pattern.compile("[0-9]{5}").matcher(postalCode).find()) {
      model.addAttribute("error", "Vous devez rentrer un code postal valide !");
      return signup;
    }
    if (!Pattern.compile("(0|\\+33)[1-9]( *[0-9]{2}){4}").matcher(phoneNumber).find()) {
      model.addAttribute("error", "Vous devez rentrer un numéro de téléphone valide !");
      return signup;
    }
    model.addAttribute("error", "");
    if (userService.testEmail(email)) {
        User user =
            new User(
                firstName,
                lastName,
                email,
                bCryptPasswordEncoder.encode(password),
                address,
                postalCode,
                city,
                phoneNumber);
        userService.save(user);
        request.getSession().setAttribute("user", user);
        return dashboard;
    } else {
      model.addAttribute("error", "L'adresse email est déjà utilisée !");
      return signup;
    }
  }

  @GetMapping("/login")
  public String login() {
    return login;
  }

  @PostMapping("/loginconfirm")
  public String login(
      @RequestParam(name = "email") String email,
      @RequestParam(name = "password") String password,
      HttpServletRequest request,
      Model model) {
    model.addAttribute("error", "");
    if (email.isEmpty() || password.isEmpty()) {
      model.addAttribute("error", "Vous devez remplir les champs avant de valider !");
      return login;
    }
    User user = userService.connect(email, password);
    request.getSession().setAttribute("user", user);
    if (user != null) {
      if (user.getEndSubscription() < System.currentTimeMillis()) {
        model.addAttribute(
            "error", "Votre abonnement a expiré. Veuillez renouveler votre abonnement");
        return billing;
      } else {
        return "redirect:/";
      }
    } else {
      model.addAttribute("error", "L'adresse email et le mot de passe ne correspondent pas !");
      return login;
    }
  }

  @GetMapping("/logout")
  public String logout(HttpServletRequest request) {
    request.getSession().invalidate();
    return "redirect:/";
  }

  @GetMapping("/passwordforgotten")
  public String passwordforgotten() {
    return "passwordforgotten";
  }

  @PostMapping("/passwordforgotten")
  public String passwordforgotten(
      @RequestParam(name = "email", required = false) String email, Model model)
      throws NoSuchProviderException, NoSuchAlgorithmException {
    User user = userService.getByEmail(email);
    if (user != null) {
      userService.resetPassword(email);
      model.addAttribute("email", email);
      return "passwordforgottenconfirm";
    } else {
      model.addAttribute("error", "L'adresse email n'a pas été trouvé !");
      return error;
    }
  }
}
