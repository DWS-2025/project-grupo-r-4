package com.example.demo.Model;

public class ProductDTO {
    private Long id;
    private String name;
    private double price;
    private String description;
    private String type;
    private String image;

    public ProductDTO(String name, String description, double price, String image, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.type = type;
    }

    public ProductDTO(Long id, String name, double price, String description, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
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
}

