package com.uparis.ppd.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.re2j.Pattern;
import com.uparis.ppd.model.Association;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.model.Status;
import com.uparis.ppd.model.Subscription;
import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Collections;

@Controller
public class ConnectionController {

    @Autowired
    private AssociationService associationService;

    @Autowired
    private ConstantProperties constantProperties;

    @Autowired
    private FormatService formatService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RegexService regexService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private SubscriptionService subscriptionService;

    private final HttpClient httpClient;

    private final ObjectMapper om = new ObjectMapper();

    ConnectionController() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    @PostMapping("/hcaptcha")
    public boolean hcaptcha(@RequestParam("h-captcha-response") String captchaResponse)
            throws IOException, InterruptedException {
        if (StringUtils.hasText(captchaResponse)) {
            String sb = "response=" + captchaResponse + "&secret=" + constantProperties.getHcaptchaSecretKey();
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

    @GetMapping("/login")
    public String login() {
        return constantProperties.getControllerLogin();
    }

    @PostMapping("/loginconfirm")
    public String loginConfirm(
            @RequestParam(name = "associationName") String associationName,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            HttpServletRequest request,
            Model model) {
        if (associationName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFulfillFields());
            return constantProperties.getControllerLogin();
        }
        Member member = memberService.connect(email, password);
        if (member != null) {
            Association association = associationService.getByName(formatService.formatWord(associationName));
            if (association != null) {
                Subscription subscription = subscriptionService.getSubscription(member, association);
                if (subscription != null) {
                    request.getSession().setAttribute(constantProperties.getAttributeNameSubscription(), subscription);
                    if (subscriptionService.isValid(subscription)) {
                        return constantProperties.getControllerIndex();
                    } else {
                        model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                        if (subscriptionService.getStatus(subscription)) {
                            model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                            return constantProperties.getControllerBillingSuperAdmin();
                        } else {
                            return constantProperties.getControllerBillingMember();
                        }
                    }
                } else {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescNotSubscribed());
                    return constantProperties.getControllerLogin();
                }
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationNotExist());
                return constantProperties.getControllerLogin();
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLoginFailed());
            return constantProperties.getControllerLogin();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return constantProperties.getControllerIndex();
    }

    @GetMapping("/passwordforgotten")
    public String passwordForgotten() {
        return constantProperties.getControllerPasswordForgotten();
    }

