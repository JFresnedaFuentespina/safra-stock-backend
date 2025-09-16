package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.DiscardedProduct;

public interface DiscardedProductService {

    public List<DiscardedProduct> findAll();

    public Optional<DiscardedProduct> findById(int id);

    public DiscardedProduct save(DiscardedProduct discardedProduct);


}
