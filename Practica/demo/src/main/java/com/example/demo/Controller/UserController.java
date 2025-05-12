package com.example.demo.Controller;

import com.example.demo.Model.ReviewDTO;
import com.example.demo.Model.User;
import com.example.demo.Model.UserDTO;
import com.example.demo.Service.PurchaseService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;


import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}/buys")
    public String showUserPurchase(@PathVariable Long id, Model model){
        UserDTO userDTO = userService.findByUserName("user");
        model.addAttribute("user", userDTO);
        return "buys";
    }
    @GetMapping("/user/{id}/reviews")
    public String showLoggedInUserReviews(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Obtenemos el UserDTO por nombre del usuario logueado
        UserDTO userDTO = userService.findByUserName(userDetails.getUsername());

        // Agregamos al modelo el usuario completo
        model.addAttribute("user", userDTO);

        // Extraemos las reseñas
        List<ReviewDTO> reviews = userDTO.getReviews(); // Asegúrate de tener este método

        // Añadimos las reseñas al modelo (para que {{#reviews}} funcione)
        model.addAttribute("reviews", reviews);

        // Añadimos el número de reseñas
        model.addAttribute("numReviews", reviews != null ? reviews.size() : 0);

        // Puedes también pasar el nombre de usuario directamente si quieres usar {{name}}
        model.addAttribute("name", userDTO.getName());

        return "myReview";
    }


    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if(principal != null) {

            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken()); // <-- Esto es lo que Mustache necesita
        return "login"; // Nombre del HTML en templates/login.html
    }

    @RequestMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            // Registrar el nuevo usuario
            User registeredUser = userService.registerNewUser(user);

            // Autenticar automáticamente al usuario después del registro
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    registeredUser.getName(),
                    user.getEncodedPassword() // Usamos la contraseña original para autenticar al usuario
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirigir a la página principal o al dashboard del usuario
            return "redirect:/";

        } catch (RuntimeException e) {
            // Mostrar el error si algo falla durante el registro
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            e.printStackTrace(); // Esto es útil para depurar el error
            return "register";  // Redirigir a la página de registro
        }
    }
}

