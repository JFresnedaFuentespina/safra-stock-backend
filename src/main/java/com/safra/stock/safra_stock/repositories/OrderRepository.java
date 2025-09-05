package com.safra.stock.safra_stock.repositories;

import com.safra.stock.safra_stock.entities.Order;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {
    boolean existsById(int id);

    Optional<Order> findById(int id);
}
