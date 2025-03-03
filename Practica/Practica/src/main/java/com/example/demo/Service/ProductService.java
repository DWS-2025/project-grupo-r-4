package com.example.demo.Service;


import com.example.demo.Model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
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

    public ProductService(){

        Product product0 = new Product ("Oso De Peluche", 19, "Oso de peluche ideal" +
                " para niños.", "Peluches");
        product0.setImage("oso-peluche.jpg");
        save(product0, null);

        Product product1 = new Product ("Muñeco bebé", 29, "Muñeco bebé para niños", "Muñecas");
        product1.setImage("muñeco-bebe.jpg");
        save(product1, null);

        Product product2 = new Product ("Scalextric", 49, "Scalextric con 50 piezas y dos coches de carreras", "Juguetes");
        product2.setImage("scalextric.jpg");
        save(product2, null);


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
        for(p : products.strem){

        }

        if(imageField != null && !imageField.isEmpty()){
            String path = imageService.createImage(imageField);
            product.setImage(path);
        }
        if(product.getImage() == null || product.getImage().isEmpty()) product.setImage("no-image.png");

        long id = nextId.getAndIncrement();
        product.setId(id);

        products.put(id, product);
        return product;
    }

    public void deleteById(long id) {
        this.products.remove(id);
    }
}
