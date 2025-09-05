package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.Local;

public interface LocalService {
    List<Local> findAll();

    Optional<Local> findById(int id);

    Local save(Local local);

    Local changeActive(Local local, boolean b);
}
