package com.uparis.ppd.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.re2j.Pattern;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.service.MemberService;
import com.uparis.ppd.service.RegexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.Duration;

@Controller
public class ConnectionController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RegexService regexService;

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

    @Value("${controller.success}")
    private String success;

    @Value("${controller.error}")
    private String error;

    @Value("${controller.passwordforgotten}")
    private String passwordForgotten;

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
    public String signUpConfirm(
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "postalCode") String postalCode,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            HttpServletRequest request,
            Model model) {
        model.addAttribute(error, "");
        if (firstName.isEmpty()
                || lastName.isEmpty()
                || address.isEmpty()
                || city.isEmpty()
                || postalCode.isEmpty()
                || email.isEmpty()
                || phoneNumber.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {
            model.addAttribute(error, "Vous devez remplir les champs pour valider !");
            return signup;
        }
        if (!Pattern.compile(regexService.getWord()).matcher(firstName).find()) {
            model.addAttribute(error, "Vous devez rentrer un prénom valide !");
            return signup;
        }
        if (!Pattern.compile(regexService.getWord()).matcher(lastName).find()) {
            model.addAttribute(error, "Vous devez rentrer un nom valide !");
            return signup;
        }
        if (!Pattern.compile(regexService.getAddress()).matcher(address).find()) {
            model.addAttribute(error, "Vous devez rentrer une adresse valide !");
            return signup;
        }
        if (!Pattern.compile(regexService.getWord()).matcher(city).find()) {
            model.addAttribute(error, "Vous devez rentrer une ville valide !");
            return signup;
        }
        if (!Pattern.compile(regexService.getPostalCode()).matcher(postalCode).find()) {
            model.addAttribute(error, "Vous devez rentrer un code postal valide !");
            return signup;
        }
        if (!Pattern.compile(regexService.getEmail()).matcher(email).find()) {
            model.addAttribute(error, "Vous devez rentrer une adresse mail valide !");
            return signup;
        }
        if (!Pattern.compile(regexService.getPhoneNumber()).matcher(phoneNumber).find()) {
            model.addAttribute(error, "Vous devez rentrer un numéro de téléphone valide !");
            return signup;
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute(error, "Les mots de passe ne correspondent pas !");
            return signup;
        }
        Member member = memberService.notifyMember(firstName, lastName, address, city, postalCode, email, phoneNumber, password, "False");
        if (member != null) {
            request.getSession().setAttribute("member", member);
            return billing;
        } else {
            model.addAttribute(error, "L'adresse email est déjà utilisée !");
            return signup;
        }
    }

    @GetMapping("/login")
    public String login() {
        return login;
    }

    @PostMapping("/loginconfirm")
    public String loginConfirm(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            HttpServletRequest request,
            Model model) {
        model.addAttribute(error, "");
        if (email.isEmpty() || password.isEmpty()) {
            model.addAttribute(error, "Vous devez remplir les champs avant de valider !");
            return login;
        }
        Member member = memberService.connect(email, password);
        if (member != null) {
            request.getSession().setAttribute("member", member);
            if (memberService.checkSubscription(member)) {
                model.addAttribute(error, "Votre abonnement a expiré. Veuillez renouveler votre abonnement !");
                return billing;
            } else {
                return index;
            }
        } else {
            model.addAttribute(error, "L'adresse email et le mot de passe ne correspondent pas !");
            return login;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return index;
    }

    @GetMapping("/passwordforgotten")
    public String passwordForgotten() {
        return passwordForgotten;
    }

    @PostMapping("/passwordforgotten")
    public String passwordForgotten(
            @RequestParam(name = "email") String email, Model model) {
        Member member = memberService.getByEmail(email);
        if (member != null) {
            memberService.resetPassword(email);
            model.addAttribute(success, "Un email vous a été envoyé !");
        } else {
            model.addAttribute(error, "L'adresse email n'a pas été trouvée !");
        }
        return passwordForgotten;
    }
}
