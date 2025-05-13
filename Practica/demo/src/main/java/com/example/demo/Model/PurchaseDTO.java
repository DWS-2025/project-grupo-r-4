package com.example.demo.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseDTO {

    private long id;
    private long userId;
    private List<Long> productIds;
    private double price;
    private List<ProductDTO> products = new ArrayList<>();

    public PurchaseDTO() {
        this.productIds = new ArrayList<>();
    }


    public PurchaseDTO(long id, long userId, List<Long> productIds, double price) {
        this.id = id;
        this.userId = userId;
        this.productIds = productIds;
        this.price = price;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static PurchaseDTO fromEntity(Purchase purchase) {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(purchase.getId());
        purchaseDTO.setUserId(purchase.getUser().getId());
        purchaseDTO.setPrice(purchase.getPrice());
        purchaseDTO.setProductIds(purchase.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList()));
        return purchaseDTO;
    }

    public static Purchase toEntity(PurchaseDTO purchaseDTO, User user, List<Product> products) {
        Purchase purchase = new Purchase();
        purchase.setId(purchaseDTO.getId());
        purchase.setPrice(purchaseDTO.getPrice());
        purchase.setUser(user);
        purchase.setProducts(products);
        return purchase;
    }
}
