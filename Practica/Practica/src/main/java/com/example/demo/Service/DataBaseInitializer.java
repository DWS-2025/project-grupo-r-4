package com.example.demo.Service;

import com.example.demo.Model.ProductDTO;
import com.example.demo.Model.UserDTO;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataBaseInitializer {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private static final String IMAGE_DIR = "src/main/resources/static/images/";

    @PostConstruct
    public void initData() throws IOException {
        // Crear directorio de imágenes si no existe
        Files.createDirectories(Paths.get(IMAGE_DIR));

        // Inicializar el usuario primero
        initializeUser();

        // Luego los productos que pueden depender del usuario
        initializeProducts();
    }

    public void initializeProducts() throws IOException {
        List<ProductDTO> productDTOs = new ArrayList<>();

        // Crear DTOs para los productos
        productDTOs.add(new ProductDTO("Casa Moderna", "Casa moderna de lego", 100.0, "Casa-moderna-lego.jpg"));
        productDTOs.add(new ProductDTO("Fifa 25 PS5", "Juego de fútbol para PS5", 60.0, IMAGE_DIR + "Fifa-25-PS5.jpg"));
        productDTOs.add(new ProductDTO("Hatchimals", "Muñeco interactivo", 40.0, IMAGE_DIR + "hatchimals.jpg"));
        productDTOs.add(new ProductDTO("Hot Wheels Circuito", "Circuito de autos Hot Wheels", 25.0, IMAGE_DIR + "Hot-wheels-circuito.jpg"));
        productDTOs.add(new ProductDTO("Muñeco Bebé", "Muñeco de bebé", 30.0, IMAGE_DIR + "muñeco-bebe.jpg"));
        productDTOs.add(new ProductDTO("Nerf Scar Fortnite", "Pistola Nerf de Fortnite", 45.0, IMAGE_DIR + "Nerf-Scar-Fortnite.jpg"));
        productDTOs.add(new ProductDTO("Oso Peluche", "Oso de peluche suave", 20.0, IMAGE_DIR + "oso-peluche.jpg"));
        productDTOs.add(new ProductDTO("Pastelería Barbie", "Set de pastelería de Barbie", 35.0, IMAGE_DIR + "Pasteleria-barbie.jpg"));
        productDTOs.add(new ProductDTO("Peluquería Play Doh", "Set de peluquería Play Doh", 22.0, IMAGE_DIR + "Peluqiería-play-doh.jpg"));
        productDTOs.add(new ProductDTO("Robot Teledirigido", "Robot teledirigido", 50.0, IMAGE_DIR + "Robot-teledirigido.jpg"));
        productDTOs.add(new ProductDTO("Scalextric", "Juego de Scalextric", 80.0, IMAGE_DIR + "scalextric.jpg"));
        productDTOs.add(new ProductDTO("Trivial Pursuit", "Juego de mesa Trivial Pursuit", 30.0, IMAGE_DIR + "Trivial-pursuit.jpg"));

        // Obtener el usuario (asumiendo que hay un método para obtenerlo)
        User user = userService.findByUserName("user");

        // Guardar los productos en la base de datos
        for (ProductDTO productDTO : productDTOs) {
            String imagePath = IMAGE_DIR + productDTO.getImage();

            try (FileInputStream fis = new FileInputStream(imagePath)) {
                MultipartFile imageFile = new MockMultipartFile(
                        productDTO.getImage(),
                        productDTO.getImage(),
                        "image/jpeg",
                        fis
                );

                // Crear el objeto Product
                Product product = new Product();
                product.setName(productDTO.getName());
                product.setDescription(productDTO.getDescription());
                product.setPrice(productDTO.getPrice());
                product.setUsers(user); // Establecer relación con el usuario

                // Guardar el producto con la imagen
                productService.save(product, imageFile);
            } catch (IOException e) {
                System.err.println("Error al cargar imagen para producto: " + productDTO.getName());
                e.printStackTrace();
            }
        }
    }

    public void initializeUser() {
        // Verificar si el usuario ya existe para evitar duplicados
        if (userService.findByUserName("user") == null) {
            User user = userService.findByUserName("user");
            userService.save(user);
        }
    }
}