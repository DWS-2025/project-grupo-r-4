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


    /*public ProductService() {
        /*Product product0 = new Product("Oso De Peluche", 19, "Oso de peluche ideal para niños.", "Peluches");
        product0.setId(nextId.getAndIncrement());  // Asignamos ID antes de guardar
        product0.setImage("oso-peluche.jpg");
        products.put(product0.getId(), product0);

        Product product1 = new Product("Muñeco bebé", 29, "Muñeco bebé para niños", "Muñecas");
        product1.setId(nextId.getAndIncrement());
        product1.setImage("muñeco-bebe.jpg");
        products.put(product1.getId(), product1);

        Product product2 = new Product("Scalextric", 49, "Scalextric con 50 piezas y dos coches de carreras", "Juguetes");
        product2.setId(nextId.getAndIncrement());
        product2.setImage("scalextric.jpg");
        products.put(product2.getId(), product2);

    }*/




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

