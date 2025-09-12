package com.safra.stock.safra_stock.repositories;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.ProductStockDate;
import com.safra.stock.safra_stock.entities.ProductStockDateId;

public interface ProductStockDateRepository extends CrudRepository<ProductStockDate, ProductStockDateId> {

}
