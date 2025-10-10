package com.safra.stock.safra_stock.entities;

// DTO para cada producto
public class ProductStockCocinaDTO {
    private String productName;
    private int stock;
    private String productDate;

    public ProductStockCocinaDTO(String productName, int stock, String productDate) {
        this.productName = productName;
        this.stock = stock;
        this.productDate = productDate;
    }

    public String getProductName() {
        return productName;
    }

    public int getStock() {
        return stock;
    }

    public String getProductDate() {
        return productDate;
    }
}
