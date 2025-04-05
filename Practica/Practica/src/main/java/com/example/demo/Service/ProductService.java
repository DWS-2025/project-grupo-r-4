package com.example.demo.Service;


import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;


@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;


    public ProductService() {
       /* Product product0 = new Product("Oso De Peluche", 19, "Oso de peluche ideal para niños.", "Peluches");
        Product product1 = new Product("Muñeco bebé", 29, "Muñeco bebé para niños", "Muñecas");
        Product product2 = new Product("Scalextric", 49, "Scalextric con 50 piezas y dos coches de carreras", "Juguetes");
        Product product3 = new Product("Casa moderna lego", 89, "Set de construcción de una casa muy bonita", "Construcción");
        Product product4 = new Product("FIFA 25 PS5", 69, "Videojuego de futbol", "Electrónica");
        Product product5 = new Product("Muñeca Barbie", 25, "Barbie con vestimenta de princesa y accesorios", "Muñecas");
        Product product6 = new Product("Hot Wheels Pista Turbo", 35, "Pista de coches con lanzador y loop", "Vehículos");
        Product product7 = new Product("Play-Doh Super Pack", 20, "Set de plastilina de colores con herramientas", "Manualidades");
        Product product8 = new Product("Robot teledirigido", 99, "Juego educativo interactivo para iPad", "Educativo");
        Product product9 = new Product("Nerf Scar Fortnite", 59, "Lanzador de dardos de largo alcance", "Aventura");
        Product product10 = new Product("Hatchimals Colleggtibles", 15, "Huevitos sorpresa con pequeñas criaturas dentro", "Coleccionables");
        Product product11 = new Product("Trivial Pursuit", 30, "Juguete interactivo para aprender letras y números", "Bebés");


        product0.setId(nextId.getAndIncrement());
        product1.setId(nextId.getAndIncrement());
        product2.setId(nextId.getAndIncrement());
        product3.setId(nextId.getAndIncrement());
        product4.setId(nextId.getAndIncrement());
        product5.setId(nextId.getAndIncrement());
        product6.setId(nextId.getAndIncrement());
        product7.setId(nextId.getAndIncrement());
        product8.setId(nextId.getAndIncrement());
        product9.setId(nextId.getAndIncrement());
        product10.setId(nextId.getAndIncrement());
        product11.setId(nextId.getAndIncrement());

        product0.setImage("oso-peluche.jpg");
        product1.setImage("muñeco-bebe.jpg");
        product2.setImage("scalextric.jpg");
        product3.setImage("Casa-moderna-lego.jpg");
        product4.setImage("Fifa-25-PS5.jpg");
        product5.setImage("Pasteleria-barbie.jpg");
        product6.setImage("Hot-wheels-circuito.jpg");
        product7.setImage("Peluqiería-play-doh.jpg");
        product8.setImage("Robot-teledirigido.jpg");
        product9.setImage("Nerf-Scar-Fortnite.jpg");
        product10.setImage("hatchimals.jpg");
        product11.setImage("Trivial-pursuit.jpg");

        products.put(product0.getId(), product0);
        products.put(product1.getId(), product1);
        products.put(product2.getId(), product2);
        products.put(product3.getId(), product3);
        products.put(product4.getId(), product4);
        products.put(product5.getId(), product5);
        products.put(product6.getId(), product6);
        products.put(product7.getId(), product7);
        products.put(product8.getId(), product8);
        products.put(product9.getId(), product9);
        products.put(product10.getId(), product10);
        products.put(product11.getId(), product11);
    */

    }




    public Collection<Product> findAll() {
        return productRepository.findAll();
    }

    public Collection<Product> findByType(String type) {
        return productRepository.findByTypeIgnoreCase(type);
    }

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product, MultipartFile imageField) {
        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            product.setImage(path);
        }

        if (product.getImage() == null || product.getImage().isEmpty()) {
            product.setImage("no-image.png");
        }

        return productRepository.save(product);
    }


    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }

    public void deleteById(long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    public void updateProduct(long id, Product productDetails, MultipartFile imageField) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setProductType(productDetails.getProductType());

        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            existingProduct.setImage(path);
        }

        productRepository.save(existingProduct);

    }

}

