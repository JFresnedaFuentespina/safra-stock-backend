package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentralId;

public interface StockDateCocinaCentralService {

    List<StockDateCocinaCentral> findAll();

    Optional<StockDateCocinaCentral> findById(StockDateCocinaCentralId id);

    StockDateCocinaCentral save(StockDateCocinaCentral stockDate);

    List<StockDateCocinaCentral> findByDate(LocalDate date);

    Optional<StockDateCocinaCentral> findByStockIdAndProductLocalId(Integer stockId, Integer productLocalId);

    void createNewStockWithProducts(CocinaCentralStockRequest request);

}
