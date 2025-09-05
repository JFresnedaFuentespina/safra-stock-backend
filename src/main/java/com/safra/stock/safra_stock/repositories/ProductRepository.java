package com.safra.stock.safra_stock.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    boolean existsByName(String name);

    Optional<Product> findByName(String name);

}
