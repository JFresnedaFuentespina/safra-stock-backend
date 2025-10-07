package com.safra.stock.safra_stock.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;

public interface ProductsCocinaCentralRepository extends CrudRepository<ProductsCocinaCentral, Integer> {

    List<ProductsCocinaCentral> findAll();

    // Buscar por nombre de producto
    ProductsCocinaCentral findByProductName(String productName);

    // Buscar por nombre de local (por si acaso)
    ProductsCocinaCentral findByLocalName(String localName);
}