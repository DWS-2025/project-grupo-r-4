package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Review {


    public interface Basic{}
    public interface Products{}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewId;
    @JsonView(Products.class)
    @ManyToOne
    private Product productId;
    @JsonView (Basic.class)
    private int rating;
    @JsonView (Products.class)
    private String review;
    @JsonView (Products.class)
    @ManyToOne
    private User user;

    public Review() {

    }

    public Review(User user, Product productId, int rating, String review) {
        this.user = user;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public User getUserId() {
        return user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return review;
    }

    public void setComment(String comment) {
        this.review = comment;
    }

    public Product getProduct() {
        return productId;
    }

    public void setProduct(Product product) {
        this.productId = product;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    @Override
    public String toString() {
        return "Review [reviewId=" + reviewId + ", user=" + user + ", productId=" + productId + ", rating=" + rating + ", review=" + review + "]";
    }

    public void setId(long id) {this.reviewId = id;}
}
