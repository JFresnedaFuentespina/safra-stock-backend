package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.util.List;

import com.safra.stock.safra_stock.entities.ProductStock;
import com.safra.stock.safra_stock.entities.ProductStockDate;

public interface ProductStockService {
    List<ProductStockDate> findAll();

    ProductStock save(ProductStock localStock);

    ProductStock edit(ProductStock localStock);

    List<ProductStockDate> findByLocalAndDate(String localName, LocalDate date);
}
