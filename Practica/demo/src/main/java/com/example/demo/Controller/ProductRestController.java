package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;


    interface ProductView extends Product.Basic, Product.ProdUser, Product.ProdRev, Product.ProdPurch {}


    @GetMapping("/products")
    public List<ProductDTO> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return productService.findPaginated(page,size);
    }


    @PostMapping("/products/filter")
    public ResponseEntity<List<ProductDTO>> filterProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Float price) {
        return ResponseEntity.ok(productService.filterByTypeAndPrice(type, price));
    }

    @GetMapping("/products/loadMore")
    public ResponseEntity<List<ProductDTO>> loadMoreProducts(
            @RequestParam int page,
            @RequestParam(defaultValue = "2") int size) {

        int offset = 10 + (page * size);
        List<ProductDTO> products = productService.findPaginated(offset, size);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/product/new")
    public ResponseEntity<ProductDTO> newProduct(@RequestBody ProductDTO productDto) {
        if (productService.existByName(productDto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Producto ya existe");
        }

        ProductDTO newProduct = productService.saveRest(productDto); // sin archivos
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }
    @PostMapping("/product/{id}/upload")
    public ResponseEntity<String> uploadFiles(
            @PathVariable Long id,
            @RequestParam MultipartFile imageField,
            @RequestParam MultipartFile fileField) throws IOException {

        if (!productService.existById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }

        productService.saveFiles(id, imageField, fileField);
        return ResponseEntity.ok("Archivos subidos correctamente");
    }
    /*@PostMapping("/product/new")
    public ResponseEntity<ProductDTO> newProduct(
            ProductDTO productDto,
            @RequestParam MultipartFile imageField,
            @RequestParam MultipartFile fileField) throws IOException {

        if(productService.existByName(productDto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Producto ya existe");
        }

        ProductDTO newProduct = productService.save(productDto, imageField, fileField);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }*/

    /*@PutMapping("/product/{id}/modify")
    public ResponseEntity<ProductDTO> modifyProduct(
            ProductDTO productDto,
            @PathVariable long id,
            @RequestParam(required = false) MultipartFile imageField) throws IOException {

        ProductDTO existingProduct = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productService.updateProduct(id, productDto, imageField);
        return ResponseEntity.ok(existingProduct);
    }*/

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> showProduct(@PathVariable long id) {
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(product);
    }

    @GetMapping("/product/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Resource image = new InputStreamResource(productDto.getImageFile().getBinaryStream());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(image);
    }

    @GetMapping("/product/{id}/file")
    public ResponseEntity<Resource> downloadProductFile(@PathVariable long id) {
        ProductDTO productDto = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        String fileName = productDto.getFile();
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no asignado");
        }

        try {
            Path filePath = Paths.get("demo", "Files").resolve(fileName).normalize();
            System.out.println("Buscando archivo en: " + filePath.toAbsolutePath()); // LOG

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no disponible");
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error accediendo archivo");
        }
    }


    /*@PutMapping(value = "/product/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductDTO> updateProductMultipart(@PathVariable long id,
                                                             @RequestParam("name") String name,
                                                             @RequestParam("description") String description,
                                                             @RequestParam("price") double price,
                                                             @RequestParam("type") String type,
                                                             @RequestParam(value = "imageFile", required = false) MultipartFile imageField) throws IOException {
        ProductDTO productDto = new ProductDTO();
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setPrice(price);
        productDto.setType(type);

        ProductDTO updatedProduct = productService.updateProduct(id, productDto, imageField);
        return ResponseEntity.ok(updatedProduct);
    }*/

    @PutMapping("/product/{id}/modify")
    public ResponseEntity<ProductDTO> updateProductData(@PathVariable long id,
                                                        @RequestBody ProductDTO productDto) {
        if (!productService.existById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }

        ProductDTO updatedProduct = productService.updateProductData(id, productDto); // sin archivos
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/product/{id}/upload")
    public ResponseEntity<String> updateProductFiles(@PathVariable long id,
                                                     @RequestParam(value = "imageField", required = false) MultipartFile imageField,
                                                     @RequestParam(value = "fileField", required = false) MultipartFile fileField) throws IOException {
        if (!productService.existById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }

        productService.updateProductFiles(id, imageField, fileField);
        return ResponseEntity.ok("Archivos actualizados correctamente");
    }



    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id) throws IOException {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO product = productOpt.get();

        productService.deleteById(id);
        if (product.getImageFile() != null) {
            imageService.deleteImage(product.getImage());
        }

        return ResponseEntity.ok("Producto eliminado");
    }

    @PostMapping("/product/{id}/review")
    public ResponseEntity<ProductDTO> addReview(
            @PathVariable long id,
            @RequestBody Review reviewRequest,
            Principal principal) {

        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO user = userService.findByUserName(principal.getName());
        productService.addReview(productOpt.get(), user.getId(), reviewRequest.getReview(), reviewRequest.getRating());

        return ResponseEntity.ok(productOpt.get());
    }

    @PostMapping("/product/{id}/purchase")
    public ResponseEntity<ProductDTO> makePurchase(@PathVariable long id, Principal principal) throws IOException {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO user = userService.findByUserName(principal.getName());
        ProductDTO product = productOpt.get();

        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setUserId(user.getId());
        purchaseDTO.setProductId(product.getId());
        purchaseDTO.setPrice(product.getPrice());

        purchaseService.createPurchase(purchaseDTO, product);

        return ResponseEntity.ok(product);
    }
    @PostMapping("/product/{id}/addToCart")
    public ResponseEntity<?> addToCart(@PathVariable("id") Long productId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProducts(new ArrayList<>());
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (!cart.getProducts().contains(product)) {
            cart.getProducts().add(product);
            cartRepository.save(cart);
        }

        return ResponseEntity.ok("Producto añadido al carrito");
    }


    @GetMapping("/cart")
    public ResponseEntity<?> viewCart(Principal principal) {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProducts(new ArrayList<>());
            return cartRepository.save(newCart);
        });

        List<ProductDTO> productsInCart = cart.getProducts().stream()
                .map(productService::convertToDTO)
                .toList();

        double total = productsInCart.stream().mapToDouble(ProductDTO::getPrice).sum();
        Map<String, Object> response = new HashMap<>();
        response.put("products", productsInCart);
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<?> checkout(Principal principal) throws IOException {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));

        List<Product> products = cart.getProducts();
        if (products == null || products.isEmpty()) {
            return ResponseEntity.badRequest().body("El carrito está vacío");
        }

        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setUserId(user.getId());
        purchaseDTO.setProductIds(products.stream().map(Product::getId).toList());
        purchaseDTO.setPrice(products.stream().mapToDouble(Product::getPrice).sum());

        PurchaseDTO savedPurchase = purchaseService.save(purchaseDTO);

        for (Product product : products) {
            ProductDTO productDTO = productService.findById(product.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            List<Long> purchases = Optional.ofNullable(productDTO.getPurchasesId()).orElseGet(ArrayList::new);
            if (!purchases.contains(savedPurchase.getId())) {
                purchases.add(savedPurchase.getId());
                productDTO.setPurchasesId(purchases);
                productService.updateProduct(product.getId(), productDTO, null);
            }
        }

        cart.setProducts(new ArrayList<>());
        cartRepository.save(cart);
        return ResponseEntity.ok("Compra realizada con éxito");
    }

    @PostMapping("/product/{id}/removeFromCart")
    public ResponseEntity<?> removeFromCart(@PathVariable("id") Long productId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));

        productRepository.findById(productId).ifPresent(product -> {
            cart.getProducts().remove(product);
            cartRepository.save(cart);
        });

        return ResponseEntity.ok("Producto eliminado del carrito");
    }


    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") long id, Principal principal) {
        try {
            reviewService.deleteById(id, principal.getName());
            return ResponseEntity.ok("Reseña eliminada correctamente.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }


    private User getUserFromPrincipal(Principal principal) {
        return userRepository.findByName(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }


}
