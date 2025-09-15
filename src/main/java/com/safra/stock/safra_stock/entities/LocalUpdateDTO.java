package com.safra.stock.safra_stock.entities;

import java.util.List;

public class LocalUpdateDTO {

    private int id;
    private String name;
    private List<Integer> workerIds;
    private int stockMinPerProduct;

    public LocalUpdateDTO() {
    }

    public LocalUpdateDTO(int id, String name, List<Integer> workerIds, int stockMinPerProduct) {
        this.id = id;
        this.name = name;
        this.workerIds = workerIds;
        this.stockMinPerProduct = stockMinPerProduct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getWorkerIds() {
        return workerIds;
    }

    public void setWorkerIds(List<Integer> workerIds) {
        this.workerIds = workerIds;
    }

    public int getStockMinPerProduct() {
        return stockMinPerProduct;
    }

    public void setStockMinPerProduct(int stockMinPerProduct) {
        this.stockMinPerProduct = stockMinPerProduct;
    }

}
