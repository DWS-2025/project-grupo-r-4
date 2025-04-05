package com.example.demo.Model;

public class ReviewDTO {

    private long reviewId;
    private long userId;
    private long productId;
    private int rating;
    private String review;


    public long getReviewId() {
        return reviewId;
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

    public static ReviewDTO fromEntity(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getReviewId());
        reviewDTO.setUserId(review.getUser().getId());
        reviewDTO.setProductId(review.getProduct().getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReview(reviewDTO.getReview());
        return reviewDTO;
    }
}

