package com.safra.stock.safra_stock.entities;

import java.util.ArrayList;
import java.util.List;

public class LocalDTO {
    private int id;
    private String name;
    private List<String> workerNames;
    private boolean active;
    private int stockMinPerProduct;
    private List<String> types;

    public LocalDTO(int id, String name, List<String> workerNames, boolean active, int stockMinPerProduct,
            List<String> types) {
        this.id = id;
        this.name = name;
        this.workerNames = workerNames;
        this.active = active;
        this.stockMinPerProduct = stockMinPerProduct;
        this.types = types;
    }

    public LocalDTO() {
        this.name = "";
        this.workerNames = new ArrayList<>();
        this.types = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getWorkerNames() {
        return workerNames;
    }

    public void setWorkerNames(List<String> workerNames) {
        this.workerNames = workerNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getStockMinPerProduct() {
        return stockMinPerProduct;
    }

    public void setStockMinPerProduct(int stockMinPerProduct) {
        this.stockMinPerProduct = stockMinPerProduct;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
