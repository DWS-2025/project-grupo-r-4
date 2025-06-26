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
import org.springframework.security.authentication.AuthenticationManager;
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

    @Autowired
    private AuthenticationManager authenticationManager;


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
    public String showUserReviewsById(@PathVariable("id") Long userId,
                                      Principal principal,
                                      Model model) {

        UserDTO user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));



        if(!user.getName().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso ver las reseñas");
        }

        // Agregamos usuario al modelo
        model.addAttribute("user", user);

        // Obtenemos y agregamos las reseñas
        List<ReviewDTO> reviews = user.getReviews();
        model.addAttribute("reviews", reviews);
        model.addAttribute("numReviews", reviews != null ? reviews.size() : 0);
        model.addAttribute("name", user.getName());

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
            userService.registerNewUser(user);

            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            e.printStackTrace();
            return "register";
        }
    }


    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model,Principal principal) {

        User currentUser = userService.findByNameDatabse(principal.getName());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin && currentUser.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta información.");
        }

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
    public String showMyAccount(@RequestParam(required = false) Long userId, Principal principal, Model model) {
        UserDTO user;

        if (userId != null) {
            user = userService.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        } else {
            user = userService.findByUserName(principal.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }
        }


        boolean isAdmin = user.getRoles() != null && user.getRoles().contains("ADMIN");

        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);

        return "myAccount";
    }




    @GetMapping("/updateAccount")
    public String showUpdateForm(Model model) {
        User user = userService.getCurrentUser(); // Obtener el usuario actual desde algún servicio
        model.addAttribute("user", user); // Pasa el objeto user a la plantilla
        return "updateAccount"; // Nombre del archivo Mustache (updateAccount.mustache)
    }

    @PostMapping("/updateAccount")
    public String updateAccount(@ModelAttribute("user") User updatedUser, RedirectAttributes redirectAttributes, Principal principal) {
        User currentUser = userService.findByNameDatabse(principal.getName());

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        // Solo actualizar campos válidos
        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
            currentUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEncodedPassword() != null && !updatedUser.getEncodedPassword().isBlank()) {
            currentUser.setEncodedPassword(userService.encodePassword(updatedUser.getEncodedPassword()));
        }

        // Guardar sin convertir a DTO
        userService.update(currentUser);

        // Actualizar autenticación
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                currentUser.getName(),
                currentUser.getEncodedPassword(),
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        redirectAttributes.addFlashAttribute("success", "Cuenta actualizada correctamente.");
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

