package com.example.demo.Repository;

import com.example.demo.Model.Purchase;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // Buscar compras de un usuario
    List<Purchase> findByUser(User user);

    // Buscar compras asociadas a un producto
    //List<Purchase> findByProducts_Id(Long productId);
}