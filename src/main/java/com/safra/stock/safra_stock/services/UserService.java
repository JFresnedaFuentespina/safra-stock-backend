package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;

import com.safra.stock.safra_stock.entities.User;

public interface UserService {
    List<User> findAll();

    User save(User user);

    boolean existsByName(String name);

    Optional<User> findByName(String name);

    User changeActive(User user, boolean active);

    Optional<User> findById(int id);

    Object saveNewUser(User user);

    List<User> getUsersByLocalName(String localName);

    List<User> findAllById(List<Integer> usersIds);
}
