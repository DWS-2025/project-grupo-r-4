package com.example.demo.Controller;


import com.example.demo.Model.*;
import com.example.demo.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;


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
    public List<ProductDTO> getAllProducts() {
        return productService.findAll();
    }

    @JsonView(Products.class)
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<ProductDTO> productDTO = productService.findById(id);
        return productDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @JsonView(ProductRev.class)
    @PostMapping("/product/")
    public ResponseEntity<ProductDTO> newProduct(@ModelAttribute ProductDTO productDTO, @RequestParam(required = false) MultipartFile imageField) throws IOException {
        ProductDTO savedProduct = productService.save(productDTO, imageField);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedProduct.getId()).toUri();
        return ResponseEntity.created(location).body(savedProduct);
    }

    /*@JsonView(ProductRev.class)
    @PutMapping("/product/{id}/modify")
    public ResponseEntity<ProductDTO> modifyProduct(@RequestBody ProductDTO productDTO, @PathVariable Long id, MultipartFile imageField) throws IOException {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO, imageField);
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/

    /*@JsonView(ProductPurch.class)
    @PostMapping("/product/{id}/purchase")
    public ResponseEntity<ProductDTO> newPurchase(@PathVariable long id, @RequestParam double price,@RequestBody ProductDTO productDTO,MultipartFile imageField) {
        Optional<ProductDTO> purchasedProduct = productService.save(productDTO,imageField); // único usuario "user"
        return purchasedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @JsonView(ProductRev.class)
    @PostMapping("/product/{id}/review")
    public ResponseEntity<ProductDTO> postReview(@PathVariable long id, @RequestParam String review, @RequestParam int rating) {
        Optional<ProductDTO> reviewedProduct = productService.addReview(id, "user", review, rating); // único usuario "user"
        return reviewedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*@DeleteMapping("/product/{id}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id) {
        Optional<ProductDTO> deletedProduct = productService.deleteById(id);
        return deletedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @JsonView(Products.class)
    @GetMapping("/products")
    public List<ProductDTO> getAllProductsPaged(@RequestParam(defaultValue = "0") int page) {
        return productService.findAllPaged(page, 10);
    }*/
}
