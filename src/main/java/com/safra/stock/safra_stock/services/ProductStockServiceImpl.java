package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safra.stock.safra_stock.entities.ProductStock;
import com.safra.stock.safra_stock.repositories.ProductStockRepository;

@Service
public class ProductStockServiceImpl implements ProductStockService {

    @Autowired
    private ProductStockRepository repository;

    @Override
    public List<ProductStock> findAll() {
        return (List<ProductStock>) this.repository.findAll();
    }

    @Override
    public ProductStock save(ProductStock localStock) {
        Optional<ProductStock> existingStock = repository.findByProductNameAndLocalNameAndDate(
                localStock.getProductName(), localStock.getLocalName(), localStock.getDate());

        ProductStock stockToSave;
        if (existingStock.isPresent()) {
            stockToSave = existingStock.get();
            stockToSave.setStock(localStock.getStock()); // actualizar cantidad para esa fecha
        } else {
            stockToSave = localStock;
        }

        ProductStock savedStock = repository.save(stockToSave);

        return savedStock;
    }

}
