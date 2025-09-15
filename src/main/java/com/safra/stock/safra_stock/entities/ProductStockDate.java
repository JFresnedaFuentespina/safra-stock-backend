package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity(name = "stock_date")
public class ProductStockDate {

    @EmbeddedId
    private ProductStockDateId id;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @MapsId("productLocalId")
    @JoinColumn(name = "product_local_id")
    private ProductStock productStock;

    public ProductStockDate() {
    }

    public ProductStockDate(ProductStockDateId id, LocalDate date, ProductStock productStock) {
        this.id = id;
        this.date = date;
        this.productStock = productStock;
    }

    public ProductStockDateId getId() {
        return id;
    }

    public void setId(ProductStockDateId id) {
        this.id = id;
    }

    public ProductStock getProductStock() {
        return productStock;
    }

    public void setProductStock(ProductStock productStock) {
        this.productStock = productStock;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ProductStockDate [id=" + id + ", date=" + date + ", productStock=" + productStock + "]";
    }

    

}
