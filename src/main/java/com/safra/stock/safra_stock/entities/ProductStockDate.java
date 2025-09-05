package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "stock_date")
public class ProductStockDate {

    @Id
    @Column(name = "stock_id")
    private int productStockId;

    @Column(name = "date")
    private LocalDate date;

    public int getProductStockId() {
        return productStockId;
    }

    public void setProductStockId(int productStockId) {
        this.productStockId = productStockId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
