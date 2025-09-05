package com.safra.stock.safra_stock.entities;

public class ProductDTO {
    private int id;
    private String name;
    private String description;
    private String image;
    private boolean active;

    public ProductDTO(int id, String name, String description, String image, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ProductDTO [name=" + name + ", description=" + description + ", image=" + image
                + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
