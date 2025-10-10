package com.safra.stock.safra_stock.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.ProductStockCocinaDTO;
import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;
import com.safra.stock.safra_stock.entities.StockCocinaGroupedDTO;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.services.StockDateCocinaCentralService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/cocina-central")
public class CocinaCentralController {

    @Autowired
    private StockDateCocinaCentralService stockDateService;

    @GetMapping("/stock")
    public List<StockCocinaGroupedDTO> listGroupedStock() {
        List<StockDateCocinaCentral> stockEntries = stockDateService.findAllWithProducts();
        // Asegúrate de que este método haga fetch join de product →
        // productsCocinaCentral → product real

        Map<LocalDate, List<StockDateCocinaCentral>> grouped = stockEntries.stream()
                .collect(Collectors.groupingBy(StockDateCocinaCentral::getDate));

        List<StockCocinaGroupedDTO> response = new ArrayList<>();

        for (Map.Entry<LocalDate, List<StockDateCocinaCentral>> entry : grouped.entrySet()) {
            LocalDate date = entry.getKey();
            List<StockDateCocinaCentral> stockList = entry.getValue();

            List<ProductStockCocinaDTO> productsDTO = stockList.stream().map(stock -> {
                ProductsCocinaCentral productEntry = stock.getProduct();
                String productName = productEntry.getProduct() != null ? productEntry.getProduct().getName() : "—";
                int stockAmount = productEntry.getStock();
                String productDate = productEntry.getDate() != null ? productEntry.getDate().toString() : "";
                return new ProductStockCocinaDTO(productName, stockAmount, productDate);
            }).collect(Collectors.toList());

            String localName = stockList.isEmpty() ? "" : stockList.get(0).getProduct().getLocalName();

            response.add(new StockCocinaGroupedDTO(date.toString(), localName, productsDTO));
        }

        // Ordenar por fecha descendente
        response.sort(Comparator.comparing(StockCocinaGroupedDTO::getDate).reversed());

        return response;
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
    public ResponseEntity<?> createStockBatch(@Valid @RequestBody List<@Valid StockDateCocinaCentral> stockList,
            BindingResult result) {
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
