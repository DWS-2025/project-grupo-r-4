package com.example.demo.Model;

public class ReviewDTO {

    private long reviewId;
    private long userId;
    private long productId;
    private int rating;
    private String review;

    private String productName;
    private String userName;

    public ReviewDTO(long userId, long productId, int rating, String review) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
    }

    public ReviewDTO() {

    }

    public long getReviewId() {
        return this.reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }


    public static ReviewDTO fromEntity(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getReviewId());
        reviewDTO.setUserId(review.getUser().getId());
        reviewDTO.setProductId(review.getProduct().getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReview(review.getReview());
        reviewDTO.setProductName(review.getProduct().getName());
        reviewDTO.setUserName(review.getUser().getName());
        return reviewDTO;
    }

}

