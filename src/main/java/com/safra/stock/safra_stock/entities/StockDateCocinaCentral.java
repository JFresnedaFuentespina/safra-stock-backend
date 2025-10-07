package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(StockDateCocinaCentralId.class)
@Table(name = "stock_date_cocina_central")
public class StockDateCocinaCentral {

    @Id
    @Column(name = "stock_id")
    private Integer stockId;

    @Id
    @Column(name = "product_local_id")
    private Integer productLocalId;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", insertable = false, updatable = false)
    private ProductsCocinaCentral product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_local_id", insertable = false, updatable = false)
    private ProductStock productLocal;

    public StockDateCocinaCentral() {
    }

    public StockDateCocinaCentral(Integer stockId, Integer productLocalId, LocalDate date) {
        this.stockId = stockId;
        this.productLocalId = productLocalId;
        this.date = date;
    }

    // Getters y setters
    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public Integer getProductLocalId() {
        return productLocalId;
    }

    public void setProductLocalId(Integer productLocalId) {
        this.productLocalId = productLocalId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ProductsCocinaCentral getProduct() {
        return product;
    }

    public void setProduct(ProductsCocinaCentral product) {
        this.product = product;
    }

    public ProductStock getProductLocal() {
        return productLocal;
    }

    public void setProductLocal(ProductStock productLocal) {
        this.productLocal = productLocal;
    }
}