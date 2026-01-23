package com.safra.stock.safra_stock.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.safra.stock.safra_stock.entities.OrderShipmentProduct;

@Repository
public interface OrderShipmentProductRepository extends CrudRepository<OrderShipmentProduct, Integer> {
    List<OrderShipmentProduct> findByShipment_Order_Id(Integer orderId); // ✅ Usando nombres de campos correctos
}
