package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;
import com.safra.stock.safra_stock.repositories.ProductsCocinaCentralRepository;

@Service
@Transactional
public class ProductsCocinaCentralServiceImpl implements ProductsCocinaCentralService {

    private final ProductsCocinaCentralRepository productsRepo;

    public ProductsCocinaCentralServiceImpl(ProductsCocinaCentralRepository productsRepo) {
        this.productsRepo = productsRepo;
    }

    @Override
    public List<ProductsCocinaCentral> findAll() {
        return productsRepo.findAll();
    }

    @Override
    public Optional<ProductsCocinaCentral> findById(Integer id) {
        return productsRepo.findById(id);
    }

    @Override
    public ProductsCocinaCentral save(ProductsCocinaCentral product) {
        return productsRepo.save(product);
    }

    @Override
    public Optional<ProductsCocinaCentral> findByProductName(String productName) {
        return Optional.ofNullable(productsRepo.findByProductName(productName));
    }
}
