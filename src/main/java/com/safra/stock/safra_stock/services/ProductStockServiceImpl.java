package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safra.stock.safra_stock.entities.ProductStock;
import com.safra.stock.safra_stock.entities.ProductStockDate;
import com.safra.stock.safra_stock.entities.ProductStockDateId;
import com.safra.stock.safra_stock.repositories.ProductStockRepository;
import com.safra.stock.safra_stock.repositories.ProductStockDateRepository;

@Service
public class ProductStockServiceImpl implements ProductStockService {

    @Autowired
    private ProductStockRepository repository;

    @Autowired
    private ProductStockDateRepository stockDateRepository;

    @Override
    public List<ProductStockDate> findAll() {
        List<ProductStockDate> stock = (List<ProductStockDate>) this.stockDateRepository.findAll();
        stock.forEach(s -> System.out.println("Stock: " + s));
        return stock;
    }

    @Override
    public ProductStock save(ProductStock localStock) {
        Optional<ProductStock> existingStock = repository.findByProductNameAndLocalNameAndDate(
                localStock.getProductName(), localStock.getLocalName(), localStock.getDate());

        ProductStock stockToSave;
        if (existingStock.isPresent()) {
            stockToSave = existingStock.get();
            stockToSave.setStock(localStock.getStock());
        } else {
            stockToSave = localStock;
        }

        ProductStock savedStock = repository.save(stockToSave);

        // Crear el ProductStockDate asociado
        ProductStockDateId stockDateId = new ProductStockDateId(savedStock.getId(), savedStock.getId());
        ProductStockDate stockDate = new ProductStockDate(stockDateId, LocalDate.now(), savedStock);
        stockDateRepository.save(stockDate);
        return savedStock;
    }
}
