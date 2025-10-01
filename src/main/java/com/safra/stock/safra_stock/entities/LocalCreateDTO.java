package com.safra.stock.safra_stock.entities;

import java.util.List;

public class LocalCreateDTO {
    private String name;
    private int stockMinPerProduct;
    private List<Integer> workers;
    private List<String> types;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStockMinPerProduct() {
        return stockMinPerProduct;
    }

    public void setStockMinPerProduct(int stockMinPerProduct) {
        this.stockMinPerProduct = stockMinPerProduct;
    }

    public List<Integer> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Integer> workers) {
        this.workers = workers;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}