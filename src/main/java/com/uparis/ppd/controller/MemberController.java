package com.uparis.ppd.controller;

import com.google.re2j.Pattern;
import com.uparis.ppd.model.Member;
import com.uparis.ppd.service.FileSystemStorageService;
import com.uparis.ppd.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @Value("${controller.addmember}")
    private String addMembers;

    @Value("${controller.addmembersconfirm}")
    private String addMembersConfirm;

    @Value("${controller.error}")
    private String error;

    @GetMapping("/addmember")
    public String addMember() {
        return addMembers;

    }

    @PostMapping("/addmembersconfirm")
    public String addMembersConfirm(
            @RequestParam(name = "memberlist") MultipartFile file, HttpServletRequest request, Model model) {
        if (Objects.equals(file.getOriginalFilename(), "")) {
            model.addAttribute("error", "Vous devez choisir un fichier avant de valider !");
            return addMembers;
        } else {
            model.addAttribute("error", "");
            String filename = fileSystemStorageService.store(file);
            List<Member> listMembers = productService.getAllProductByCustomerId(customer.getId());
        }
        return profile;
    } else

    {
        model.addAttribute("error", "Vous n'avez pas l'autorisation !");
        return error;
    }
}
}
