package com.uparis.ppd.controller;

import com.uparis.ppd.model.Subscription;
import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OurassoController {

    @Autowired
    private ConstantProperties constantProperties;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping({"/", "/index"})
    public String index(HttpServletRequest request, Model model) {
        Subscription subscription = (Subscription) request.getSession().getAttribute(constantProperties.getAttributeNameSubscription());
        if (subscription != null) {
            if (subscriptionService.isValid(subscription)) {
                return constantProperties.getControllerIndex();
            } else {
                model.addAttribute(constantProperties.getAttributeNameError(), constantProperties.getAttributeDescSubscriptionExpired());
                if (subscription.getStatus().isSuperAdmin()) {
                    model.addAttribute(constantProperties.getAttributeNamePrice(), subscriptionService.getPrice(subscription));
                    return constantProperties.getControllerBillingSuperAdmin();
                } else {
                    return constantProperties.getControllerBillingMember();
                }
            }
        } else {
            return constantProperties.getControllerIndex();
        }
    }
}
