package com.uparis.ppd.controller;

import com.uparis.ppd.model.Member;
import com.uparis.ppd.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AssoController {

    @Autowired
    private MemberService memberService;

    @Value("${controller.billing}")
    private String billing;

    @Value("${controller.error}")
    private String error;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        model.addAttribute(error, "");
        Member member = (Member) request.getSession().getAttribute("member");
        if (member != null && !memberService.checkSubscription(member)) {
            model.addAttribute(error, "Votre abonnement a expiré. Veuillez renouveler votre abonnement !");
            return billing;
        }
        return "index";
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        model.addAttribute(error, "");
        Member member = (Member) request.getSession().getAttribute("member");
        if (member != null && !memberService.checkSubscription(member)) {
            model.addAttribute(error, "Votre abonnement a expiré. Veuillez renouveler votre abonnement !");
            return billing;
        }
        return "index";
    }
}
