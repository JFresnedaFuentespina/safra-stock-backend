package com.safra.stock.safra_stock.entities;

import java.util.List;

public class StockCocinaGroupedDTO {
    private String date;
    private String localName;
    private List<ProductStockCocinaDTO> products;

    public StockCocinaGroupedDTO(String date, String localName, List<ProductStockCocinaDTO> products) {
        this.date = date;
        this.localName = localName;
        this.products = products;
    }

    public String getDate() { return date; }
    public String getLocalName() { return localName; }
    public List<ProductStockCocinaDTO> getProducts() { return products; }
}