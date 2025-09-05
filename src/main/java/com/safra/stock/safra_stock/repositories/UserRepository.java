package com.safra.stock.safra_stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.safra.stock.safra_stock.entities.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByName(String username);

    Optional<User> findByName(String username);

    @Query("SELECT u FROM User u JOIN u.locales l WHERE l.name = :localName")
    List<User> findUsersByLocalName(@Param("localName") String localName);
}
