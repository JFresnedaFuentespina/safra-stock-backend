package com.safra.stock.safra_stock.entities;

public class OrderShipmentProductDTO {
    private String productName;
    private int quantity;

    public OrderShipmentProductDTO(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderShipmentProductDTO [productName=" + productName + ", quantity=" + quantity + "]";
    }
}