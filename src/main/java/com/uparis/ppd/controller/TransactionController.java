package com.uparis.ppd.controller;

import com.google.re2j.Pattern;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.service.MemberService;
import com.uparis.ppd.service.RegexService;
import com.uparis.ppd.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RegexService regexService;

    @Value("${controller.index}")
    private String index;

    @Value("${controller.login}")
    private String login;

    @Value("${controller.billing}")
    private String billing;

    @Value("${controller.error}")
    private String error;

    @GetMapping("/billing")
    public String billing() {
        return billing;
    }

    @PostMapping("/billingconfirm")
    public String billingConfirm(
            @RequestParam(name = "creditCard") String creditCard,
            @RequestParam(name = "expirationDate") String expirationDate,
            @RequestParam(name = "cryptogram") String cryptogram,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "duration") String duration,
            HttpServletRequest request,
            Model model) {
        model.addAttribute(error, "");
        Member member = (Member) request.getSession().getAttribute("member");
        if (member != null) {
            if (creditCard.isEmpty()
                    || expirationDate.isEmpty()
                    || cryptogram.isEmpty()
                    || name.isEmpty()) {
                model.addAttribute(error, "Vous devez remplir les champs avant de valider !");
                return billing;
            }
            if (!Pattern.compile(regexService.getCreditCard()).matcher(creditCard).find()) {
                model.addAttribute(error, "Vous devez rentrer un numéro de carte bancaire valide !");
                return billing;
            }
            if (!Pattern.compile(regexService.getExpirationDate()).matcher(expirationDate).find()) {
                model.addAttribute(error, "Vous devez rentrer une date d'expiration valide !");
                return billing;
            }
            if (!Pattern.compile(regexService.getCryptogram()).matcher(cryptogram).find()) {
                model.addAttribute(error, "Vous devez rentrer un cryptogramme valide !");
                return billing;
            }
            if (!Pattern.compile(regexService.getWord()).matcher(name).find()) {
                model.addAttribute(error, "Vous devez rentrer un nom valide !");
                return billing;
            }
            if (!transactionService.checkExpirationDate(expirationDate)) {
                model.addAttribute(error, "Votre carte bancaire a exprirée !");
                return billing;
            }
            long time = System.currentTimeMillis();
            switch (duration) {
                case "1":
                    transactionService.create(member, time, 14.99);
                    member.setEndSubscription(time + ((31556952L / 12) * 1000));
                    break;
                case "3":
                    transactionService.create(member, time, 42.99);
                    member.setEndSubscription(time + ((3 * 31556952L / 12) * 1000));
                    break;
                case "12":
                    transactionService.create(member, time, 149.99);
                    member.setEndSubscription(time + ((12 * 31556952L / 12) * 1000));
                    break;
                default:
                    model.addAttribute(error, "Paiment invalide. Veuillez-contacter les administrateurs.");
                    return billing;
            }
            member.setStartSubscription(time);
            member.setDelaySubscription(0);
            member.setDelayedSubscription(false);
            memberService.update(member);
            return index;
        } else {
            model.addAttribute(error, "Vous n'êtes pas connecté");
            return login;
        }
    }

    @PostMapping("/billingskip")
    public String billingSkip(HttpServletRequest request, Model model) {
        model.addAttribute(error, "");
        Member member = (Member) request.getSession().getAttribute("member");
        if (member != null) {
            long time = System.currentTimeMillis();
            member.setDelaySubscription(time + (60*1000));
            // member.setDelaySubscription(time + ((31556952L / 365) * 7) * 1000);
            member.setDelayedSubscription(true);
            memberService.update(member);
            return index;
        }
        model.addAttribute(error, "Vous n'êtes pas connecté");
        return login;
    }
}
