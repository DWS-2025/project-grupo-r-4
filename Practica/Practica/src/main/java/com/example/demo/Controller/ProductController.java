package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Service.*;

import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping({"/products", "/products/"}) // Mapea ambas versiones
    public String showProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        List<ProductDTO> products = productService.findPaginated(page, size);
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/products/loadMore")
    @ResponseBody public List<ProductDTO> loadMoreProducts( @RequestParam(value = "page") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        return productService.findPaginated(page, size);
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

        List<Long> reviewsId=product.getReviewsId();
        List<ReviewDTO> reviewDTOS = new java.util.ArrayList<>(List.of());

        if(reviewsId != null){
            for(Long reviewId : reviewsId) {
                reviewDTOS.add(reviewService.findById(reviewId));
            }
            List<UserDTO> users = new ArrayList<>();
            for(ReviewDTO review : reviewDTOS) {
                users.add(userService.findById(review.getUserId()).get());

            }
            model.addAttribute("users", users);
            model.addAttribute("reviews", reviewDTOS);
        }

        model.addAttribute("product", product);

        return "product";
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

        productService.addReview(id, "user", review, rating);

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
    public String newPurchase(@PathVariable long id) throws IOException {
            PurchaseDTO purchaseDTO = new PurchaseDTO();
            ProductDTO productDTO = productService.findById(id).get();
            purchaseService.createPurchase(purchaseDTO, productDTO);
            return "redirect:/product/" + id;

    }
}

