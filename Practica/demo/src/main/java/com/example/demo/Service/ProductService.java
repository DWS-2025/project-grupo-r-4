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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;


import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;


    // Convertir de Product -> ProductDTO
    public ProductDTO convertToDTO(Product product) {
        if(product.getReviews() != null){
            List<Long> reviewIds = product.getReviews().stream().map(Review::getReviewId).toList();
            return new ProductDTO(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getProductType(),
                    product.getImageFile(),
                    product.getFile(),
                    reviewIds
            );
        }else{
            return new ProductDTO(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getProductType(),
                    product.getImageFile(),
                    product.getFile()
            );
        }
    }

    // Convertir de ProductDTO -> Product
    public Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setProductType(dto.getType());
        product.setImage(dto.getImage());
        product.setImageFile(dto.getImageFile());
        return product;
    }
    public Product convertToEntity2(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setProductType(dto.getType());
        product.setImage(dto.getImage());
        product.setImageFile(dto.getImageFile());
        product.setId(dto.getId());
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

    public ProductDTO save(ProductDTO productDTO, MultipartFile imageField, MultipartFile fileField) throws IOException {
        Product product = convertToEntity(productDTO);

        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            product.setImage(path);
            product.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }
        if(fileField != null && !fileField.isEmpty()){
            String path = fileService.createFile(fileField);
            product.setFile(path);
        }else product.setFile("vacio.txt");

        if (product.getImage() == null || product.getImage().isEmpty()) {
            product.setImage("no-image.png");
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }
    public ProductDTO saveRest(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);

        if (product.getImage() == null || product.getImage().isEmpty()) {
            product.setImage("no-image.png");
        }

        if (product.getFile() == null || product.getFile().isEmpty()) {
            product.setFile("vacio.txt");
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }


    public void saveFiles(Long id, MultipartFile imageField, MultipartFile fileField) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (imageField != null && !imageField.isEmpty()) {
            String imagePath = imageService.createImage(imageField);
            product.setImage(imagePath);
            product.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }

        if (fileField != null && !fileField.isEmpty()) {
            String filePath = fileService.createFile(fileField);
            product.setFile(filePath);
        }

        productRepository.save(product);
    }



    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }
    public boolean existById(Long id) {
        return productRepository.existsById(id);
    }

    public void deleteById(long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        Product product = productRepository.findById(id).get();


        List<Review> reviews = product.getReviews();

        for (Review r : reviews){
            User user = r.getUser();
            user.getReviews().remove(r);
            r.setUser(null);
        }

        productRepository.deleteById(id);
    }

    public ProductDTO updateProduct(long id, ProductDTO productDetails, MultipartFile imageField) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setProductType(productDetails.getType());

        if (imageField != null && !imageField.isEmpty()) {
            String path = imageService.createImage(imageField);
            existingProduct.setImage(path);
            existingProduct.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    public ProductDTO updateProductData(long id, ProductDTO productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setProductType(productDetails.getType());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    public void updateProductFiles(long id, MultipartFile imageField, MultipartFile fileField) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Procesar imagen
        if (imageField != null && !imageField.isEmpty()) {
            String imagePath = imageService.createImage(imageField); // guarda imagen y devuelve ruta
            existingProduct.setImage(imagePath);
            existingProduct.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }

        // Procesar archivo (PDF u otro)
        if (fileField != null && !fileField.isEmpty()) {
            existingProduct.setFile(fileField.getOriginalFilename()); // nombre o ruta del archivo
            existingProduct.setFileFile(BlobProxy.generateProxy(fileField.getInputStream(), fileField.getSize()));
        }

        productRepository.save(existingProduct);
    }


    public ProductDTO findProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        convertToDTO(productOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
        return convertToDTO(productOptional.get());
    }

    public String sanitizeReview(String commentText) {
        return Jsoup.clean(commentText, Safelist.basic());
    }

    public Optional<ProductDTO> addReview(ProductDTO productDTO, Long userId, String reviewText, int rating) {

        UserDTO user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String s_review = sanitizeReview(reviewText);

        ReviewDTO newReviewDTO = new ReviewDTO();
        newReviewDTO.setUserId(userId);
        newReviewDTO.setReview(s_review);
        newReviewDTO.setRating(rating);
        newReviewDTO.setProductId(productDTO.getId());

        // Guardar la review
        ReviewDTO reviewDTO = reviewService.save(newReviewDTO);
        productDTO.getReviewsId().add(reviewDTO.getReviewId());

        return Optional.of(productDTO);
    }


    public List<ProductDTO> filterByTypeAndPrice(String type, Float price) {
        Product exampleProduct = new Product();

        // Configuramos el matcher para búsqueda exacta
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "name", "description", "image")
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

        if (type != null && !type.isEmpty()) {
            exampleProduct.setType(type);
        } else {
            matcher = matcher.withIgnorePaths("type");
        }

        if (price != null) {
            exampleProduct.setPrice(price);
        } else {
            matcher = matcher.withIgnorePaths("price");
        }

        Example<Product> example = Example.of(exampleProduct, matcher);
        List<Product> products = productRepository.findAll(example);

        return products.stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getType()))
                .toList();
    }

    public List<ProductDTO> findPaginated(int offset, int size) {
        return productRepository.findPaginatedProducts(offset, size)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .findFirst()
                .map(this::convertToDTO) // Convierte el producto a ProductDTO si se encuentra
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")); // Lanza excepción si no se encuentra
    }



}


