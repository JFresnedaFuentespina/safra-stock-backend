package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.Product;
import com.safra.stock.safra_stock.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAll() {
        return (List<Product>) repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> findByName(String name) {
        return repository.findByName(name);
    }

    @Transactional
    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Transactional
    @Override
    public Optional<Product> delete(int id) {
        Optional<Product> productOptional = repository.findById(id);
        productOptional.ifPresent(productDb -> {
            repository.delete(productDb);
        });
        return productOptional;
    }

    @Override
    public Optional<Product> update(Product product) {
        Optional<Product> productOptional = repository.findById(product.getId());
        if (productOptional.isPresent()) {
            Product oldProduct = productOptional.orElseThrow();

            oldProduct.setName(product.getName());
            oldProduct.setDescription(product.getDescription());
            return Optional.of(repository.save(oldProduct));
        }
        return productOptional;
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Optional<Product> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public boolean changeActive(int id, boolean active) {
        Optional<Product> productOptional = repository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setActive(active);
            repository.save(product);
            return true;
        } else {
            return false;
        }
    }
}
