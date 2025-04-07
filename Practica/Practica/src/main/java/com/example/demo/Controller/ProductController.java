package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
public class ProductController {

    private static final String PRODUCTS_FOLDER = "products";

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

    @GetMapping("/products/")
    public String showProducts(Model model) {
        // Ahora usamos ProductDto
        List<ProductDTO> products = this.productService.findAll();
        model.addAttribute("products", products);

        return "products";
    }

    @GetMapping("/product/new")
    public String newProductForm(Model model) {
        model.addAttribute("user", userService.findByUserName("user")); // Mejor pedirlo al userService

        return "newProduct";
    }

    @PostMapping("/product/new")
    public String newProduct(Model model,ProductDTO productDto, MultipartFile imageField) throws IOException {
        if(productService.existByName(productDto.getName())) {
            return "404";
        }
        ProductDTO newProduct = productService.save(productDto, imageField);
        model.addAttribute("product", newProduct.getId());
        return "redirect:/product/" + newProduct.getId();
    }

    @GetMapping("/product/{id}/modify")
    public String modifyProductForm(Model model, @PathVariable long id) {
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("user", userService.findByUserName("user"));
        model.addAttribute("product", product);

        return "modify";
    }

    @PostMapping("/product/{id}/modify")
    public String modifyProduct(Model model,ProductDTO productDto, @PathVariable long id, MultipartFile imageField) throws IOException {
        ProductDTO existingProduct = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productService.updateProduct(id, productDto, imageField);
        model.addAttribute("product", existingProduct);

        return "redirect:/product/" + id;
    }

    @GetMapping("/product/{id}")
    public String showProduct(Model model, @PathVariable long id) {
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("product", product);

        return "product";
    }

    @GetMapping("/product/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Resource image = imageService.getImage(productDto.getImage());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(image);
    }

    @GetMapping("/product/{id}/review")
    public String newReview(Model model, @PathVariable long id) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("user", userService.findByUserName("user"));
        model.addAttribute("product", productDto);

        return "reviews";
    }

    @PostMapping("/product/{id}/review")
    public String postReview(@PathVariable long id, @RequestParam String review, @RequestParam int rating, ReviewDTO reviewDto) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserDTO userDto = userService.findByUserName("user");

        reviewDto.setProductId(productDto.getId());
        reviewDto.setUserId(userDto.getId());

        reviewService.save(reviewDto);

        return "redirect:/product/" + id;
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProductForm(Model model, @PathVariable long id) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("product", productDto);

        return "deleteProduct";
    }

    @PostMapping("/deleteProduct/{id}")
    public String deleteConfirmedProduct(@PathVariable long id) throws IOException {
        productService.deleteById(id);
        imageService.deleteImage(PRODUCTS_FOLDER);

        return "redirect:/products/";
    }

    @GetMapping("/product/{id}/purchase")
    public String newPurchaseForm(Model model, @PathVariable long id) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("precio", productDto.getPrice());
        model.addAttribute("product", productDto);
        model.addAttribute("purchase", new PurchaseDTO());

        return "newPurchase";
    }

    @PostMapping("/product/{id}/purchase")
    public String newPurchase(@PathVariable long id) {
        try {
            purchaseService.createPurchase(purchaseService.save(new PurchaseDTO()));
            return "redirect:/product/" + id;
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return "404";
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            return "500";
        }
    }
}
