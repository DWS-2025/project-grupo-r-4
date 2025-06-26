package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    public interface Basic{}
    public interface ProdRev{}
    public interface ProdPurch{}
    public interface ProdUser{}



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Basic.class)
    private long id;
    @JsonView (Basic.class)
    private String name;
    @JsonView (Basic.class)
    private double price;
    private String image;
    @JsonView(Basic.class)
    private String description;
    @JsonView(Basic.class)
    private String type;
    @OneToMany(mappedBy = "productId",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonView(ProdRev.class)
    private List<Review> reviews;

    @JsonView(Basic.class)
    private String file;


    @JsonView(ProdUser.class)
    @ManyToMany
    private List<User> users;
    @JsonView(ProdPurch.class)
    @ManyToMany(mappedBy = "products",cascade = CascadeType.REMOVE)
    private List<Purchase> purchase;
    @Lob
    @JsonIgnore
    private Blob fileFile;
    @Lob
    @JsonIgnore
    private Blob imageFile;

    @JsonView(Basic.class)
    private String imagePath;

    public Product() {

    }

    public Product(String name,double price,String description, String type) {
        super();
        this.name= name;
        this.price=price;
        this.description=description;
        this.type = type;
        this.users = new ArrayList<>();
        this.reviews= new ArrayList<>();
        this.purchase= new ArrayList<>();
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Blob getFileFile() {
        return fileFile;
    }

    public void setFileFile(Blob fileFile) {
        this.fileFile = fileFile;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setProductType(String type) {
        this.type = type;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<User> getUsers(){
        return users;
    }

    public void setUsers(User user) {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        this.users.add(user);
    }

    public void addUser(User user){
        this.users.add(user);
    }

    public List<Purchase> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<Purchase> purchase) {
        this.purchase = purchase;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", name=" + name + ", price=" + price +  ", image=" + image + ", description=" + description + ", type=" + type + "]";
    }

}
