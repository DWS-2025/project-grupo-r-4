package com.example.demo.Service;


import com.example.demo.Model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ImageService imageService;

    private ConcurrentMap<Long, Product> products = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    public ProductService() {
        Product product0 = new Product("Oso De Peluche", 19, "Oso de peluche ideal para niños.", "Peluches");
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
    }




    public Collection<Product> findAll() {
        return products.values().stream().toList();
    }

    public Collection<Product> findByType(String type) {
        return products.values().stream()
                .filter(product -> product.getProductType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public Optional<Product> findById(long id) {
        if(this.products.containsKey(id)){
            return Optional.of(products.get(id));
        }else{
            return Optional.empty();
        }
    }

    public Product save(Product product, MultipartFile imageField) {
        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            product.setImage(path);
        }

        if (product.getImage() == null || product.getImage().isEmpty()) {
            product.setImage("no-image.png");
        }

        // Si el producto no tiene ID asignado (producto nuevo)
        if (product.getId() == 0) {
            long id = nextId.getAndIncrement();
            product.setId(id);
        }

        products.put(product.getId(), product);
        return product;
    }


    public boolean existByName(String name) {
        for(Product p : products.values().stream().toList()){
            if(p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void deleteById(long id) {
        this.products.remove(id);
    }

    public void updateProduct(long id, Product productDetails, MultipartFile imageField) {
        Optional<Product> productOptional = findById(id);

        if (productOptional.isPresent()) {
            Product existingProduct = productOptional.get();

            // Actualizar datos del producto
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setProductType(productDetails.getProductType());

            // Si hay una nueva imagen, actualizamos
            if (imageField != null && !imageField.isEmpty()) {
                String path = imageService.createImage(imageField);
                existingProduct.setImage(path);
            }

            // No generamos un nuevo ID, solo actualizamos el producto en la colección
            products.put(id, existingProduct);

        } else {
            throw new IllegalArgumentException("Producto no encontrado");
        }
    }


}

