package com.safra.stock.safra_stock.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.Role;
import com.safra.stock.safra_stock.entities.User;
import com.safra.stock.safra_stock.repositories.RoleRepository;
import com.safra.stock.safra_stock.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();

        optionalRoleUser.ifPresent(roles::add);
        user.setEnabled(true);
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User saveNewUser(User user) {
        List<Role> roles = new ArrayList<>();
        roleRepository.findByName("ROLE_USER").ifPresent(roles::add);
        if (user.isAdmin()) {
            roleRepository.findByName("ROLE_ADMIN").ifPresent(roles::add);
        }
        user.setRoles(roles);
        user.setEnabled(true);
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public boolean existsByName(String username) {
        return userRepository.existsByName(username);
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User changeActive(User user, boolean active) {
        user.setEnabled(active);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUsersByLocalName(String localName) {
        return userRepository.findUsersByLocalName(localName);
    }

    @Override
    public List<User> findAllById(List<Integer> userIds) {
        Iterable<User> iterable = userRepository.findAllById(userIds);
        List<User> users = new ArrayList<>();
        iterable.forEach(users::add);
        return users;
    }

}
