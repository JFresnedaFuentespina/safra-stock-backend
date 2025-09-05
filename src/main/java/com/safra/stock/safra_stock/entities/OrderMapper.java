package com.safra.stock.safra_stock.entities;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getId());
        dto.setLocal(order.getLocal());

        List<ProductQuantityDTO> products = order.getProductsInOrder().stream()
                .map(pio -> {
                    ProductQuantityDTO pq = new ProductQuantityDTO();
                    pq.setProductName(pio.getProduct().getName());
                    pq.setQuantity(pio.getQuantity());
                    return pq;
                }).collect(Collectors.toList());

        dto.setProducts(products);
        dto.setDate(order.getDate());
        dto.setActive(order.isActive());
        return dto;
    }
}
