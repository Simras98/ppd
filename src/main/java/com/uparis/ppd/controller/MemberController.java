package com.uparis.ppd.controller;

import com.google.re2j.Pattern;
import com.uparis.ppd.service.MemberService;
import com.uparis.ppd.service.RegexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RegexService regexService;

    @Value("${controller.addmember}")
    private String addMember;

    @Value("${controller.addmemberconfirm}")
    private String addMemberConfirm;

    @Value("${controller.error}")
    private String error;

    @GetMapping("/addmember")
    public String addMember() {
        return addMember;
    }

    @PostMapping("/addmemberconfirm")
    public String addMemberConfirm(
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "postalCode") String postalCode,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phoneNumber") String phoneNumber,
            HttpServletRequest request,
            Model model) {
        model.addAttribute(error, "");
        if (firstName.isEmpty()
                || lastName.isEmpty()
                || address.isEmpty()
                || city.isEmpty()
                || postalCode.isEmpty()
                || email.isEmpty()
                || phoneNumber.isEmpty()) {
            model.addAttribute(error, "Vous devez remplir les champs pour valider !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getWord()).matcher(firstName).find()) {
            model.addAttribute(error, "Vous devez rentrer un prénom valide !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getWord()).matcher(lastName).find()) {
            model.addAttribute(error, "Vous devez rentrer un nom valide !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getAddress()).matcher(address).find()) {
            model.addAttribute(error, "Vous devez rentrer une adresse valide !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getWord()).matcher(city).find()) {
            model.addAttribute(error, "Vous devez rentrer une ville valide !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getPostalCode()).matcher(postalCode).find()) {
            model.addAttribute(error, "Vous devez rentrer un code postal valide !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getEmail()).matcher(email).find()) {
            model.addAttribute(error, "Vous devez rentrer une adresse mail valide !");
            return addMember;
        }
        if (!Pattern.compile(regexService.getPhoneNumber()).matcher(phoneNumber).find()) {
            model.addAttribute(error, "Vous devez rentrer un numéro de téléphone valide !");
            return addMember;
        }
        memberService.notifyMember(firstName, lastName, address, city, postalCode, email, phoneNumber);
        return addMemberConfirm;
    }

    @PostMapping("/addmemberwithfileconfirm")
    public String addMemberWithFileConfirm(
            @RequestParam(name = "memberlist") MultipartFile file, HttpServletRequest request, Model model) {
        if (Objects.equals(file.getOriginalFilename(), "")) {
            model.addAttribute("error", "Vous devez choisir un fichier avant de valider !");
            return addMember;
        } else {
            model.addAttribute("error", "");
            memberService.addMembers(file);
        }
        return addMemberConfirm;
    }
}
