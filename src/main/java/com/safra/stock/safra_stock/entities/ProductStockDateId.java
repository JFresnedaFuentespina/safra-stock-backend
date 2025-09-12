package com.safra.stock.safra_stock.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;

public class ProductStockDateId implements Serializable {

    @Column(name = "stock_id")
    private int stockId;

    @Column(name = "product_local_id")
    private int productLocalId;

    public ProductStockDateId() {

    }

    public ProductStockDateId(int stockId, int productLocalId) {
        this.stockId = stockId;
        this.productLocalId = productLocalId;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getProductLocalId() {
        return productLocalId;
    }

    public void setProductLocalId(int productLocalId) {
        this.productLocalId = productLocalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ProductStockDateId))
            return false;
        ProductStockDateId that = (ProductStockDateId) o;
        return stockId == that.stockId && productLocalId == that.productLocalId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, productLocalId);
    }
}
