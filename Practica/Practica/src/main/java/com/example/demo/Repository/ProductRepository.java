package com.example.demo.Repository;

import com.example.demo.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Buscar productos por nombre
    Optional<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByTypeIgnoreCase(String type);

    boolean existsByName(String name);
}
