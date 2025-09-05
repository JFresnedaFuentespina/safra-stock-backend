package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.Order;
import com.safra.stock.safra_stock.repositories.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return (List<Order>) repository.findAll();
    }

    @Override
    public Order save(Order order) {
        return repository.save(order);
    }

    @Override
    public Optional<Order> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public boolean changeActive(Order order, boolean active) {
        order.setActive(active);
        repository.save(order);
        return true;
    }

}
