package com.safra.stock.safra_stock.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safra.stock.safra_stock.entities.OrderShipment;
@Repository
public interface OrderShipmentRepository extends CrudRepository<OrderShipment, Integer> {
    List<OrderShipment> findByOrder_Id(Integer orderId);
}
