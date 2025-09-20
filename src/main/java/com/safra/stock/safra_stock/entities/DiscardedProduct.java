package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "products_disposal")
public class DiscardedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "discardedProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisposedProduct> products;

    private String reason;
    private LocalDate disposalDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DisposedProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DisposedProduct> products) {
        this.products = products;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(LocalDate disposalDate) {
        this.disposalDate = disposalDate;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
}
