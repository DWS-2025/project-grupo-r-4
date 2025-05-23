    package com.example.demo.Model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.Lob;
    import org.springframework.web.multipart.MultipartFile;

    import java.sql.Blob;
    import java.util.ArrayList;
    import java.util.List;

    public class ProductDTO {
        private Long id;
        private String name;
        private double price;
        private String description;
        private String type;
        private String image;
        private String file;
        private List<Long> users = new ArrayList<>();;
        private List<Long> reviewsId = new ArrayList<>();
        private List<Long> purchasesId = new ArrayList<>();

        @Lob
        @JsonIgnore
        private Blob imageFile;

        private String imagePath;


        public ProductDTO() {
            this.reviewsId = new ArrayList<>();
            this.purchasesId = new ArrayList<>();
            this.users = new ArrayList<>();
        }

        public ProductDTO(String name, double price,String description, String type) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.type = type;
        }

        public ProductDTO(Long id, String name, double price, String description, String type) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.description = description;
            this.type = type;
        }

        public ProductDTO(long id, String name, double price, String description, String productType, Blob imageFile, String file) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.description = description;
            this.type = productType;
            this.imageFile = imageFile;
            this.file = file;
        }

        public ProductDTO(long id, String name, double price, String description, String productType, Blob imageFile,String file, List<Long> reviewIds) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.description = description;
            this.type = productType;
            this.imageFile = imageFile;
            this.file = file;
            this.reviewsId = new ArrayList<>(reviewIds);
        }


        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public List<Long> getUsers() {
            return users;
        }

        public void setUsers(List<Long> users) {
            this.users = users;
        }

        public List<Long> getPurchasesId() {
            return purchasesId;
        }

        public void setPurchasesId(List<Long> purchasesId) {
            this.purchasesId = purchasesId;
        }

        public List<Long> getReviewsId() {
            return this.reviewsId;
        }

        public void setReviewsId(List<Long> reviewsId) {
            this.reviewsId = reviewsId;
        }

        public Blob getImageFile() {
            return imageFile;
        }

        public void setImageFile(Blob imageFile) {
            this.imageFile = imageFile;
        }
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

    }

