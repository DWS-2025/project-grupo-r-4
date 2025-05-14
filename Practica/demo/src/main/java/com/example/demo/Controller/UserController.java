package com.example.demo.Controller;

import com.example.demo.Model.ReviewDTO;
import com.example.demo.Model.User;
import com.example.demo.Model.UserDTO;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.PurchaseService;
import com.example.demo.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{id}/buys")
    public String showUserPurchase(@PathVariable Long id, Model model, Principal principal) {
        // Seguridad: asegurarse de que el usuario autenticado está accediendo a su propia información o es admin
        User currentUser = userService.findByNameDatabse(principal.getName());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin && currentUser.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta información.");
        }

        Optional<UserDTO> userDTOOptional = userService.findById(id);
        if (userDTOOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }

        UserDTO userDTO = userDTOOptional.get();

        // Asegúrate de que el DTO contiene una lista de compras
        model.addAttribute("user", userDTO);
        model.addAttribute("buys", userDTO.getPurchases()); // Asegúrate de tener este método en UserDTO
        model.addAttribute("numBuys", userDTO.getPurchases() != null ? userDTO.getPurchases().size() : 0);
        model.addAttribute("name", userDTO.getName());

        return "buys"; // El nombre de tu vista (buys.mustache o buys.html)
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

    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model) {

        Optional<UserDTO> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "editUser";
        } else {
            return "redirect:/myAccount";
        }
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model, Principal principal) {
        // Obtener el usuario actual por su nombre de usuario
        User currentUser = userService.findByNameDatabse(principal.getName());

        // Verificar que el usuario exista y que tenga el rol "ADMIN"
        if (currentUser == null || !currentUser.getRoles().contains("ADMIN")) {
            return "access-denied"; // O puedes redirigir a otra página si prefieres
        }

        // Obtener la lista de usuarios para mostrar
        List<UserDTO> users = userService.findAll();  // Obtén todos los usuarios desde el servicio
        model.addAttribute("users", users);  // Agregar los usuarios a la vista
        return "userList"; // Redirige a la vista que contiene la lista de usuarios
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        // Obtener el usuario actual
        User currentUser = userService.findByNameDatabse(principal.getName());

        // Verificar que el usuario existe y tiene rol ADMIN
        if (currentUser == null || !currentUser.getRoles().contains("ADMIN")) {
            return "access-denied";
        }

        // Si el admin se está eliminando a sí mismo
        if (Objects.equals(currentUser.getId(), id))  {
            userService.deleteById(id);

            try {
                request.logout(); // Invalida la sesión actual
            } catch (ServletException e) {
                e.printStackTrace();
            }

            return "redirect:/login?accountDeleted"; // Redirige a login
        }

        // Eliminar a otro usuario
        userService.deleteById(id);
        return "redirect:/admin/users?userDeleted";
    }


    @GetMapping("/myAccount")
    public String showMyAccount(Model model, Principal principal) {
        // Obtener el usuario actual por su nombre de usuario
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            // Handle the case where the user does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        // Verificar si el usuario es admin
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        // Agregar los datos al modelo
        model.addAttribute("user", currentUser);
        model.addAttribute("isAdmin", isAdmin);

        return "myAccount"; // Devolver la vista 'myAccount.mustache'
    }


    @GetMapping("/updateAccount")
    public String showUpdateForm(Model model) {
        User user = userService.getCurrentUser(); // Obtener el usuario actual desde algún servicio
        model.addAttribute("user", user); // Pasa el objeto user a la plantilla
        return "updateAccount"; // Nombre del archivo Mustache (updateAccount.mustache)
    }

    @PostMapping("/updateAccount")
    public String updateAccount(@RequestParam("name") String name,
                                @RequestParam("encodedPassword") String encodedPassword,
                                @RequestParam("id") Long userId,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {

        // 1. Get current user
        User user = userService.getCurrentUser();

        // 2. Update user fields
        user.setName(name);
        user.setEncodedPassword(encodedPassword);

        // 3. Save changes (void method)
        userService.update(user);

        // 4. Get fresh user data for the redirect
        User updatedUser = userService.findByNameDatabse(principal.getName());

        // 5. Add to flash attributes
        redirectAttributes.addFlashAttribute("user", updatedUser);
        redirectAttributes.addFlashAttribute("isAdmin", updatedUser.getRoles().contains("ADMIN"));
        redirectAttributes.addFlashAttribute("success", "Account updated successfully!");

        return "redirect:/myAccount";
    }

    @PostMapping("/deleteAccount")
    @Transactional
    public String deleteAccount(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return "redirect:/login?error=noPrincipal";
        }

        String username = principal.getName();
        UserDTO userDTO = userService.findByUserName(username);

        if (userDTO == null) {
            return "redirect:/login?error=userNotFound";
        }

        // Eliminar datos relacionados y la cuenta
        userService.deleteById(userDTO.getId());

        // Invalidar sesión completamente
        try {
            request.logout(); // Cierra la sesión a nivel de seguridad
            request.getSession().invalidate(); // Invalida la sesión HTTP
        } catch (ServletException e) {
            e.printStackTrace();
        }

        // Redirigir a login con mensaje
        return "redirect:/login?accountDeleted";
    }
}

