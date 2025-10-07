package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.ProductItem;
import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentralId;
import com.safra.stock.safra_stock.repositories.ProductsCocinaCentralRepository;
import com.safra.stock.safra_stock.repositories.StockDateCocinaCentralRepository;

@Service
@Transactional
public class StockDateCocinaCentralServiceImpl implements StockDateCocinaCentralService {
    @Autowired
    private StockDateCocinaCentralRepository stockDateRepo;

    @Autowired
    private ProductsCocinaCentralRepository productsRepo;

    public StockDateCocinaCentralServiceImpl(StockDateCocinaCentralRepository stockDateRepo) {
        this.stockDateRepo = stockDateRepo;
    }

    @Override
    public List<StockDateCocinaCentral> findAll() {
        return stockDateRepo.findAll();
    }

    @Override
    public Optional<StockDateCocinaCentral> findById(StockDateCocinaCentralId id) {
        return stockDateRepo.findById(id);
    }

    @Override
    public StockDateCocinaCentral save(StockDateCocinaCentral stockDate) {
        return stockDateRepo.save(stockDate);
    }

    @Override
    public List<StockDateCocinaCentral> findByDate(LocalDate date) {
        return stockDateRepo.findByDate(date);
    }

    @Override
    public Optional<StockDateCocinaCentral> findByStockIdAndProductLocalId(Integer stockId, Integer productLocalId) {
        return stockDateRepo.findByStockIdAndProductLocalId(stockId, productLocalId);
    }

    @Override
    @Transactional
    public void createNewStockWithProducts(CocinaCentralStockRequest request) {

        // Guardar cada producto en products_cocina_central
        for (ProductItem item : request.getProducts()) {
            ProductsCocinaCentral product = new ProductsCocinaCentral();
            product.setLocalName(item.getLocalName());
            product.setProductName(item.getProductName());
            product.setStock(item.getStock());
            product.setDate(LocalDateTime.now());

            productsRepo.save(product);

            // Crear relación en stock_date_cocina_central
            StockDateCocinaCentral relation = new StockDateCocinaCentral();
            relation.setStockId(product.getId());
            relation.setProductLocalId(product.getId());
            relation.setDate(request.getDate());

            stockDateRepo.save(relation);
        }
    }
}
