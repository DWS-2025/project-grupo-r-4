package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Security.*;
import com.example.demo.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataBaseInitializer {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;

    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "/demo/images");

    @PostConstruct
    public void init() {
        try {
            initializeProducts();
            initializeReviews();
        } catch (Exception e) {
            System.err.println("Error durante la inicialización de la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeReviews() {
        try {
            ReviewDTO review = new ReviewDTO(1, 1, 3, "ni tan mal");
            reviewService.save(review); // <-- ESTA LÍNEA ES CLAVE
            System.out.println("Reseña inicializada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar reseña: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeProducts() {
        List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();


        // Crear DTOs para los productos usando el nuevo constructor
        productDTOs.add(new ProductDTO("Casa Moderna", 100.0, "Casa moderna de lego", "Muñecos"));
        productDTOs.add(new ProductDTO("Fifa 25 PS5", 60.0, "Juego de fútbol para PS5", "Electronica"));
        productDTOs.add(new ProductDTO("Hatchimals", 40.0, "Muñeco interactivo", "Muñecos"));
        productDTOs.add(new ProductDTO("Hot Wheels Circuito", 25.0, "Circuito de autos Hot Wheels", "Otros"));
        productDTOs.add(new ProductDTO("Muñeco Bebe", 30.0, "Muñeco de bebé", "Muñecos"));
        productDTOs.add(new ProductDTO("Nerf Scar Fortnite", 45.0, "Pistola Nerf de Fortnite", "Otros"));
        productDTOs.add(new ProductDTO("Oso Peluche", 20.0, "Oso de peluche suave", "Muñecos"));
        productDTOs.add(new ProductDTO("Pasteleria Barbie", 35.0, "Set de pastelería de Barbie", "Muñecos"));
        productDTOs.add(new ProductDTO("Peluqueria Play Doh", 22.0, "Set de peluquería Play Doh", "Muñecos"));
        productDTOs.add(new ProductDTO("Robot Teledirigido", 50.0, "Robot teledirigido", "Electronica"));
        productDTOs.add(new ProductDTO("Scalextric", 80.0, "Juego de Scalextric", "Electronica"));
        productDTOs.add(new ProductDTO("Trivial Pursuit", 30.0, "Juego de mesa Trivial Pursuit", "JuegoMesa"));


        User user1 =userRepository.save(new User("user", passwordEncoder.encode("pass"), "USER"));
        User user2 = userRepository.save(new User("admin", passwordEncoder.encode("adminpass"), "USER", "ADMIN"));
        User user3 = userRepository.save(new User("random", passwordEncoder.encode("random"), "USER"));


        ReviewDTO review = new ReviewDTO(1,1,3,"robo");

        for (ProductDTO productDTO : productDTOs) {

            String imageFileName = productDTO.getName().replaceAll("\\s+", "-") + ".jpg"; // Ej: Casa Moderna -> Casa-Moderna.jpg
            Path imagePath = IMAGES_FOLDER.resolve(imageFileName);

            if (!Files.exists(imagePath)) {
                System.err.println("Advertencia: El archivo de imagen no existe para el producto: " + productDTO.getName());
                continue;
            }

            try (FileInputStream fis = new FileInputStream(imagePath.toFile())) {
                MultipartFile imageFile = new MockMultipartFile(
                        imageFileName,
                        imageFileName,
                        "image/jpeg",
                        fis
                );
                String imageName = productDTO.getName().replaceAll("\\s+", "-") + ".jpg";
                productDTO.setImage(imageName);
                productService.save(productDTO, imageFile);
            } catch (IOException e) {
                System.err.println("Error al cargar imagen para producto: " + productDTO.getName());
                e.printStackTrace();
            }
        }
    }

}
