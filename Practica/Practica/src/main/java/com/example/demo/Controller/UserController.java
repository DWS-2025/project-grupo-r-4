package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.PurchaseService;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/user/{id}/buys")
    public String showUserPurchase(Model model){
        User user = userService.findByUserName("user");
        model.addAttribute("user", user
        );

        return "buys";
    }
    @GetMapping("/user/{id}/reviews")
    public String showUserReview(Model model){
        User user = userService.findByUserName("user");
        model.addAttribute("user", user
        );

        return "myReview";
    }
}
