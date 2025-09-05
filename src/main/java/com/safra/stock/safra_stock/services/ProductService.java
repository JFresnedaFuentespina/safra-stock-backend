package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.Product;

public interface ProductService {
    List<Product> findAll();

    Optional<Product> findById(int id);

    Optional<Product> findByName(String name);

    Product save(Product product);

    Optional<Product> update(Product product);

    Optional<Product> delete(int id);

    boolean existsByName(String name);

    boolean changeActive(int id, boolean active);
}
