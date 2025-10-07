package com.safra.stock.safra_stock.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.services.StockDateCocinaCentralService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/cocina-central")
public class CocinaCentralController {

    @Autowired
    private StockDateCocinaCentralService stockDateService;

    @GetMapping("/stock")
    public List<StockDateCocinaCentral> listStock() {
        return stockDateService.findAll();
    }

    @PostMapping("/stock")
    public ResponseEntity<?> createStock(@Valid @RequestBody CocinaCentralStockRequest request, BindingResult result) {
        try {
            if (result.hasFieldErrors()) {
                return validation(result);
            }
            stockDateService.createNewStockWithProducts(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el nuevo stock: " + e.getMessage());
        }
    }

    @PostMapping("/stock/batch")
    public ResponseEntity<?> createStockBatch(@Valid @RequestBody List<@Valid StockDateCocinaCentral> stockList, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        List<StockDateCocinaCentral> saved = stockList.stream()
                .map(stockDateService::save)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
