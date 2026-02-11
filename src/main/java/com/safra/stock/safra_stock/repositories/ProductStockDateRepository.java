package com.safra.stock.safra_stock.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.ProductStockDate;
import com.safra.stock.safra_stock.entities.ProductStockDateId;

public interface ProductStockDateRepository
        extends CrudRepository<ProductStockDate, ProductStockDateId> {

    List<ProductStockDate> findByProductStock_LocalNameAndDate(String localName, LocalDate date);

}
