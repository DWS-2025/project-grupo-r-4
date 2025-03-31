package com.example.demo.Controller;


import com.example.demo.Model.Product;
import com.example.demo.Model.Purchase;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
import com.example.demo.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class ProductRestController {
    @Autowired
    private ProductService productService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private User user;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;

    interface ProductPurch extends Product.Basic,Product.ProdUser,Product.ProdRev,Product.ProdPurch{}
    interface ProductRev extends Product.Basic,Product.ProdUser,Product.ProdRev,Product.ProdPurch{}
    interface Products extends Product.Basic{}

    @JsonView(Products.class)
    @GetMapping("/products/")
    public List<Product> getAllProducts() {
        return productService.findAll().stream().toList();
    }

    @JsonView(Products.class)
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            return ResponseEntity.ok(optionalProduct.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @JsonView( ProductRev.class)
    @PostMapping("/product/")
    public ResponseEntity<Product> newProduct(Product product, MultipartFile imageField) throws IOException {
        if(!imageField.isEmpty()){
            productService.save(product,imageField);
        }else{
            productService.save(product,null);
        }
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(location).body(product);
    }

    @JsonView( ProductRev.class)
    @PutMapping("/product/{id}/modify")
    public ResponseEntity<Product> modifyProduct(Product product, @PathVariable int id) throws IOException {
        Optional<Product> productOptional = productService.findById(id);
        Product product1 = productOptional.get();
        product1.setDescription(product.getDescription());
        product1.setPrice(product.getPrice());
        product1.setType(product.getType());
        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @JsonView( ProductPurch.class)
    @PostMapping("/product/{id}/purchase")
    public ResponseEntity<Product> newPurchase(@PathVariable long id, @RequestParam double price) throws IOException {
        User user1 = userService.findByUserName("user");
        Purchase purchase = new Purchase(user1, price);
        purchaseService.save(purchase);
        Optional<Product> optionalProduct = productService.findById(id);
        Product product = optionalProduct.get();
        product.getPurchase().add(purchase);
        purchase.getProducts().add(product);
        user1.getProducts().add(purchase);
        user1.getProductList().add(product);
        purchase.setUser(user1);
        product.getUsers().add(user1);

        return ResponseEntity.ok(optionalProduct.get());
    }

    @JsonView( ProductRev.class)
    @PostMapping("/product/{id}/review")
    public ResponseEntity<Product> postReview(@PathVariable long id, @RequestParam String user, @RequestParam String review, @RequestParam int rating) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            User user1 = userService.findByUserName("user");
            Product product1 = product.get();
            Review review1 = new Review(user1, product1, rating, review);
            reviewService.save(review1);
            product1.getReviews().add(review1);
            review1.setProduct(product1);
            user1.getReviews().add(review1);
            review1.setUser(user1);
            user1.setNumReviews(user1.getNumReviews()+1);


            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Optional <Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            productService.deleteById(id);
            return ResponseEntity.ok(optionalProduct.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
