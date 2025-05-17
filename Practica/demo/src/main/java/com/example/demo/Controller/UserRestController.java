package com.example.demo.Controller;

import com.example.demo.Model.ReviewDTO;
import com.example.demo.Model.User;
import com.example.demo.Model.UserDTO;
import com.example.demo.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    // Obtener info del usuario actual
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDTO userDTO = userService.findByUserName(principal.getName());
        return ResponseEntity.ok(userDTO);
    }

    // Obtener usuario por ID (admin o usuario dueño)
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Principal principal) {
        User currentUser = userService.findByNameDatabse(principal.getName());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin && !Objects.equals(currentUser.getId(), id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // Listar todos los usuarios (solo admin)
    @GetMapping("/showAll")
    public ResponseEntity<List<UserDTO>> getAllUsers(Principal principal) {
        User currentUser = userService.findByNameDatabse(principal.getName());
        if (currentUser == null || !currentUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.findAll());
    }

    // Crear nuevo usuario (registro)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = userService.registerNewUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Actualizar cuenta (solo el usuario dueño)
    @PutMapping("/me")
    public ResponseEntity<?> updateAccount(@RequestBody User updatedUser, Principal principal) {
        User currentUser = userService.findByNameDatabse(principal.getName());

        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
            currentUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEncodedPassword() != null && !updatedUser.getEncodedPassword().isBlank()) {
            currentUser.setEncodedPassword(userService.encodePassword(updatedUser.getEncodedPassword()));
        }

        userService.update(currentUser);

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                currentUser.getName(),
                currentUser.getEncodedPassword(),
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return ResponseEntity.ok("Cuenta actualizada correctamente. Logueate de nuevo para utilizar la cuenta");
    }

    // Eliminar cuenta (propia o por admin)
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        User currentUser = userService.findByNameDatabse(principal.getName());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin && !Objects.equals(currentUser.getId(), id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteById(id);

        // Si se eliminó a sí mismo, cerrar sesión
        if (Objects.equals(currentUser.getId(), id)) {
            try {
                request.logout();
                request.getSession().invalidate();
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("Cuenta eliminada");
    }

    // Obtener reseñas del usuario
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long id, Principal principal) {
        User currentUser = userService.findByNameDatabse(principal.getName());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin && !Objects.equals(currentUser.getId(), id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDTO user = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(user.getReviews());
    }

    // Obtener compras del usuario
    @GetMapping("/{id}/purchases")
    public ResponseEntity<?> getUserPurchases(@PathVariable Long id, Principal principal) {
        User currentUser = userService.findByNameDatabse(principal.getName());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin && !Objects.equals(currentUser.getId(), id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDTO userDTO = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(userDTO.getPurchases());
    }
}
