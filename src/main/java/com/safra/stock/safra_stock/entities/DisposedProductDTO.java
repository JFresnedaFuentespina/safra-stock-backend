package com.safra.stock.safra_stock.entities;

public class DisposedProductDTO {

    private int productId;
    private int quantity;

    public DisposedProductDTO() {
    }

    public DisposedProductDTO(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
