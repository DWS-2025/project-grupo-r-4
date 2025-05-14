package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ReviewService reviewService;

    interface ProductView extends Product.Basic, Product.ProdUser, Product.ProdRev, Product.ProdPurch {}


    @GetMapping("/products")
    public List<ProductDTO> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return productService.findPaginated(page, size);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/product")
    public ResponseEntity<ProductDTO> createProduct(@RequestParam("name")String name,
                                                    @RequestParam("price") double price,
                                                    @RequestParam("description")String description,
                                                    @RequestParam("type")String type,
                                                    @RequestParam(value = "imageFile",required = false) MultipartFile imageField) throws IOException {
        if (productService.existByName(name)) {
            return ResponseEntity.badRequest().build();
        }
        ProductDTO productDto = new ProductDTO(name,price,description,type);
        ProductDTO savedProduct = productService.save(productDto, imageField);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping(value = "/product/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductDTO> updateProductMultipart(
            @PathVariable long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("type") String type,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageField) throws IOException {

        ProductDTO productDto = new ProductDTO();
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setPrice(price);
        productDto.setType(type);

        ProductDTO updatedProduct = productService.updateProduct(id, productDto, imageField);
        return ResponseEntity.ok(updatedProduct);
    }


    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) throws IOException {
        productService.deleteById(id);
        imageService.deleteImage("products"); // Asegúrate que borre la correcta
        return ResponseEntity.ok("Producto eliminado");
    }

    @PostMapping("/product/{id}/review")
    public ResponseEntity<ProductDTO> addReview(@PathVariable long id,
                                                @RequestParam("review") String review,
                                                @RequestParam("rating") int rating
                                                ) {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) return ResponseEntity.notFound().build();

        productService.addReview(productOpt.get(), id, review, rating);
        return ResponseEntity.ok(productOpt.get());
    }

    @PostMapping("/product/{id}/purchase")
    public ResponseEntity<ProductDTO> makePurchase(@PathVariable long id) throws IOException {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) return ResponseEntity.notFound().build();

        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseService.createPurchase(purchaseDTO, productOpt.get());

        return ResponseEntity.ok(productOpt.get());
    }

    @GetMapping("/product/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Resource image = new InputStreamResource(productDto.getImageFile().getBinaryStream());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(image);
    }

    @GetMapping("/products/filter")
    public List<ProductDTO> filterProducts(@RequestParam(required = false) String type,
                                           @RequestParam(required = false) Float price) {
        return productService.filterByTypeAndPrice(type, price);
    }
}
