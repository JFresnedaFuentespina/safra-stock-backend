package com.safra.stock.safra_stock.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderShipmentItemDTO {
    private String productName;
    private int quantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("orderId")
    private Integer orderId;

    // GETTERS Y SETTERS PÚBLICOS
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        System.out.println("SETTER orderId: " + orderId); // <- debug
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "OrderShipmentItemDTO [productName=" + productName + ", quantity=" + quantity + ", date=" + date
                + ", orderId=" + orderId + "]";
    }
}
