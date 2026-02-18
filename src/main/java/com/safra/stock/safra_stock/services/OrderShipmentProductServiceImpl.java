package com.safra.stock.safra_stock.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.OrderShipmentProduct;
import com.safra.stock.safra_stock.repositories.OrderShipmentProductRepository;

@Service
@Transactional
public class OrderShipmentProductServiceImpl implements OrderShipmentProductService {

    private final OrderShipmentProductRepository shipmentProductRepository;

    public OrderShipmentProductServiceImpl(OrderShipmentProductRepository shipmentProductRepository) {
        this.shipmentProductRepository = shipmentProductRepository;
    }

    @Override
    public List<OrderShipmentProduct> findByOrderId(Integer orderId) {
        // Cada producto ya está asociado a un envío que a su vez tiene un orderId
        return shipmentProductRepository.findByShipment_Order_Id(orderId);
    }

    @Override
    public OrderShipmentProduct save(OrderShipmentProduct item) {
        // Guardar directamente el producto
        return shipmentProductRepository.save(item);
    }

    @Override
    public List<OrderShipmentProduct> saveAll(List<OrderShipmentProduct> items) {
        return (List<OrderShipmentProduct>) shipmentProductRepository.saveAll(items);
    }
}
