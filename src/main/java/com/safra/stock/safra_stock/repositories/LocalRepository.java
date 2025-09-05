package com.safra.stock.safra_stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.Local;

public interface LocalRepository extends CrudRepository<Local, Integer> {
    Optional<Local> findByName(String name);
    List<Local> findAll();

}
