package com.safra.stock.safra_stock.controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safra.stock.safra_stock.entities.Local;
import com.safra.stock.safra_stock.entities.LocalCreateDTO;
import com.safra.stock.safra_stock.entities.LocalDTO;
import com.safra.stock.safra_stock.entities.LocalType;
import com.safra.stock.safra_stock.entities.LocalUpdateDTO;
import com.safra.stock.safra_stock.entities.User;
import com.safra.stock.safra_stock.repositories.LocalTypeRepository;
import com.safra.stock.safra_stock.services.LocalService;
import com.safra.stock.safra_stock.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/safra-stock/locales")
public class LocalController {

    @Autowired
    private LocalService service;

    @Autowired
    private UserService userService;

    @Autowired
    LocalTypeRepository localTypeRepository;

    @GetMapping
    public List<LocalDTO> list() {
        return service.findAll().stream()
                .map(local -> new LocalDTO(
                        local.getId(),
                        local.getName(),
                        local.getWorkers().stream()
                                .map(User::getName)
                                .collect(Collectors.toList()),
                        local.isActive(),
                        local.getStockMinPerProduct(),
                        local.getTypes().stream()
                                .map(LocalType::getName)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        System.out.println("OBTENIENDO LOCAL: " + id);
        Optional<Local> localOptional = service.findById(id);
        if (!localOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Local local = localOptional.get();
        LocalDTO localDTO = new LocalDTO(
                local.getId(),
                local.getName(),
                local.getWorkers().stream()
                        .map(User::getName)
                        .collect(Collectors.toList()),
                local.isActive(),
                local.getStockMinPerProduct(),
                local.getTypes().stream()
                        .map(LocalType::getName)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok().body(localDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody LocalCreateDTO dto) {
        Local local = new Local();
        local.setName(dto.getName());
        local.setActive(true);
        local.setStockMinPerProduct(dto.getStockMinPerProduct());

        // Buscar entidades User por ID
        List<User> users = userService.findAllById(dto.getWorkers());
        local.setWorkers(users);

        // Buscar entidades LocalType por nombre
        List<LocalType> types = localTypeRepository.findByNameIn(dto.getTypes());
        local.setTypes(types);

        Local saved = service.save(local);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> edit(@Valid @RequestBody LocalUpdateDTO dto, @PathVariable int id) {
        Optional<Local> optionalLocal = service.findById(id);
        if (!optionalLocal.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Local local = optionalLocal.get();
        local.setName(dto.getName());

        // actualizar stock mínimo
        local.setStockMinPerProduct(dto.getStockMinPerProduct());

        // actualizar workers desde workerIds
        List<User> workers = userService.findAllById(dto.getWorkerIds());
        local.setWorkers(workers);

        // actualizar types
        List<LocalType> types = localTypeRepository.findByNameIn(dto.getTypes());
        local.setTypes(types);

        return ResponseEntity.ok().body(service.save(local));
    }

    @PutMapping("/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disable(@PathVariable int id) {
        Optional<Local> localOptional = service.findById(id);
        if (!localOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Local local = localOptional.get();
        return ResponseEntity.ok().body(service.changeActive(local, false));
    }

    @PutMapping("/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enable(@PathVariable int id) {
        Optional<Local> localOptional = service.findById(id);
        if (!localOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Local local = localOptional.get();
        return ResponseEntity.ok().body(service.changeActive(local, true));
    }

    public ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
