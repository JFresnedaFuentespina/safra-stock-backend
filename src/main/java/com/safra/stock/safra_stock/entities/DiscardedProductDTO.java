package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;
import java.util.List;

public class DiscardedProductDTO {

    private String reason;
    private List<DisposedProductDTO> products;
    private LocalDate disposalDate;
    private int localId;

    public DiscardedProductDTO() {
    }

    public DiscardedProductDTO(String reason, List<DisposedProductDTO> products, LocalDate disposalDate, int localId) {
        this.reason = reason;
        this.products = products;
        this.disposalDate = disposalDate;
        this.localId = localId;
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
        this.products = products;
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
}
