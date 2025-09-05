package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.Order;

public interface OrderService {

    List<Order> findAll();

    Order save(Order order);

    Optional<Order> findById(int id);

    boolean changeActive(Order order, boolean active);

}
