package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class User {
    public interface RevInt{}
    public interface UserInfo{}
    public interface PurchInt{}
    @JsonView(UserInfo.class)
    private long id;
    @JsonView(UserInfo.class)
    private String name;
    @JsonView(UserInfo.class)
    private String password;
    @JsonView(UserInfo.class)
    private String address;
    @JsonView(UserInfo.class)
    private String phone;
    @JsonView(UserInfo.class)
    private int numReviews;

    @JsonView(RevInt.class)
    private List<Review> reviews;
    @JsonView(PurchInt.class)
    private List<Purchase> products;
    private List<Product> productList;

    public User(String name) {
        this.name = name;
        this.products=new ArrayList<>();
        this.reviews=new ArrayList<>();
        this.productList= new ArrayList<>();
    }
    public User(){
        this.reviews = new ArrayList<>();
        this.products = new ArrayList<>();
        this.productList= new ArrayList<>();
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getNumReviews() {
        return this.numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public List<Review> getReviews(){
        return reviews;
    }

    public void setReviews(List<Review> reviews){
        this.reviews = reviews;
    }

    public List<Purchase> getProducts(){
        return products;
    }

    public void setProducts(List<Purchase> products){
        this.products = products;
    }

    public void addProduct(Purchase product){
        this.products.add(product);
    }

    public void incNumReviews() {
        this.numReviews++;
    }
    @Override
    public String toString() {
        return "Post [user=" + name + ", password=" + password + ", address=" + address + ", phone=" + phone + ", numReviews=" + numReviews + "]";
    }
}
