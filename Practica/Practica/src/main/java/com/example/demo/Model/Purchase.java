package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class Purchase {
    public interface Basic{}
    public interface Products{}
    @JsonView(Basic.class)
    private long id;
    private User user;
    @JsonView(Products.class)
    private List<Product> products;
    @JsonView(Basic.class)
    private double price;

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
        return products;
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
