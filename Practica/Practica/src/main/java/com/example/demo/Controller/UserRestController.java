package com.example.demo.Controller;


import com.example.demo.Model.Product;
import com.example.demo.Model.Purchase;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {
    interface UserRev extends User.RevInt, Review.Basic,Review.Products,Product.Basic{}
    interface UserPurch extends User.PurchInt,Purchase.Basic,Purchase.Products,Product.Basic{}

    @Autowired
    private UserService userService;

    @GetMapping("/users/")
    public List<User> getAllUsers() {
        return userService.findAll().stream().toList();
    }

    @JsonView(UserPurch.class)
    @GetMapping("/user/{id}/buys")
    public ResponseEntity<List<Purchase>> getUserPurchase(@PathVariable Long id) {
        User user = userService.findByUserName("user");
        if (user != null) {
            return ResponseEntity.ok(user.getProducts());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @JsonView(UserRev.class)
    @GetMapping("/user/{id}/reviews")
    public ResponseEntity<User> getUserReview() {
        User user = userService.findByUserName("user");
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
