package com.safra.stock.safra_stock.services;

import java.util.List;

import com.safra.stock.safra_stock.entities.OrderShipmentProduct;

public interface OrderShipmentProductService {
    List<OrderShipmentProduct> findByOrderId(Integer orderId);

    OrderShipmentProduct save(OrderShipmentProduct item);

    List<OrderShipmentProduct> saveAll(List<OrderShipmentProduct> items);
}
