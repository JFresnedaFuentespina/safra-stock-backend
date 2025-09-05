package com.safra.stock.safra_stock.entities;

import java.util.List;

public class LocalUpdateDTO {

    private int id;
    private String name;
    private List<Integer> workerIds;

    public LocalUpdateDTO() {
    }

    public LocalUpdateDTO(int id, String name, List<Integer> workerIds) {
        this.id = id;
        this.name = name;
        this.workerIds = workerIds;
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

}
