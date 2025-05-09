package com.example.demo.Repository;

import com.example.demo.Model.Product;
import com.example.demo.Model.Purchase;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Buscar reseñas de un producto
    Optional<Review> findByProductId(Product productId);
    List<Review> findByUser(User user);
}