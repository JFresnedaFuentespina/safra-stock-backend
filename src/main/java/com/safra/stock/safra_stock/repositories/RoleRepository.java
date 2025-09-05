package com.safra.stock.safra_stock.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.safra.stock.safra_stock.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
