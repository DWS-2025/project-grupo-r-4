package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Model.UserDTO;
import com.example.demo.Service.PurchaseService;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}/buys")
    public String showUserPurchase(@PathVariable Long id, Model model){
        UserDTO userDTO = userService.findByUserName("user");
        model.addAttribute("user", userDTO);
        return "buys";
    }
    @GetMapping("/user/{id}/reviews")
    public String showUserReviews(@PathVariable Long id, Model model) {
        UserDTO userDTO = userService.findByUserName("user");
        model.addAttribute("user", userDTO);
        return "myReview";
    }
}
