package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public UserDTO convertToDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setNumReviews(user.getNumReviews());
        userDTO.setEncodedPassword(user.getEncodedPassword());
        userDTO.setRoles(user.getRoles());
        userDTO.setReviews(
                user.getReviews().stream()
                        .map(reviewService::convertToDTO)
                        .collect(Collectors.toList())
        );
        userDTO.setReviewIds(user.getReviews().stream()
                .map(Review::getReviewId)
                .collect(Collectors.toList()));
        userDTO.setProductIds(user.getProducts().stream()
                .map(Purchase::getId)
                .collect(Collectors.toList()));

        // Nueva parte: añadir lista de ReviewDTOs completas
        List<ReviewDTO> reviewDTOs = user.getReviews().stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setReviewId(review.getReviewId());
            dto.setRating(review.getRating());
            dto.setReview(review.getReview());
            dto.setProductId(review.getProduct() != null ? review.getProduct().getId() : -1);
            dto.setProductName(review.getProduct() != null ? review.getProduct().getName() : "Producto eliminado"); // ✅ nueva línea
            dto.setUserName(review.getUser().getName());
            return dto;
        }).collect(Collectors.toList());

        userDTO.setReviews(reviewDTOs);

        // NUEVA PARTE: mapear compras (PurchaseDTO)
        List<PurchaseDTO> purchaseDTOs = user.getProducts().stream().map(purchase -> {
            PurchaseDTO purchaseDTO = new PurchaseDTO();
            purchaseDTO.setId(purchase.getId());
            purchaseDTO.setPrice(purchase.getPrice());

            // Mapear productos dentro de la compra
            List<ProductDTO> productDTOs = purchase.getProducts().stream().map(product -> {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(product.getName());
                productDTO.setPrice(product.getPrice());
                return productDTO;
            }).collect(Collectors.toList());

            purchaseDTO.setProducts(productDTOs);
            return purchaseDTO;
        }).collect(Collectors.toList());

        userDTO.setPurchases(purchaseDTOs); // <-- esto es clave


        return userDTO;
    }



    User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setNumReviews(userDTO.getNumReviews());
        user.setEncodedPassword(userDTO.getEncodedPassword());
        user.setRoles(userDTO.getRoles());

        return user;
    }


    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public UserDTO findByUserName(String name) {
        User user = userRepository.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return convertToDTO(user);
    }


    public Optional<UserDTO> findById(long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }



    public UserDTO save(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        user = userRepository.save(user);
        return convertToDTO(user);  // Devolver el usuario guardado como DTO
    }


    public void deleteByUser(String username) {
        User user = userRepository.findByName(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        userRepository.delete(user);
    }


    public void deleteById(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    public User findByNameDatabse(String name){
        return userRepository.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the user by username and throw an exception if not found
        return userRepository.findByName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }




    public User registerNewUser(User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Codificar la contraseña antes de guardarla
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));

        // Asignar rol por defecto
        user.setRoles(List.of("USER"));

        return userRepository.save(user);
    }
    public String encodePassword(String password) {
        return passwordEncoder.encode(password); // Usa el mismo bean que en el registro
    }

    public void update(User user) {
        userRepository.save(user);  // Guardar usuario actualizado
    }

    public void delete(Long id) {
        userRepository.deleteById(id);  // Eliminar usuario por id
    }

    public void updateUser(Long userId, String username, String password) throws Exception {
        // Buscar al usuario por su ID
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(username); // Actualizar el nombre de usuario
            user.setEncodedPassword(password); // Actualizar la contraseña (cuidado con la encriptación de contraseñas)

            // Guardar los cambios en la base de datos
            userRepository.save(user);
        } else {
            throw new Exception("Usuario no encontrado");
        }
    }

}
