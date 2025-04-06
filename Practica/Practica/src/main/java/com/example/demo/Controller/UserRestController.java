package com.example.demo.Controller;


import com.example.demo.Model.*;
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

    /*interface UserRev extends UserDTO.RevInt, ReviewDTO.Basic, ReviewDTO.Products, ProductDTO.Basic {}
    interface UserPurch extends UserDTO.PurchInt, PurchaseDTO.Basic, PurchaseDTO.Products, ProductDTO.Basic {}

    @Autowired
    private UserService userService;

    @GetMapping("/users/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @JsonView(UserPurch.class)
    @GetMapping("/user/{id}/buys")
    public ResponseEntity<List<PurchaseDTO>> getUserPurchases(@PathVariable Long id) {
        List<PurchaseDTO> purchases = userService.findUserPurchases(id);
        return ResponseEntity.ok(purchases);
    }

    @JsonView(UserRev.class)
    @GetMapping("/user/{id}/reviews")
    public ResponseEntity<UserDTO> getUserReviews(@PathVariable Long id) {
        UserDTO userDTO = userService.findUserWithReviews(id);
        return ResponseEntity.ok(userDTO);
    }*/
}