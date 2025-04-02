package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.ReviewRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private AtomicLong nextId = new AtomicLong();

    public Optional<Review> findById(long id) {
        return reviewRepository.findById(id);
    }

    public Review save(Review review) {
        long id = nextId.getAndIncrement();
        review.setId(id);
        return reviewRepository.save(review);
    }

    public void createReview(User user, Product product, int rating, String reviewText) {
        Review review = new Review(user, product, rating, reviewText);
        reviewRepository.save(review);

        product.getReviews().add(review);
        user.getReviews().add(review);
        user.setNumReviews(user.getNumReviews() + 1);

        productRepository.save(product);
        userRepository.save(user);
    }

    public void deleteById(long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada");
        }
        reviewRepository.deleteById(id);
    }
}
