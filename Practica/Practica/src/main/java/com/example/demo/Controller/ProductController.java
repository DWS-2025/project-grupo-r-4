package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Model.Purchase;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
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
import java.util.Optional;

@Controller
public class ProductController {

    private static final String PRODUCTS_FOLDER = "products";
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

    @GetMapping("/products/")
    public String showProducts(Model model) {
        Collection<Product> products = this.productService.findAll();
        model.addAttribute("products", products);

        Optional<Product> product0 = this.productService.findById(0);
        Optional<Product> product1 = this.productService.findById(1);
        Optional<Product> product2 = this.productService.findById(2);

        model.addAttribute("product0", product0.orElse(null));
        model.addAttribute("product1", product1.orElse(null));
        model.addAttribute("product2", product2.orElse(null));


        return "products";
    }

    @GetMapping("/product/new")
    public String newProductForm(Model model) {

        String userName = user.getName();
        model.addAttribute("user", userName);

        Product product0 = productService.findById(0).orElse(null);
        Product product1 = productService.findById(1).orElse(null);
        Product product2 = productService.findById(2).orElse(null);

        model.addAttribute("product0", product0);
        model.addAttribute("product1", product1);
        model.addAttribute("product2", product2);
        return "newProduct";
    }

    @PostMapping("/product/new")
    public String newProduct(Model model, Product product, MultipartFile imageField) throws IOException {
        if(productService.existByName(product.getName())) {
            return "404";
        }
        Product newProduct = productService.save(product, imageField);


        model.addAttribute("product", newProduct.getId());

        return "redirect:/product/" + newProduct.getId();
    }

    @GetMapping("/product/{id}/modify")
    public String modifyProductForm(Model model, @PathVariable long id) {
        Optional<Product> productOptional = productService.findById(id);
        model.addAttribute("user", user.getName());
        model.addAttribute("product", productOptional.get());

        return "modify";
    }

    @PostMapping("/product/{id}/modify")
    public String modifyProduct(Model model, Product product, @PathVariable int id,MultipartFile imageField) throws IOException {
        Optional<Product> productOptional = productService.findById(id);
        Product product1 = productOptional.get();
        productService.updateProduct(id, product1, imageField);
        model.addAttribute("product", productOptional.get());

        return "redirect:/product/" + productOptional.get().getId();
    }

    @GetMapping("/product/{id}")
    public String showProduct(Model model, @PathVariable long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);
            model.addAttribute("user", user.getName());

            model.addAttribute("product0", productService.findById(0).orElse(null));
            model.addAttribute("product1", productService.findById(1).orElse(null));
            model.addAttribute("product2", productService.findById(2).orElse(null));
            return "product";
        } else {
            return "404";
        }
    }



    @GetMapping("/product/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) {

        Optional<Product> op = productService.findById(id);

        if (op.isPresent()) {
            Product product = op.get();
            Resource poster = imageService.getImage(product.getImage());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(poster);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
    }

    @GetMapping("/product/{id}/review")
    public String newReview(Model model, @PathVariable long id) {
        Optional<Product> product = productService.findById(id);

        model.addAttribute("user", user.getName());
        model.addAttribute("product", product.get());

        return "reviews";
    }

    @PostMapping("/product/{id}/review")
    public String postReview(Model model, @PathVariable long id, @RequestParam String review, @RequestParam int rating) {
        Optional<Product> product = productService.findById(id);

        if (product.isPresent()) {
            User user = userService.findByUserName("user");
            reviewService.createReview(user, product.get(), rating, review);
            return "redirect:/product/" + id;
        } else {
            return "404";
        }
    }


    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(Model model, @PathVariable long id) throws IOException {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);
            return "deleteProduct";
        } else {
            return "404";
        }
    }

    @PostMapping("/deleteProduct/{id}")
    public String deleteConfirmedProduct(@PathVariable long id) throws IOException {
        productService.deleteById(id);
        imageService.deleteImage(PRODUCTS_FOLDER);
        return "redirect:/products/";
    }

    @GetMapping("/product/{id}/purchase")
    public String newPurchaseForm(Model model, @PathVariable long id) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product1 = optionalProduct.get();
            model.addAttribute("precio", product1.getPrice());
            model.addAttribute("product", product1);

            model.addAttribute("purchase", purchaseService.getClass());

            return "newPurchase";
        } else return "404";

    }

    @PostMapping("/product/{id}/purchase")
    public String newPurchase(@PathVariable long id) {
        try {
            purchaseService.createPurchase(id, "user");
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
