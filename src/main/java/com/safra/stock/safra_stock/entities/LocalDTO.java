package com.safra.stock.safra_stock.entities;

import java.util.ArrayList;
import java.util.List;

public class LocalDTO {
    private int id;
    private String name;
    private List<String> workerNames;
    private boolean active;

    public LocalDTO(int id, String name, List<String> workerNames, boolean active) {
        this.id = id;
        this.name = name;
        this.workerNames = workerNames;
        this.active = active;
    }

    public LocalDTO() {
        this.name = "";
        this.workerNames = new ArrayList<>();
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
}
