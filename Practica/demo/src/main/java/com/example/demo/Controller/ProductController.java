    package com.example.demo.Controller;

    import com.example.demo.Model.*;
    import com.example.demo.Repository.CartRepository;
    import com.example.demo.Repository.ProductRepository;
    import com.example.demo.Repository.UserRepository;
    import com.example.demo.Service.*;


    import jakarta.servlet.ServletRequest;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.InputStreamResource;
    import org.springframework.core.io.Resource;
    import org.springframework.core.io.UrlResource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.server.ResponseStatusException;
    import jakarta.servlet.http.HttpSession;

    import java.io.File;
    import java.io.IOException;
    import java.net.MalformedURLException;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.security.Principal;
    import java.sql.SQLException;
    import java.util.*;
    import java.util.stream.Collectors;
    import java.util.stream.IntStream;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;


    @Controller
    public class ProductController {

        private static final String PRODUCTS_FOLDER = "products";

        @Autowired
        private ProductService productService;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private PurchaseService purchaseService;

        @Autowired
        private UserService userService;

        @Autowired
        private ImageService imageService;

        @Autowired
        private ReviewService reviewService;

        @GetMapping({"/products", "/products/"}) // Mapea ambas versiones
        public String showProducts(
                @RequestParam(value = "page", defaultValue = "0") int page,
                @RequestParam(value = "size", defaultValue = "10") int size,
                Model model) {

            List<ProductDTO> products = productService.findPaginated(page, size);
            model.addAttribute("products", products);
            return "products";
        }

        @GetMapping("/filter")
        public String showFilterPage() {
            return "filter";
        }

        @GetMapping("/products/filter")
        @ResponseBody
        public List<ProductDTO> filterProducts(
                @RequestParam(required = false) String type,
                @RequestParam(required = false) Float price) {

            return productService.filterByTypeAndPrice(type, price);
        }


        @GetMapping("/products/loadMore")
        @ResponseBody
        public List<ProductDTO> loadMoreProducts(
                @RequestParam int page,
                @RequestParam(defaultValue = "2") int size) {

            int offset = 10 + (page * size);

            return productService.findPaginated(offset, size);
        }


            @GetMapping("/product/new")
        public String newProductForm(Model model) {
            model.addAttribute("user", userService.findByUserName("user"));

            return "newProduct";
        }

        @PostMapping("/product/new")
        public String newProduct(Model model,ProductDTO productDto, MultipartFile imageField, MultipartFile fileField) throws IOException {
            if(productService.existByName(productDto.getName())) {
                return "404";
            }
            ProductDTO newProduct = productService.save(productDto, imageField, fileField);
            model.addAttribute("product", newProduct.getId());
            return "redirect:/product/" + newProduct.getId();
        }

        @GetMapping("/product/{id}/modify")
        public String modifyProductForm(Model model, @PathVariable long id) {
            ProductDTO product = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            model.addAttribute("user", userService.findByUserName("user"));
            model.addAttribute("product", product);

            return "modify";
        }

        @PostMapping("/product/{id}/modify")
        public String modifyProduct(Model model,ProductDTO productDto, @PathVariable long id, MultipartFile imageField) throws IOException {
            ProductDTO existingProduct = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            productService.updateProduct(id, productDto, imageField);
            model.addAttribute("product", existingProduct);

            return "redirect:/product/" + id;
        }


        @GetMapping("/product/{id}")
        public String showProduct(Model model, @PathVariable long id) {
            ProductDTO product = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            List<Long> reviewsId = product.getReviewsId();
            List<ReviewDTO> reviewDTOS = new ArrayList<>();

            if (reviewsId != null) {
                for (Long reviewId : reviewsId) {
                    ReviewDTO review = reviewService.findById(reviewId);
                    // Obtener el nombre del usuario y añadirlo al DTO
                    userService.findById(review.getUserId()).ifPresent(user -> {
                        review.setUserName(user.getName());  // 👈 Asegura que esto está en ReviewDTO
                    });
                    reviewDTOS.add(review);
                }
            }

            model.addAttribute("product", product);
            model.addAttribute("reviews", reviewDTOS);

            return "product";
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
        @PostMapping("/products/{id}/file")
        public ResponseEntity<Resource> downloadProductFile(@PathVariable long id) {
            ProductDTO productDto = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            String fileName = productDto.getFile();
            if (fileName == null || fileName.isBlank()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no tiene un archivo asignado");
            }

            try {
                Path filePath = Paths.get("demo\\Files").resolve(fileName).normalize();
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
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al acceder al archivo");
            }
        }






        @GetMapping("/product/{id}/review")
        public String newReview(Model model, @PathVariable long id) {
            ProductDTO productDto = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            UserDTO userDto = userService.findByUserName(username);

            model.addAttribute("user", userDto);
            model.addAttribute("product", productDto);

            return "reviews";
        }

        @PostMapping("/product/{id}/review")
        public String postReview(@PathVariable long id, @RequestParam String review, @RequestParam int rating) {
            ProductDTO productDto = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName(); // Obtener el nombre del usuario logueado
            UserDTO userDto = userService.findByUserName(username); // Buscar el usuario con el username

            if (userDto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }

            // Llamar al método addReview con userId en lugar de username
            productService.addReview(productDto, userDto.getId(), review, rating);

            return "redirect:/product/" + id;
        }



        @PostMapping("/deleteProduct/{id}")
        public String deleteConfirmedProduct(@PathVariable long id) throws IOException {
            productService.deleteById(id);

            return "redirect:/products/";
        }


        @GetMapping("/product/{id}/purchase")
        public String newPurchaseForm(Model model, @PathVariable long id, Principal principal) {
            ProductDTO productDto = productService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            UserDTO userDto = userService.findByUserName(principal.getName());

            model.addAttribute("precio", productDto.getPrice());
            model.addAttribute("product", productDto);
            model.addAttribute("purchase", new PurchaseDTO());
            model.addAttribute("user", userDto);

            return "newPurchase";
        }


        @PostMapping("/product/{id}/purchase")
        public String newPurchase(@PathVariable long id, Principal principal) throws IOException {
            if (principal == null) {
                return "redirect:/login"; // Redirigir si no está logueado
            }

            // Obtener el usuario autenticado
            User user = userService.findByNameDatabse(principal.getName());

            // Buscar el producto con el id
            ProductDTO productDTO = productService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Crear el DTO de compra y asignar los valores
            PurchaseDTO purchaseDTO = new PurchaseDTO();
            purchaseDTO.setUserId(user.getId());  // Asigna el usuario a la compra
            purchaseDTO.setProductId(id);  // Asignar el producto a la compra
            purchaseDTO.setPrice(productDTO.getPrice());  // Asignar el precio del producto a la compra

            // Crear la compra
            purchaseService.createPurchase(purchaseDTO, productDTO);

            return "redirect:/product/" + id;  // Redirigir al producto después de la compra
        }


        @PostMapping("/cart/add")
        public String addToCart(@RequestParam("productId") Long productId, Principal principal) {
            // Obtener el usuario autenticado
            String username = principal.getName();
            User user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar el carrito del usuario o crearlo si no existe
            Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setProducts(new ArrayList<>());
                return cartRepository.save(newCart);
            });

            // Buscar el producto
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Añadir el producto al carrito si no está ya
            if (!cart.getProducts().contains(product)) {
                cart.getProducts().add(product);
                cartRepository.save(cart);
            }

            return "redirect:/products";
        }



        @GetMapping("/cart")
        public String viewCart(Model model, Principal principal) {
            String username = principal.getName();
            User user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar carrito o crear uno nuevo si no existe
            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        newCart.setProducts(new ArrayList<>());
                        return cartRepository.save(newCart);
                    });

            List<ProductDTO> productsInCart = cart.getProducts().stream()
                    .map(productService::convertToDTO)
                    .collect(Collectors.toList());

            model.addAttribute("cartProducts", productsInCart);
            model.addAttribute("total", productsInCart.stream()
                    .mapToDouble(ProductDTO::getPrice)
                    .sum());

            return "cart";
        }


        @PostMapping("/cart/checkout")
        public String checkout(Principal principal) throws IOException {
            String username = principal.getName();
            User user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            List<Product> products = cart.getProducts();

            if (products == null || products.isEmpty()) {
                return "redirect:/cart";
            }

            PurchaseDTO purchaseDTO = new PurchaseDTO();
            purchaseDTO.setUserId(user.getId());
            purchaseDTO.setProductIds(
                    products.stream().map(Product::getId).collect(Collectors.toList())
            );

// 👇 Aquí calculamos y seteamos el precio total
            double totalPrice = products.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();
            purchaseDTO.setPrice(totalPrice);

            PurchaseDTO savedPurchase = purchaseService.save(purchaseDTO);

            for (Product product : products) {
                ProductDTO productDTO = productService.findById(product.getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Producto con ID " + product.getId() + " no encontrado"));

                List<Long> purchases = Optional.ofNullable(productDTO.getPurchasesId())
                        .orElseGet(ArrayList::new);

                if (!purchases.contains(savedPurchase.getId())) {
                    purchases.add(savedPurchase.getId());
                }

                productDTO.setPurchasesId(purchases);
                productService.updateProduct(product.getId(), productDTO, null);
            }

            // Limpiar el carrito del usuario
            cart.setProducts(new ArrayList<>());
            cartRepository.save(cart);

            return "redirect:/products";
        }




        @PostMapping("/cart/remove")
        public String removeFromCart(@RequestParam("productId") Long productId, Principal principal) {
            // Obtener el usuario autenticado
            String username = principal.getName();
            User user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener el carrito del usuario
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            // Buscar el producto y eliminarlo del carrito si existe
            productRepository.findById(productId).ifPresent(product -> {
                cart.getProducts().remove(product);
                cartRepository.save(cart);
            });

            return "redirect:/cart";
        }
        @PostMapping("/reviews/delete")
        public String deleteReview(@RequestParam("reviewId") long reviewId,
                                   RedirectAttributes redirectAttributes,
                                   Principal principal) {
            try {
                reviewService.deleteById(reviewId);
                redirectAttributes.addFlashAttribute("success", "Reseña eliminada correctamente.");
            } catch (ResponseStatusException e) {
                redirectAttributes.addFlashAttribute("error", e.getReason());
            }
            return "redirect:/myAccount";
        }


    }




