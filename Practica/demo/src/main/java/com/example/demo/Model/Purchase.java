package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
public class Purchase {

    public interface Basic{}
    public interface Products{}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Basic.class)
    private long id;
    @ManyToOne
    @JsonView(Products.class)
    private User user;
    @JsonView(Products.class)
    @ManyToMany
    private List<Product> products;
    @JsonView(Basic.class)
    private double price;

    public Purchase() {
        products = new ArrayList<>();
    }

    public Purchase(User userId, double price) {
        this.user = userId;
        this.price = price;
        this.products = new ArrayList<>();
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(User userId) {
        this.user = userId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    @Override
    public String toString() {
        return "Post [id=" + id + ", user=" + user + ", price=" + price + "]";
    }

}
