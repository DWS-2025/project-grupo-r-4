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
        public String newProduct(Model model,ProductDTO productDto, MultipartFile imageField) throws IOException {
            if(productService.existByName(productDto.getName())) {
                return "404";
            }
            ProductDTO newProduct = productService.save(productDto, imageField);
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
/*
        @PostMapping("/products/{id}/upload")
        public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
            try {
                Optional<Product> optionalProduct = productRepository.findById(id);
                if (optionalProduct.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }

                Product product = optionalProduct.get();
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String uploadDir = "uploads/";

                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                File dest = new File(uploadDir + fileName);
                file.transferTo(dest);

                product.setImagePath(fileName);
                productRepository.save(product);

                return ResponseEntity.ok("Imagen subida correctamente.");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo.");
            }
        }

        @GetMapping("/images/{fileName}")
        public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws MalformedURLException {
            Path path = Paths.get("uploads/").resolve(fileName).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // cambia según tipo
                    .body(resource);
        }
*/

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
            String username = auth.getName();
            UserDTO userDto = userService.findByUserName(username);

            productService.addReview(productDto, username, review, rating);

            return "redirect:/product/" + id;
        }


        @PostMapping("/deleteProduct/{id}")
        public String deleteConfirmedProduct(@PathVariable long id) throws IOException {
            productService.deleteById(id);
            imageService.deleteImage(PRODUCTS_FOLDER);

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

            User user = userService.findByNameDatabse(principal.getName());
            ProductDTO productDTO = productService.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            PurchaseDTO purchaseDTO = new PurchaseDTO();
            purchaseDTO.setUserId(user.getId());  // Asigna el usuario a la compra

            purchaseService.createPurchase(purchaseDTO, productDTO);

            return "redirect:/product/" + id;
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
        public String checkout(HttpSession session) throws IOException {
            @SuppressWarnings("unchecked")
            List<Long> cart = (List<Long>) session.getAttribute("cart");

            if (cart == null || cart.isEmpty()) {
                return "redirect:/cart";
            }

            // Crear la compra
            PurchaseDTO purchaseDTO = new PurchaseDTO();
            purchaseDTO.setProductIds(new ArrayList<>(cart));

            PurchaseDTO savedPurchase = purchaseService.save(purchaseDTO);

            // Procesar cada producto del carrito
            for (Long productId : cart) {
                ProductDTO productDTO = productService.findById(productId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Producto con ID " + productId + " no encontrado"));

                // Verificar si la lista de compras está vacía y, si es así, inicializarla
                List<Long> purchases = Optional.ofNullable(productDTO.getPurchasesId())
                        .orElseGet(ArrayList::new);

                // Evitar duplicados
                if (!purchases.contains(savedPurchase.getId())) {
                    purchases.add(savedPurchase.getId());
                }

                // Asignar la lista de compras al DTO
                productDTO.setPurchasesId(purchases);

                // Llamar al servicio de actualización de productos. Pasamos 'null' para la imagen si no es necesaria.
                productService.updateProduct(productId, productDTO, null);
            }

            // Limpiar el carrito
            session.removeAttribute("cart");

            // Redirigir a los productos
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

    }




