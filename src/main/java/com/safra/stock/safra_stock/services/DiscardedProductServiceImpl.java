package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.safra.stock.safra_stock.entities.DiscardedProduct;
import com.safra.stock.safra_stock.repositories.DiscardedProductRepository;

@Service
public class DiscardedProductServiceImpl implements DiscardedProductService {

    @Autowired
    private DiscardedProductRepository discardedProductRepository;

    @Override
    public List<DiscardedProduct> findAll() {
        return StreamSupport
                .stream(discardedProductRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public DiscardedProduct save(DiscardedProduct discardedProduct) {
        return discardedProductRepository.save(discardedProduct);
    }

    @Override
    public Optional<DiscardedProduct> findById(int id) {
        return discardedProductRepository.findById(id);
    }
}
