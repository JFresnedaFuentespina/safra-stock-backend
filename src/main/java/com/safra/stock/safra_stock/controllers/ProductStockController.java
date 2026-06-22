package com.safra.stock.safra_stock.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safra.stock.safra_stock.entities.ProductStock;
import com.safra.stock.safra_stock.entities.ProductStockDate;
import com.safra.stock.safra_stock.services.ProductStockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/stock")
public class ProductStockController {

    @Autowired
    private ProductStockService service;

    @GetMapping()
    public List<ProductStockDate> list() {
        return service.findAll();
    }

    @GetMapping("/get-local-stock")
    public ResponseEntity<?> getStock(
            @RequestParam String localName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        System.out.println("INTENTANDO ACCEDER AL STOCK DE " + localName + " DEL DIA " + date);

        return ResponseEntity.ok(
                service.findByLocalAndDate(localName, date));
    }

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody ProductStock localStock, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(localStock));
    }

    @PutMapping()
    public ResponseEntity<?> edit(@Valid @RequestBody ProductStock stock, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.edit(stock));
    }

    @PutMapping("/update-batch")
    public ResponseEntity<?> updateBatch(@RequestBody List<ProductStock> stockList) {
        List<ProductStock> updated = stockList.stream()
                .map(service::edit)
                .toList();
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody List<@Valid ProductStock> stockList) {
        List<ProductStock> saved = stockList.stream()
                .map(service::save)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
