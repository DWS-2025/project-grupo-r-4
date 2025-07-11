package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.ReviewRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public ReviewDTO convertToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getReviewId());
        reviewDTO.setUserId(review.getUser().getId());
        reviewDTO.setProductId(review.getProduct().getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReview(review.getReview());
        return reviewDTO;
    }

    public Review convertToEntity(ReviewDTO reviewDTO) {
        // Buscar usuario y producto por sus IDs
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Crear la entidad Review
        Review review = new Review();
        review.setReviewId(reviewDTO.getReviewId());
        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewDTO.getRating());
        review.setReview(reviewDTO.getReview());

        return review;
    }




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
        return convertToDTO(review);
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


        return convertToDTO(review);
    }


    public void deleteById(long id, String username) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));

        User authUser = userRepository.findByName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!review.getUser().getName().equals(username) && !authUser.getRoles().contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta reseña");
        }

        Product product = review.getProduct();
        User user = review.getUser();

        product.getReviews().remove(review);
        user.getReviews().remove(review);
        user.setNumReviews(user.getNumReviews() - 1);

        reviewRepository.deleteById(id);
        productRepository.save(product);
        userRepository.save(user);
    }



    public List<ReviewDTO> findByUser(UserDTO userDTO) {
        User user = userRepository.findByName(userDTO.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return reviewRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
}
