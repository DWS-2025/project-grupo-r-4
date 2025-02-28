package com.example.demo.Controller;

import org.springframework.ui.Model;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class WebController {
    private ProductController productController;
    private UserController userController;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    public WebController(ProductController productController, UserController userController) {
        this.productController = productController;
        this.userController = userController;
    }

    @GetMapping("/")
    public String index(Model model) {
        Optional<Product> product0Optional = productService.findById(0);
        Optional<Product> product1Optional = productService.findById(1);
        Optional<Product> product2Optional = productService.findById(2);

        if(product0Optional.isPresent()) {
            model.addAttribute("product0", product0Optional.get());
        }
        if(product1Optional.isPresent()) {
            model.addAttribute("product1", product1Optional.get());
        }
        if(product2Optional.isPresent()) {
            model.addAttribute("product2", product2Optional.get());
        }
        return "index";
    }

    @GetMapping("/buys")
    public String buys(Model model) {
        return "buys";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(Model model) {
        return "deleteProduct";
    }

    @GetMapping("/details")
    public String details(Model model) {
        return "details";
    }

    @GetMapping("/myAccount")
    public String myAccount(Model model) {
        User user = userService.findByUserName("user");
        model.addAttribute("user", user);

        //Mejor oferta en el html
        Optional<Product> product0Optional = productService.findById(0);
        if (product0Optional.isPresent()) {
            model.addAttribute("product0", product0Optional.get());
        } else {
            model.addAttribute("product0NotFound", true);
        }

        //Novedad en el html
        Optional<Product> product1Optional = productService.findById(1);
        if (product1Optional.isPresent()) {
            model.addAttribute("product1", product1Optional.get());
        } else {
            model.addAttribute("product1NotFound", true);
        }

        return "myAccount";
    }

    @GetMapping("/myReview")
    public String myReview(Model model){
        User user = userService.findByUserName("user");
        model.addAttribute("user", user);
        return "myReview";
    }

    @GetMapping("/newProduct")
    public String newProduct(Model model) {
        return "newProduct";
    }

    @GetMapping("/products")
    public String products(Model model) {
        return "products";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        return "reviews";
    }

    @GetMapping("/ubication")
    public String ubication(Model model) {
        return "ubication";
    }

}
