package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class Product {
    public interface Basic{}
    public interface ProdRev{}
    public interface ProdPurch{}
    public interface ProdUser{}
    @JsonView(Basic.class)
    private long id;
    @JsonView (Basic.class)
    private String name;
    @JsonView (Basic.class)
    private double price;
    private String image;
    @JsonView(Basic.class)
    private String description;
    @JsonView(Basic.class)
    private String type;
    @JsonView(ProdRev.class)
    private List<Review> reviews;
    @JsonView(ProdUser.class)
    private List<User> users;
    @JsonView(ProdPurch.class)
    private List<Purchase> purchase;

    public Product(String name,double price,String description, String type) {
        super();
        this.name= name;
        this.price=price;
        this.description=description;
        this.type = type;
        this.users = new ArrayList<>();
        this.reviews= new ArrayList<>();
        this.purchase= new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProductType(String type) {
        this.type = type;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<User> getUsers(){
        return users;
    }

    public void setUsers(List<User> users){
        this.users = users;
    }

    public void addUser(User user){
        this.users.add(user);
    }

    public List<Purchase> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<Purchase> purchase) {
        this.purchase = purchase;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", name=" + name + ", price=" + price +  ", image=" + image + ", description=" + description + ", type=" + type + "]";
    }

}
