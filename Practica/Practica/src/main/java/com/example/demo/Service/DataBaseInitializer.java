package com.example.demo.Service;

import com.example.demo.Model.ProductDTO;
import com.example.demo.Model.UserDTO;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "/Practica/Practica/images");

    @PostConstruct
    public void init() {
        try {
            // Inicializar el usuario primero
            initializeUser();

            // Luego, inicializar productos
            initializeProducts();
        } catch (Exception e) {
            System.err.println("Error durante la inicialización de la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void initializeProducts() {

        // Crear DTOs para los productos usando el nuevo constructor
        productService.save(new ProductDTO( "Casa Moderna", 100.0, "Casa moderna de lego", ""), null);
        productService.save(new ProductDTO( "Fifa 25 PS5", 60.0, "Juego de fútbol para PS5", ""),null);
        productService.save(new ProductDTO( "Hatchimals", 40.0, "Muñeco interactivo", ""),null);
        productService.save(new ProductDTO( "Hot Wheels Circuito", 25.0, "Circuito de autos Hot Wheels", ""),null);
        productService.save(new ProductDTO("Muñeco Bebe", 30.0, "Muñeco de bebé", ""),null);
        productService.save(new ProductDTO("Nerf Scar Fortnite", 45.0, "Pistola Nerf de Fortnite", ""),null);
        productService.save(new ProductDTO("Oso Peluche", 20.0, "Oso de peluche suave", ""),null);
        productService.save(new ProductDTO("Pasteleria Barbie", 35.0, "Set de pastelería de Barbie", ""),null);
        productService.save(new ProductDTO("Peluqueria Play Doh", 22.0, "Set de peluquería Play Doh", ""),null);
        productService.save(new ProductDTO("Robot Teledirigido", 50.0, "Robot teledirigido", ""),null);
        productService.save(new ProductDTO( "Scalextric", 80.0, "Juego de Scalextric", ""),null);
        productService.save(new ProductDTO("Trivial Pursuit", 30.0, "Juego de mesa Trivial Pursuit", ""),null);

        List<ProductDTO> productDTOs = productService.findAll();
        // Obtener el User
        User user = userService.findByNameDatabse("user");

        // Procesar cada producto
        for (ProductDTO productDTO : productDTOs) {
            // Crear la ruta de la imagen: nombre_imagen = nombre_producto_sin_espacios + ".jpg"
            String imageFileName = productDTO.getName().replaceAll("\\s+", "-") + ".jpg"; // Ej: Casa Moderna -> Casa-Moderna.jpg
            Path imagePath = IMAGES_FOLDER.resolve(imageFileName);

            // Verificar si el archivo de imagen existe
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

                productService.save(productDTO, imageFile);
            } catch (IOException e) {
                System.err.println("Error al cargar imagen para producto: " + productDTO.getName());
                e.printStackTrace();
            }
        }
    }

    public void initializeUser() {
        // Verificar si el usuario ya existe para evitar duplicados
        UserDTO userDTO = new UserDTO();
        userDTO.setName("user");
        userService.save(userDTO); // Guardar el nuevo usuario
        System.out.println("Usuario 'user' creado exitosamente.");
    }
}
