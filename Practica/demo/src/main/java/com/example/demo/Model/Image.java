package com.example.demo.Model;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String imageId;

    public Image(String urlImage) {
        this.imageId=urlImage;
    }

    public Image() {

    }

    public String getUrlImage() {
        return imageId;
    }

    public void setUrlImage(String urlImage) {
        this.imageId = urlImage;
    }
}
