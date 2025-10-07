package com.safra.stock.safra_stock.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentralId;

public interface StockDateCocinaCentralRepository
        extends CrudRepository<StockDateCocinaCentral, StockDateCocinaCentralId> {

    List<StockDateCocinaCentral> findAll();

    // Buscar registros por fecha
    List<StockDateCocinaCentral> findByDate(LocalDate date);

    // Buscar un registro por IDs compuestos
    Optional<StockDateCocinaCentral> findByStockIdAndProductLocalId(Integer stockId, Integer productLocalId);

    // Eliminar registros antiguos, por ejemplo
    void deleteByDateBefore(LocalDate date);
}