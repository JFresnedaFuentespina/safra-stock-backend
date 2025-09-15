package com.safra.stock.safra_stock.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "local")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private boolean active;

    @ManyToMany
    @JoinTable(name = "users_locals", joinColumns = @JoinColumn(name = "local_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> workers;

    @Column(name = "stock_min_per_product")
    private int stockMinPerProduct;

    public int getStockMinPerProduct() {
        return stockMinPerProduct;
    }

    public void setStockMinPerProduct(int stockMinPerProduct) {
        this.stockMinPerProduct = stockMinPerProduct;
    }

    public List<User> getWorkers() {
        return workers;
    }

    public void setWorkers(List<User> workers) {
        this.workers = workers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
