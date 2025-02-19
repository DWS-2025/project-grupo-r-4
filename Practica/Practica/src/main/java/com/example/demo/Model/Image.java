package com.example.demo.Model;

public class Image {
    private String imageId;

    public Image(String urlImage) {
        this.imageId=urlImage;
    }

    public String getUrlImage() {
        return imageId;
    }

    public void setUrlImage(String urlImage) {
        this.imageId = urlImage;
    }
}
