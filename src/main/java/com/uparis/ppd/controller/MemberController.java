package com.uparis.ppd.controller;

import com.google.re2j.Pattern;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.model.Status;
import com.uparis.ppd.model.Subscription;
import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.service.MemberService;
import com.uparis.ppd.service.RegexService;
import com.uparis.ppd.service.StatusService;
import com.uparis.ppd.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
public class MemberController {

    @Autowired
    private ConstantProperties constantProperties;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RegexService regexService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/managemembers")
    public String manageMembers(HttpServletRequest request, Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                Object[] members = subscriptionService.getMembersByAssociation(subscription);
                model.addAttribute(constantProperties.getAttributeNameMembers(), members);
                return constantProperties.getControllerManageMembers();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscriptionService.getStatusSuperAdmin(subscription)) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @PostMapping("/addmembersconfirm")
    public String addMemberConfirm(
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "man") String man,
            @RequestParam(name = "birthDate") String birthDate,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "postalCode") String postalCode,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "admin") String isAdmin,
            @RequestParam(name = "associationName") String associationName,
            HttpServletRequest request, Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                if (firstName.isEmpty()
                        || lastName.isEmpty()
                        || man.isEmpty()
                        || birthDate.isEmpty()
                        || address.isEmpty()
                        || city.isEmpty()
                        || postalCode.isEmpty()
                        || email.isEmpty()
                        || phoneNumber.isEmpty()
                        || isAdmin.isEmpty()
                        || associationName.isEmpty()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFulfillFields());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getWord()).matcher(firstName).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFirstname());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getWord()).matcher(lastName).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLastname());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getGender()).matcher(man).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLastname());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getBirthDate()).matcher(birthDate).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescBirthdate());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getAddress()).matcher(address).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAddress());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getWord()).matcher(city).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCity());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getPostalCode()).matcher(postalCode).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPostalCode());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getEmail()).matcher(email).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescEmail());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getPhoneNumber()).matcher(phoneNumber).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPhoneNumber());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getLevel()).matcher(isAdmin).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescStatus());
                    return constantProperties.getControllerManageMembers();
                }
                if (!Pattern.compile(regexService.getWord()).matcher(associationName).find()) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescAssociationName());
                    return constantProperties.getControllerManageMembers();
                }
                String password = memberService.createRandomPassword();
                Member member = memberService.create(firstName, lastName, man, birthDate, address, city, postalCode, email, phoneNumber, password);
                Status status = statusService.create(Boolean.parseBoolean(isAdmin), false);
                Subscription newSubscription = subscriptionService.create(0, 0, 0, false, false, member, status, subscription.getAssociation(), Collections.emptySet());
                subscriptionService.notifyWelcome(newSubscription, subscription.getMember(), password);
                model.addAttribute(constantProperties.getAttributeNameSuccess(), constantProperties.getAttributeDescMemberAdded());
                return constantProperties.getControllerManageMembers();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscriptionService.getStatusSuperAdmin(subscription)) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @PostMapping("/addmembersfromfileconfirm")
    public String addMemberFromFileConfirm(
            @RequestParam(name = "file") MultipartFile file,
            HttpServletRequest request,
            Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                if (Objects.equals(file.getOriginalFilename(), "")) {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescChooseFile());
                    return constantProperties.getControllerManageMembers();
                }
                List<String> passwords = memberService.createRandomPasswords(memberService.getNumberOfPasswords(file));
                List<Member> members = memberService.createFromFile(file, passwords);
                List<Status> status = statusService.createFromFile(file);
                for (int i = 0; i < members.size(); i++) {
                    Subscription newSubscription = subscriptionService.create(0, 0, 0, false, false, members.get(i), status.get(i), subscription.getAssociation(), Collections.emptySet());
                    subscriptionService.notifyWelcome(newSubscription, subscription.getMember(), passwords.get(i));
                }
                model.addAttribute(constantProperties.getAttributeNameSuccess(), constantProperties.getAttributeDescMembersAdded());
                return constantProperties.getControllerManageMembers();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscriptionService.getStatusSuperAdmin(subscription)) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @GetMapping(value = "receivemembersfromfile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<ByteArrayResource> receiveMembersFromFile(HttpServletRequest request) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        final ByteArrayOutputStream in = subscriptionService.exportMembers(subscription);
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + subscription.getAssociation().getName() + "Members.xlsx");
        return new HttpEntity<>(new ByteArrayResource(in.toByteArray()), header);
    }

    @GetMapping("/managepayments")
    public String managePayments(HttpServletRequest request, Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                Object[] transactions = subscriptionService.getTransactions(subscription);
                model.addAttribute(constantProperties.getAttributeNameTransactions(), transactions);
                return constantProperties.getControllerManagePayments();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscriptionService.getStatusSuperAdmin(subscription)) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                return constantProperties.getControllerProfile();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscriptionService.getStatusSuperAdmin(subscription)) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @PostMapping("/passwordedit")
    public String passwordEdit(
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "confirmNewPassword") String confirmNewPassword,
            HttpServletRequest request,
            Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                boolean confirm = memberService.editPassword(subscription.getMember().getEmail(), oldPassword, newPassword, confirmNewPassword);
                if (confirm) {
                    model.addAttribute(constantProperties.getAttributeNameSuccess(), constantProperties.getAttributeDescPasswordChanged());
                } else {
                    model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescError());
                }
                return constantProperties.getControllerProfile();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscriptionService.getStatusSuperAdmin(subscription)) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }
}
