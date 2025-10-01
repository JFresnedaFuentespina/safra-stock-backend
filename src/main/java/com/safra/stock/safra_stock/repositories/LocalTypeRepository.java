package com.safra.stock.safra_stock.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.LocalType;

public interface LocalTypeRepository extends CrudRepository<LocalType, Integer>{

    List<LocalType> findByNameIn(List<String> names);

}
