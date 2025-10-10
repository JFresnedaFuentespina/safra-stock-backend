package com.safra.stock.safra_stock.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;

public interface StockDateCocinaCentralRepository
        extends CrudRepository<StockDateCocinaCentral, Integer> {

    List<StockDateCocinaCentral> findAll();

    List<StockDateCocinaCentral> findByDate(LocalDate date);

    void deleteByDateBefore(LocalDate date);

    @Query("SELECT s FROM StockDateCocinaCentral s JOIN FETCH s.product p JOIN FETCH p.product")
    List<StockDateCocinaCentral> findAllWithProducts();
}