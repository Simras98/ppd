package com.uparis.ppd.controller;

import com.uparis.ppd.model.Transaction;
import com.uparis.ppd.model.User;
import com.uparis.ppd.service.TransactionService;
import com.uparis.ppd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

@Controller
public class TransactionController {

  @Autowired private TransactionService transactionService;

  @Autowired private UserService userService;

  @Value("${controller.index}")
  private String index;

  @Value("${controller.login}")
  private String login;

  @Value("${controller.billing}")
  private String billing;

  @PostMapping("/billingconfirm")
  public String billing(
      @RequestParam(name = "creditCard") String creditCard,
      @RequestParam(name = "expirationDate") String expirationDate,
      @RequestParam(name = "cryptogram") String cryptogram,
      @RequestParam(name = "duration") String duration,
      HttpServletRequest request,
      Model model) {
    User user = (User) request.getSession().getAttribute("user");

    if (user != null) {
      Date date = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      calendar.add(Calendar.MONTH, Integer.parseInt(duration));
      double price;
      switch (duration) {
        case "1":
          price = 14.99;
          break;
        case "3":
          price = 42.99;
          break;
        case "12":
          price = 149.99;
          break;
        default:
          model.addAttribute("error", "Paiment invalide. Veuillez-contacter les administrateurs.");
          return billing;
      }
      transactionService.save(
          new Transaction(
              user, date.getTime(), "producer_subscription_" + duration + "_month", price));
      user.setStartSubscription(calendar.getTimeInMillis());
      user.setEndSubscription(calendar.getTimeInMillis()+1000);
      userService.save(user);
      // faire la vérif de validité
      // régler probleme affichage menu
      return index;
    } else {
      model.addAttribute("error", "Vous n'êtes pas connectés");
      return login;
    }
  }
}
