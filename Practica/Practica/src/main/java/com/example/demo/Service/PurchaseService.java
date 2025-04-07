package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.PurchaseRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;


    private PurchaseDTO convertToDTO(Purchase purchase) {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(purchase.getId());
        purchaseDTO.setPrice(purchase.getPrice());

        purchaseDTO.setUserId(1L);

        purchaseDTO.setProductIds(purchase.getProducts().stream().map(Product::getId).toList());
        return purchaseDTO;
    }


    private Purchase convertToEntity(PurchaseDTO purchaseDTO) {
        Purchase purchase = new Purchase();
        purchase.setPrice(purchaseDTO.getPrice());
        purchase.setId(purchaseDTO.getId());
        if(userService.findById(purchaseDTO.getUserId()).isPresent()){
            purchase.setUserId(userService.convertToEntity(userService.findById(purchaseDTO.getUserId()).get()));
        }
        for (Long productId : purchaseDTO.getProductIds()) {
            purchase.getProducts().add(productService.convertToEntity2(productService.findById(productId).get()));
        }
        return purchase;
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


    public void createPurchase(PurchaseDTO purchaseDTO, ProductDTO productDTO) throws IOException {
        if (purchaseDTO.getProductIds() == null) {
            purchaseDTO.setProductIds(new ArrayList<>());
        }
        if (productDTO.getPurchasesId() == null) {
            productDTO.setPurchasesId(new ArrayList<>());
        }
        purchaseDTO.getProductIds().add(productDTO.getId());
        productDTO.getPurchasesId().add(purchaseDTO.getId());
        save(purchaseDTO);
    }

    public List<PurchaseDTO> findByUser(UserDTO userDTO) {
        User user = userRepository.findByName(userDTO.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return purchaseRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
}
