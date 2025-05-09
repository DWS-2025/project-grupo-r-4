package com.example.demo.Repository;

import com.example.demo.Model.Product;
import com.example.demo.Model.ProductDTO;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Buscar productos por nombre
    Optional<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByTypeIgnoreCase(String type);

    boolean existsByName(String name);

    @Query(value = "SELECT * FROM products ORDER BY id LIMIT ?2 OFFSET ?1",
            nativeQuery = true)
    List<Product> findPaginatedProducts(int offset, int size);

}
