package com.safra.stock.safra_stock.entities;

import java.util.List;

public class EmailPedidoRequest {

    private String localName;
    private String message;
    private List<ProductPedidoDTO> products;

    public List<ProductPedidoDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductPedidoDTO> products) {
        this.products = products;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
