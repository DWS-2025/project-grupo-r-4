package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Component
@SessionScope
public class User {
    public interface RevInt{}
    public interface UserInfo{}
    public interface PurchInt{}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UserInfo.class)
    private long id;
    @JsonView(UserInfo.class)
    @Column(unique = true)
    private String name;
    @JsonView(UserInfo.class)
    private String encodedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @JsonView(UserInfo.class)
    private String address;
    @JsonView(UserInfo.class)
    private String phone;
    @JsonView(UserInfo.class)
    private int numReviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonView(RevInt.class)
    private List<Review> reviews;
    @JsonView(PurchInt.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Purchase> products;
    @JsonView(PurchInt.class)
    @ManyToMany
    private List<Product> productList;

    public User(String name, String encodedPassword, String... roles){
        this.name = name;
        this.encodedPassword = encodedPassword;
        this.roles = List.of(roles);
        this.reviews = new ArrayList<>();
    }

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

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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
        return "Post [user=" + name + ", password=" + encodedPassword + ", address=" + address + ", phone=" + phone + ", numReviews=" + numReviews + "]";
    }
}

