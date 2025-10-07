package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;

public interface ProductsCocinaCentralService {

    List<ProductsCocinaCentral> findAll();

    Optional<ProductsCocinaCentral> findById(Integer id);

    ProductsCocinaCentral save(ProductsCocinaCentral product);

    Optional<ProductsCocinaCentral> findByProductName(String productName);
}
