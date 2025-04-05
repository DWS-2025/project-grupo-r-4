package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Model.Review;
import com.example.demo.Model.ReviewDTO;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.ReviewRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    public List<ReviewDTO> findReviewsByProductId(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        List<Review> reviews = product.getReviews();
        return reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
    }


    public ReviewDTO findById(long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));
        return ReviewDTO.fromEntity(review);
    }


    public ReviewDTO save(ReviewDTO reviewDTO) {

        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));


        Review review = new Review(user, product, reviewDTO.getRating(), reviewDTO.getReview());


        review = reviewRepository.save(review);


        product.getReviews().add(review);
        user.getReviews().add(review);
        user.setNumReviews(user.getNumReviews() + 1);


        productRepository.save(product);
        userRepository.save(user);


        return ReviewDTO.fromEntity(review);
    }


    public void deleteById(long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada");
        }


        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));

        Product product = review.getProduct();
        User user = review.getUser();


        product.getReviews().remove(review);
        user.getReviews().remove(review);
        user.setNumReviews(user.getNumReviews() - 1);


        reviewRepository.deleteById(id);


        productRepository.save(product);
        userRepository.save(user);
    }
}
