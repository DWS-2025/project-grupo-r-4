package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import jakarta.persistence.Lob;

import java.io.IOException;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;

    // Convertir de Product -> ProductDTO
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getProductType(),
                product.getImageFile()
        );
    }

    // Convertir de ProductDTO -> Product
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setProductType(dto.getType());
        product.setImage(dto.getImage());
        product.setImageFile(dto.getImageFile());
        return product;
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> findByType(String type) {
        return productRepository.findByTypeIgnoreCase(type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> findById(long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ProductDTO save(ProductDTO productDTO, MultipartFile imageField) throws IOException {
        Product product = convertToEntity(productDTO);

        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            product.setImage(path);
            product.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }

        if (product.getImage() == null || product.getImage().isEmpty()) {
            product.setImage("no-image.png");
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
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

    public ProductDTO updateProduct(long id, ProductDTO productDetails, MultipartFile imageField) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setProductType(productDetails.getType());

        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            existingProduct.setImage(path);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    public ProductDTO findProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        convertToDTO(productOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
        return convertToDTO(productOptional.get());
    }

    public Optional<ProductDTO> addReview(Long productId, String username, String reviewText, int rating) {
        // Buscar el producto
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product product = optionalProduct.get();

        // Buscar el usuario
        UserDTO user = userService.findByUserName(username);

        // Crear la review
        Review newReview = new Review();
        newReview.setProduct(product);
        newReview.setUser(userService.convertToEntity(user));
        newReview.setComment(reviewText);
        newReview.setRating(rating);

        // Guardar la review
        reviewRepository.save(newReview);

        // (Opcional) Actualizar lista de reviews del producto, si las manejas en cascada
        product.getReviews().add(newReview); // si tienes `@OneToMany(mappedBy = "product") List<Review> reviews` en `Product`
        productRepository.save(product);

        // Convertir el producto a DTO y devolver
        return Optional.of(convertToDTO(product));  // Asumiendo que tienes un método `convertToDto()` en `Product`
    }

    public List<ProductDTO> filterByTypeAndPrice(String type, Float price) {
        Product exampleProduct = new Product();  // <-- Usa Product, no ProductDTO

        if (type != null) {
            exampleProduct.setType(type);
        }
        if (price != null) {
            exampleProduct.setPrice(price);
        }

        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnorePaths("id", "name", "description", "image")
                .withIgnoreNullValues();

        Example<Product> example = Example.of(exampleProduct, matcher);

        List<Product> products = productRepository.findAll(example);

        // Convertir List<Product> -> List<ProductDTO>
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getDescription(), product.getType()))
                .toList();

        return productDTOS;
    }
    public List<ProductDTO> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}


