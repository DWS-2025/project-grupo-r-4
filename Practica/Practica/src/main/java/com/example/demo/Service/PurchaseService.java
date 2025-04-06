package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.PurchaseRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    private PurchaseDTO convertToDTO(Purchase purchase) {
        return PurchaseDTO.fromEntity(purchase);
    }


    private Purchase convertToEntity(PurchaseDTO purchaseDTO) {
        User user = userRepository.findById(purchaseDTO.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<Product> products = productRepository.findAllById(purchaseDTO.getProductIds());
        if (products.size() != purchaseDTO.getProductIds().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uno o más productos no encontrados");
        }

        return PurchaseDTO.toEntity(purchaseDTO, user, products);
    }


    public List<PurchaseDTO> findAll() {
        List<Purchase> purchases = purchaseRepository.findAll();
        return purchases.stream()
                .map(this::convertToDTO)
                .toList();
    }


    public PurchaseDTO findById(long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));
        return convertToDTO(purchase);
    }


    public PurchaseDTO save(PurchaseDTO purchaseDTO) {
        Purchase purchase = convertToEntity(purchaseDTO);
        purchase = purchaseRepository.save(purchase);
        return convertToDTO(purchase);
    }


    public void deleteById(long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada");
        }
        purchaseRepository.deleteById(id);
    }


    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        return save(purchaseDTO);
    }

    public List<PurchaseDTO> findByUser(UserDTO userDTO) {
        User user = userRepository.findByName(userDTO.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return purchaseRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
}
