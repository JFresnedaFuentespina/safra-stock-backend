package com.safra.stock.safra_stock.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.ProductStock;

public interface ProductStockRepository extends CrudRepository<ProductStock, Integer> {

    Iterable<ProductStock> findById(int id);

    Iterable<ProductStock> findByLocalName(String localName);

    Iterable<ProductStock> findByProductName(String productName);

    Optional<ProductStock> findByProductNameAndLocalName(String productName, String localName);

    Optional<ProductStock> findByProductNameAndLocalNameAndDate(String productName, String localName,
            LocalDateTime date);

}