    @PostMapping("/passwordforgotten")
    public String passwordForgotten(@RequestParam(name = "email") String email, Model model) {
        Member member = memberService.getByEmail(email);
        if (member != null) {
            memberService.resetPassword(email);
            model.addAttribute(constantProperties.getAttributeNameSuccess(), constantProperties.getAttributeDescEmailSend());
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescEmailNotFound());
        }
        return constantProperties.getControllerPasswordForgotten();
    }

    @GetMapping("/signup")
    public String signUp() {
        return constantProperties.getControllerSignup();
    }

    @GetMapping("/signupmember")
    public String signupMember() {
        return constantProperties.getControllerSignup();
    }

    @PostMapping("/signupmemberconfirm")
    public String signupMemberConfirm(
            @RequestParam(name = "associationName") String associationName,
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "sex") String sex,
            @RequestParam(name = "birthDate") String birthDate,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "postalCode") String postalCode,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            HttpServletRequest request,
            Model model) {
        if (associationName.isEmpty()
                || firstName.isEmpty()
                || lastName.isEmpty()
                || sex.isEmpty()
                || birthDate.isEmpty()
                || address.isEmpty()
                || city.isEmpty()
                || postalCode.isEmpty()
                || email.isEmpty()
                || phoneNumber.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFulfillFields());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(associationName).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationName());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(firstName).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFirstname());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(lastName).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLastname());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getSex()).matcher(sex).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSex());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getBirthDate()).matcher(birthDate).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescBirthdate());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getAddress()).matcher(address).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAddress());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(city).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCity());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getPostalCode()).matcher(postalCode).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPostalCode());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getEmail()).matcher(email).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescEmail());
            return constantProperties.getControllerSignupMember();
        }
        if (!Pattern.compile(regexService.getPhoneNumber()).matcher(phoneNumber).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPhoneNumber());
            return constantProperties.getControllerSignupMember();
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLoginFailed());
            return constantProperties.getControllerSignupMember();
        }
        Member member = memberService.getByEmail(email);
        if (member == null) {
            Association association = associationService.getByName(associationName);
            if (association != null) {
                member = memberService.create(firstName, lastName, sex, birthDate, address, city, postalCode, email, phoneNumber, password);
                Status status = statusService.create(false, false);
                Subscription subscription = subscriptionService.create(0, 0, 0, false, false, member, status, association, Collections.emptySet());
                subscriptionService.notifyWelcome(subscription, null, null);
                request.getSession().setAttribute(constantProperties.getAttributeNameSubscription(), subscription);
                return constantProperties.getControllerIndex();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationNotExist());
                return constantProperties.getControllerSignupMember();
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescEmailExist());
            return constantProperties.getControllerSignupMember();
        }
    }

    @GetMapping("/signupsuperadmin")
    public String signupSuperAdmin() {
        return constantProperties.getControllerSignupSuperAdmin();
    }

    @PostMapping("/signupsuperadminconfirm")
    public String signupSuperAdminConfirm(
            @RequestParam(name = "associationName") String associationName,
            @RequestParam(name = "associationDescription") String associationDescription,
            @RequestParam(name = "associationPrice1Month") String association1MonthPrice,
            @RequestParam(name = "associationPrice3Months") String association3MonthPrice,
            @RequestParam(name = "associationPrice12Months") String association12MonthPrice,
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "sex") String sex,
            @RequestParam(name = "birthDate") String birthDate,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "postalCode") String postalCode,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            HttpServletRequest request,
            Model model) {
        if (associationName.isEmpty()
                || associationDescription.isEmpty()
                || association1MonthPrice.isEmpty()
                || association3MonthPrice.isEmpty()
                || association12MonthPrice.isEmpty()
                || firstName.isEmpty()
                || lastName.isEmpty()
                || sex.isEmpty()
                || birthDate.isEmpty()
                || address.isEmpty()
                || city.isEmpty()
                || postalCode.isEmpty()
                || email.isEmpty()
                || phoneNumber.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFulfillFields());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(associationName).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationName());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(associationDescription).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationDescription());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getPrice()).matcher(association1MonthPrice).find()
                || !Pattern.compile(regexService.getPrice()).matcher(association3MonthPrice).find()
                || !Pattern.compile(regexService.getPrice()).matcher(association12MonthPrice).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPrice());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(firstName).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFirstname());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(lastName).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLastname());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getSex()).matcher(sex).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSex());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getBirthDate()).matcher(birthDate).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescBirthdate());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getAddress()).matcher(address).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAddress());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getWord()).matcher(city).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCity());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getPostalCode()).matcher(postalCode).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPostalCode());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getEmail()).matcher(email).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescEmail());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!Pattern.compile(regexService.getPhoneNumber()).matcher(phoneNumber).find()) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPhoneNumber());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPassword());
            return constantProperties.getControllerSignupSuperAdmin();
        }
        Member superAdmin = memberService.getByEmail(email);
        if (superAdmin == null) {
            Association association = associationService.getByName(associationName);
            if (association == null) {
                superAdmin = memberService.create(firstName, lastName, sex, birthDate, address, city, postalCode, email, phoneNumber, password);
                Status status = statusService.create(true, true);
                association = associationService.create(associationName, associationDescription, association1MonthPrice, association3MonthPrice, association12MonthPrice);
                Subscription subscription = subscriptionService.create(System.currentTimeMillis(), System.currentTimeMillis() + (60 * 1000), 0, false, false, superAdmin, status, association, Collections.emptySet());
                // Subscription subscription = subscriptionService.create(System.currentTimeMillis(), System.currentTimeMillis() + ((31556952L / 12) * 1000), 0, false, false, superAdmin, status, association, Collections.emptySet());
                subscriptionService.notifyWelcome(subscription, null, null);
                request.getSession().setAttribute(constantProperties.getAttributeNameSubscription(), subscription);
                return constantProperties.getControllerIndex();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationExist());
                return constantProperties.getControllerSignupSuperAdmin();
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescEmailExist());
            return constantProperties.getControllerSignupSuperAdmin();
        }
    }
}
