package com.uparis.ppd.controller;

import com.google.re2j.Pattern;
import com.uparis.ppd.model.Subscription;
import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.service.RegexService;
import com.uparis.ppd.service.SubscriptionService;
import com.uparis.ppd.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TransactionController {

    @Autowired
    private ConstantProperties constantProperties;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RegexService regexService;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/billingmember")
    public String billingMember() {
        return constantProperties.getControllerBillingMember();
    }

    @PostMapping("/billingmemberconfirm")
    public String billingMemberConfirm(
            @RequestParam(name = "creditCard") String creditCard,
            @RequestParam(name = "expirationDate") String expirationDate,
            @RequestParam(name = "cryptogram") String cryptogram,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "duration") String duration,
            HttpServletRequest request,
            Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (creditCard.isEmpty() || expirationDate.isEmpty() || cryptogram.isEmpty() || name.isEmpty()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFulfillFields());
                return constantProperties.getControllerBillingMember();
            }
            if (!Pattern.compile(regexService.getCreditCard()).matcher(creditCard).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCreditCard());
                return constantProperties.getControllerBillingMember();
            }
            if (!Pattern.compile(regexService.getExpirationDate()).matcher(expirationDate).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescExpirationDate());
                return constantProperties.getControllerBillingMember();
            }
            if (!Pattern.compile(regexService.getCryptogram()).matcher(cryptogram).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCryptogram());
                return constantProperties.getControllerBillingMember();
            }
            if (!Pattern.compile(regexService.getWord()).matcher(name).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescName());
                return constantProperties.getControllerBillingMember();
            }
            if (!transactionService.checkExpirationDate(expirationDate)) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescExpirationDateExpired());
                return constantProperties.getControllerBillingMember();
            }
            if (subscriptionService.subscribe(subscription, duration)) {
                request.getSession().setAttribute(constantProperties.getAttributeNameSubscription(), subscription);
                model.addAttribute(constantProperties.getAttributeNamePage(), constantProperties.getControllerDashboard());
                request.getSession().setAttribute(constantProperties.getAttributeNameDate(), subscriptionService.convertLongToDateString(subscription));
                request.getSession().setAttribute(constantProperties.getAttributeNameMembers(), subscriptionService.getMembersByAssociation(subscription));
                request.getSession().setAttribute(constantProperties.getAttributeNameMembersInMonth(), subscriptionService.getMembersByAssociationInLastMonth(subscription));
                return constantProperties.getControllerDashboard();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPaymentFailed());
                return constantProperties.getControllerBillingMember();
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @GetMapping("/billingsuperadmin")
    public String billingSuperAdmin() {
        return constantProperties.getControllerBillingSuperAdmin();
    }

    @PostMapping("/billingsuperadminconfirm")
    public String billingSuperAdminConfirm(
            @RequestParam(name = "creditCard") String creditCard,
            @RequestParam(name = "expirationDate") String expirationDate,
            @RequestParam(name = "cryptogram") String cryptogram,
            @RequestParam(name = "name") String name,
            HttpServletRequest request,
            Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (creditCard.isEmpty() || expirationDate.isEmpty() || cryptogram.isEmpty() || name.isEmpty()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescFulfillFields());
                return constantProperties.getControllerBillingSuperAdmin();
            }
            if (!Pattern.compile(regexService.getCreditCard()).matcher(creditCard).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCreditCard());
                return constantProperties.getControllerBillingSuperAdmin();
            }
            if (!Pattern.compile(regexService.getExpirationDate()).matcher(expirationDate).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescExpirationDate());
                return constantProperties.getControllerBillingSuperAdmin();
            }
            if (!Pattern.compile(regexService.getCryptogram()).matcher(cryptogram).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescCryptogram());
                return constantProperties.getControllerBillingSuperAdmin();
            }
            if (!Pattern.compile(regexService.getWord()).matcher(name).find()) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescName());
                return constantProperties.getControllerBillingSuperAdmin();
            }
            if (!transactionService.checkExpirationDate(expirationDate)) {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescExpirationDateExpired());
                return constantProperties.getControllerBillingSuperAdmin();
            }
            if (subscriptionService.subscribe(subscription, null)) {
                request.getSession().setAttribute(constantProperties.getAttributeNameSubscription(), subscription);
                model.addAttribute(constantProperties.getAttributeNamePage(), constantProperties.getControllerDashboard());
                request.getSession().setAttribute(constantProperties.getAttributeNameDate(), subscriptionService.convertLongToDateString(subscription));
                request.getSession().setAttribute(constantProperties.getAttributeNameMembers(), subscriptionService.getMembersByAssociation(subscription));
                request.getSession().setAttribute(constantProperties.getAttributeNameMembersInMonth(), subscriptionService.getMembersByAssociationInLastMonth(subscription));
                return constantProperties.getControllerDashboard();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescPaymentFailed());
                return constantProperties.getControllerBillingSuperAdmin();
            }
        } else {
            model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
            return constantProperties.getControllerLogin();
        }
    }

    @PostMapping("/billingskip")
    public String billingSkip(HttpServletRequest request, Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            subscriptionService.skipSubscription(subscription);
            model.addAttribute(constantProperties.getAttributeNameSubscription(), subscription);
            request.getSession().setAttribute(constantProperties.getAttributeNameDate(), subscriptionService.convertLongToDateString(subscription));
            request.getSession().setAttribute(constantProperties.getAttributeNameMembers(), subscriptionService.getMembersByAssociation(subscription));
            request.getSession().setAttribute(constantProperties.getAttributeNameMembersInMonth(), subscriptionService.getMembersByAssociationInLastMonth(subscription));
            return constantProperties.getControllerDashboard();
        }
        model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescLogout());
        return constantProperties.getControllerLogin();
    }
}
