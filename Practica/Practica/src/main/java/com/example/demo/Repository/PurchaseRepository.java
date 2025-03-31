package com.example.demo.Repository;

import com.example.demo.Model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // Buscar compras de un usuario
    Optional<Purchase> findPurchasesByUserId(Long userId);

    // Buscar compras asociadas a un producto
    Optional<Purchase> findPurchasesById(Long productId);
}