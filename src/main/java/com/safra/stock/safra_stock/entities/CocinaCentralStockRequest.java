package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;
import java.util.List;

public class CocinaCentralStockRequest {
    private LocalDate date;
    private List<ProductItem> products;

    // Getters y setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<ProductItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProductItem> products) {
        this.products = products;
    }
}
