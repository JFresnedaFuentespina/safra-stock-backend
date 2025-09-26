package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiscardedProductDTO {

    private String reason;
    private List<DisposedProductDTO> products = new ArrayList<>();
    private LocalDate disposalDate;
    private int localId;
    private boolean active;

    public DiscardedProductDTO() {
    }

    public DiscardedProductDTO(String reason, List<DisposedProductDTO> products, LocalDate disposalDate, int localId,
            boolean active) {
        this.reason = reason;
        this.products = new ArrayList<>(products);
        this.disposalDate = disposalDate;
        this.localId = localId;
        this.active = active;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<DisposedProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<DisposedProductDTO> products) {
        this.products = new ArrayList<>(products); // mutable
    }

    public LocalDate getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(LocalDate disposalDate) {
        this.disposalDate = disposalDate;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
