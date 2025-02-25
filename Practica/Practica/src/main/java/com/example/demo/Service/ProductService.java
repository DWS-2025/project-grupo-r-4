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

    private ConcurrentMap<String, Product> products = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    //Constructor

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

    public Product Save(Product product, MultipartFile imageField) {
        if(imageField.isEmpty() && imageField!=null){
            String path = imageService.createImage(imageField);
            product.setImage(path);
        }
        if(product.getImage() == null || product.getImage().isEmpty()) product.setImage("no-image.png");

        long id = nextId.getAndIncrement();
        product.setId(id);

        products.put(String.valueOf(id), product);
        return product;
    }

    public void deleteById(long id) {
        this.products.remove(id);
    }
}
