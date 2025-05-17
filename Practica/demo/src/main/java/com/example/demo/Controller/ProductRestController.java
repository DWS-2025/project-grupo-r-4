package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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
        return productService.findPaginated(page,size);
    }


    @GetMapping("/products/filter")
    public ResponseEntity<List<ProductDTO>> filterProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Float price) {
        return ResponseEntity.ok(productService.filterByTypeAndPrice(type, price));
    }

    @GetMapping("/products/loadMore")
    public ResponseEntity<List<ProductDTO>> loadMoreProducts(
            @RequestParam int page,
            @RequestParam(defaultValue = "2") int size) {

        int offset = 10 + (page * size);
        List<ProductDTO> products = productService.findPaginated(offset, size);
        return ResponseEntity.ok(products);
    }


    @PostMapping("/product/new")
    public ResponseEntity<ProductDTO> newProduct(
            ProductDTO productDto,
            @RequestParam MultipartFile imageField,
            @RequestParam MultipartFile fileField) throws IOException {

        if(productService.existByName(productDto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Producto ya existe");
        }

        ProductDTO newProduct = productService.save(productDto, imageField, fileField);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/product/{id}/modify")
    public ResponseEntity<ProductDTO> modifyProduct(
            ProductDTO productDto,
            @PathVariable long id,
            @RequestParam(required = false) MultipartFile imageField) throws IOException {

        ProductDTO existingProduct = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productService.updateProduct(id, productDto, imageField);
        return ResponseEntity.ok(existingProduct);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> showProduct(@PathVariable long id) {
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(product);
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

    @GetMapping("/product/{id}/file")
    public ResponseEntity<Resource> downloadProductFile(@PathVariable long id) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        String fileName = productDto.getFile();
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no asignado");
        }

        try {
            Path filePath = Paths.get("demo\\Files").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no disponible");
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error accediendo archivo");
        }
    }


    @PutMapping(value = "/product/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductDTO> updateProductMultipart(@PathVariable long id,
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
    public ResponseEntity<String> deleteProduct(@PathVariable long id) throws IOException {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO product = productOpt.get();

        // Primero intenta borrar la imagen si existe
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageService.deleteImage(imageUrl);
        }

        // Luego borra el producto
        productService.deleteById(id);

        return ResponseEntity.ok("Producto eliminado");
    }


    @PostMapping("/product/{id}/review")
    public ResponseEntity<ProductDTO> addReview(
            @PathVariable long id,
            @RequestBody Review reviewRequest,
            Principal principal) {

        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO user = userService.findByUserName(principal.getName());
        productService.addReview(productOpt.get(), user.getId(), reviewRequest.getReview(), reviewRequest.getRating());

        return ResponseEntity.ok(productOpt.get());
    }

    @PostMapping("/product/{id}/purchase")
    public ResponseEntity<ProductDTO> makePurchase(@PathVariable long id, Principal principal) throws IOException {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO user = userService.findByUserName(principal.getName());
        ProductDTO product = productOpt.get();

        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setUserId(user.getId());
        purchaseDTO.setProductId(product.getId());
        purchaseDTO.setPrice(product.getPrice());

        purchaseService.createPurchase(purchaseDTO, product);

        return ResponseEntity.ok(product);
    }

}
