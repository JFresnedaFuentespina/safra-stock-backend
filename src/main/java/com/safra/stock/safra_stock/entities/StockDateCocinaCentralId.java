package com.safra.stock.safra_stock.entities;

import java.io.Serializable;

public class StockDateCocinaCentralId implements Serializable {
    private Integer stockId;
    private Integer productLocalId;

    public StockDateCocinaCentralId() {
    }

    public StockDateCocinaCentralId(Integer stockId, Integer productLocalId) {
        this.stockId = stockId;
        this.productLocalId = productLocalId;
    }

    // equals & hashCode obligatorios para PK compuesta
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        StockDateCocinaCentralId that = (StockDateCocinaCentralId) o;
        if (!stockId.equals(that.stockId))
            return false;
        return productLocalId.equals(that.productLocalId);
    }

    @Override
    public int hashCode() {
        int result = stockId.hashCode();
        result = 31 * result + productLocalId.hashCode();
        return result;
    }
}