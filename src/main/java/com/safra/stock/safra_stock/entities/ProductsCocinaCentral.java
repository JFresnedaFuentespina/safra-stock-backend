package com.safra.stock.safra_stock.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products_cocina_central")
public class ProductsCocinaCentral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "local_name", nullable = true)
    private String localName = "Cocina Central";

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Esta es la relación con la tabla de productos

    @Column(name = "stock", nullable = false)
    private int stock;

    public ProductsCocinaCentral() {
    }

    public ProductsCocinaCentral(Integer id, LocalDateTime date, String localName, Product product, int stock) {
        this.id = id;
        this.date = date;
        this.localName = localName;
        this.product = product;
        this.stock = stock;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
