package com.safra.stock.safra_stock.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.Local;
import com.safra.stock.safra_stock.entities.User;
import com.safra.stock.safra_stock.repositories.LocalRepository;
import com.safra.stock.safra_stock.repositories.UserRepository;

@Service
public class LocalServiceImpl implements LocalService {

    @Autowired
    private LocalRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Local> findAll() {
        return (List<Local>) this.repository.findAll();
    }

    @Override
    public Local save(Local local) {
        List<User> validWorkers = local.getWorkers().stream()
                .map(user -> userRepository.findByName(user.getName())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + user.getId())))
                .collect(Collectors.toList());

        local.setWorkers(validWorkers);

        return repository.save(local);
    }

    @Override
    public Optional<Local> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public Local changeActive(Local local, boolean b) {
        local.setActive(b);
        return repository.save(local);
    }

}
