package com.safra.stock.safra_stock.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safra.stock.safra_stock.entities.Role;
import com.safra.stock.safra_stock.entities.User;
import com.safra.stock.safra_stock.entities.UserDTO;
import com.safra.stock.safra_stock.repositories.RoleRepository;
import com.safra.stock.safra_stock.repositories.UserRepository;
import com.safra.stock.safra_stock.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    public UserController(UserService service, RoleRepository roleRepository) {
        this.service = service;
        this.roleRepository = roleRepository;
    }

    @GetMapping()
    public List<User> list() {
        return service.findAll();
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        System.out.println("INFORMACIÓN DEL USUARIO: " + username + "...");
        Optional<User> optionalUser = service.findByName(username);
        if (optionalUser.isPresent()) {
            UserDTO userDTO = new UserDTO(optionalUser.get());
            System.out.println("USUARIO!!! " + userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe.");
    }

    @GetMapping("/{username}/locales")
    public ResponseEntity<?> getLocalesForUser(@PathVariable String username) {
        Optional<User> optionalUser = service.findByName(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el usuario con nombre: " + username);
        }

        User user = optionalUser.get();
        return ResponseEntity.ok(user.getLocales());
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or #user.id == 0")
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        // Siempre asignamos el rol de usuario
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROL NO ENCONTRADO"));
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);

        // Comprobar si es el primer usuario
        long userCount = repository.count();
        boolean isFirstUser = userCount == 0;

        // Si el usuario es el primero o el campo admin viene en true, asignar rol de
        // admin
        if (isFirstUser || Boolean.TRUE.equals(user.isAdmin())) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROL DE ADMIN NO ENCONTRADO"));
            roles.add(adminRole);
            user.setAdmin(true);
        } else {
            user.setAdmin(false);
        }

        user.setRoles(roles);

        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveNewUser(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        System.out.println("INTENTO DE REGISTRO: " + user.getName());
        user.setAdmin(false);
        return create(user, result);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editUser(@Valid @RequestBody User user, BindingResult result) {
        System.out.println("INTENTO MODIFICACIÓN USUARIO: " + user);

        if (result.hasFieldErrors()) {
            return validation(result);
        }

        Optional<User> userOptional = service.findById(user.getId());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User userExisting = userOptional.get();
        userExisting.setEmail(user.getEmail());
        if (user.getPassword() != null) {
            userExisting.setPassword(user.getPassword());
        }
        userExisting.setEnabled(user.isEnabled());

        // Si se enviaron roles, los validamos y actualizamos
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Role> rolesPersistentes = new ArrayList<>();
            boolean isAdmin = false;

            for (Role r : user.getRoles()) {
                Optional<Role> roleOpt = roleRepository.findByName(r.getName());
                if (roleOpt.isPresent()) {
                    Role rolEncontrado = roleOpt.get();
                    rolesPersistentes.add(rolEncontrado);
                    if ("ROLE_ADMIN".equals(rolEncontrado.getName())) {
                        isAdmin = true;
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol no encontrado: " + r.getName());
                }
            }

            userExisting.setRoles(rolesPersistentes);
            userExisting.setAdmin(isAdmin);
        }

        return ResponseEntity.ok(service.save(userExisting));
    }

    @PutMapping("/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disable(@PathVariable int id) {
        Optional<User> userOptional = service.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User userExisting = userOptional.get();
        service.changeActive(userExisting, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enable(@PathVariable int id) {
        Optional<User> userOptional = service.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User userExisting = userOptional.get();
        service.changeActive(userExisting, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-users-in-local")
    public ResponseEntity<?> getUsersInLocal(@RequestParam String localName) {
        List<User> users = service.getUsersByLocalName(localName);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron usuarios para el local: " + localName);
        }
        List<UserDTO> dtoList = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    public ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
