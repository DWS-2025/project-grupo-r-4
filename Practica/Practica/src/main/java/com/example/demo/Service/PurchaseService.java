package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Model.Purchase;
import com.example.demo.Model.User;
import com.example.demo.Repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collection;

@Service

public class PurchaseService {
    @Autowired
    private ProductService productService;


    @Autowired
    private UserService userService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    public Collection<Purchase> findAll(String username) {
        User user = userService.findByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        return purchaseRepository.findByUser(user);
    }

    public Purchase findById(long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));
    }

    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public void deleteById(long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada");
        }
        purchaseRepository.deleteById(id);
    }

    public void createPurchase(long productId, String username) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        User user = userService.findByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        double price = product.getPrice();
        Purchase purchase = new Purchase(user, price);
        purchaseRepository.save(purchase);


        product.getPurchase().add(purchase);
        purchase.getProducts().add(product);
        user.getProducts().add(purchase);
        user.getProductList().add(product);
        purchase.setUser(user);
        product.getUsers().add(user);


        purchaseRepository.save(purchase);
    }
}