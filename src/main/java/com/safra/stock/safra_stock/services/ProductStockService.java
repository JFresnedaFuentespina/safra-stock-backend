package com.safra.stock.safra_stock.services;

import java.util.List;

import com.safra.stock.safra_stock.entities.ProductStock;

public interface ProductStockService {
    List<ProductStock> findAll();

    ProductStock save(ProductStock localStock);
}
