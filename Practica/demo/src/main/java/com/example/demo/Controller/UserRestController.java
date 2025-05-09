package com.example.demo.Controller;


import com.example.demo.Model.*;
import com.example.demo.Service.PurchaseService;
import com.example.demo.Service.ReviewService;
import com.example.demo.Service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private User user;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/users/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/user/{id}/buys")
    public ResponseEntity<List<PurchaseDTO>> getUserPurchases(@PathVariable Long id) {
        Optional<UserDTO> user = userService.findById(id);
        List<PurchaseDTO> purchases = purchaseService.findByUser(user.get());
        return ResponseEntity.ok(purchases);
    }


    @GetMapping("/user/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long id) {
        Optional<UserDTO> user = userService.findById(id);
        List<ReviewDTO> reviewDTOS = reviewService.findByUser(user.get());
        return ResponseEntity.ok(reviewDTOS);
    }
}