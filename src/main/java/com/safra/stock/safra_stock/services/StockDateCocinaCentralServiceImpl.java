package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.Product;
import com.safra.stock.safra_stock.entities.ProductItem;
import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.repositories.ProductRepository;
import com.safra.stock.safra_stock.repositories.ProductsCocinaCentralRepository;
import com.safra.stock.safra_stock.repositories.StockDateCocinaCentralRepository;

@Service
@Transactional
public class StockDateCocinaCentralServiceImpl implements StockDateCocinaCentralService {
    @Autowired
    private StockDateCocinaCentralRepository stockDateRepo;

    @Autowired
    private ProductsCocinaCentralRepository productsCocinaRepo;

    @Autowired
    private ProductRepository productRepository;

    public StockDateCocinaCentralServiceImpl(StockDateCocinaCentralRepository stockDateRepo) {
        this.stockDateRepo = stockDateRepo;
    }

    @Override
    public List<StockDateCocinaCentral> findAll() {
        return stockDateRepo.findAll();
    }

    @Override
    public Optional<StockDateCocinaCentral> findById(int id) {
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
    @Transactional
    public void createNewStockWithProducts(CocinaCentralStockRequest request) {
        for (ProductItem item : request.getProducts()) {

            Product productEntity = productRepository.findByName(item.getProductName())
                    .orElseGet(() -> {
                        Product newProduct = new Product();
                        newProduct.setName(item.getProductName());
                        return productRepository.save(newProduct);
                    });

            ProductsCocinaCentral productStock = new ProductsCocinaCentral();
            productStock.setLocalName(item.getLocalName() != null ? item.getLocalName() : "Cocina Central");
            productStock.setProduct(productEntity);
            productStock.setStock(item.getQuantity());
            productStock.setDate(item.getDate() != null
                    ? item.getDate().atStartOfDay()
                    : (request.getDate() != null ? request.getDate().atStartOfDay() : LocalDate.now().atStartOfDay()));

            productsCocinaRepo.save(productStock);

            StockDateCocinaCentral relation = new StockDateCocinaCentral();
            relation.setProduct(productStock);
            relation.setDate(LocalDate.now());

            stockDateRepo.save(relation);
        }
    }

    @Override
    public List<StockDateCocinaCentral> findAllWithProducts() {
        return stockDateRepo.findAllWithProducts();
    }

    @Override
    public List<StockDateCocinaCentral> findLastStock() {
        StockDateCocinaCentral lastStockEntry = stockDateRepo.findTopByOrderByDateDesc();

        if (lastStockEntry == null) {
            return List.of();
        }

        LocalDate lastDate = lastStockEntry.getDate();

        return stockDateRepo.findByDate(lastDate);
    }

}
