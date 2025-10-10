package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;

public interface StockDateCocinaCentralService {

    List<StockDateCocinaCentral> findAll();

    Optional<StockDateCocinaCentral> findById(int id);

    StockDateCocinaCentral save(StockDateCocinaCentral stockDate);

    List<StockDateCocinaCentral> findByDate(LocalDate date);

    void createNewStockWithProducts(CocinaCentralStockRequest request);

    @Query("SELECT s FROM StockDateCocinaCentral s " +
            "JOIN FETCH s.product p " +
            "JOIN FETCH p.product prod")
    List<StockDateCocinaCentral> findAllWithProducts();

}
