package com.safra.stock.safra_stock.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.OrderShipmentItemDTO;
import com.safra.stock.safra_stock.entities.ProductItem;
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

        // Agrupar por la fecha del pedido (NO por la del producto)
        Map<LocalDate, List<StockDateCocinaCentral>> grouped = stockEntries.stream()
                .collect(Collectors.groupingBy(StockDateCocinaCentral::getDate));

        List<StockCocinaGroupedDTO> response = new ArrayList<>();

        for (Map.Entry<LocalDate, List<StockDateCocinaCentral>> entry : grouped.entrySet()) {
            LocalDate stockDate = entry.getKey(); // fecha del pedido
            List<StockDateCocinaCentral> entriesForDate = entry.getValue();

            List<ProductStockCocinaDTO> productsDTO = entriesForDate.stream()
                    .map(StockDateCocinaCentral::getProduct)
                    .filter(Objects::nonNull)
                    .map(p -> new ProductStockCocinaDTO(
                            p.getProduct().getName(),
                            p.getStock(),
                            p.getDate() != null ? p.getDate().toLocalDate().toString() : null // fecha propia del
                                                                                              // producto
                    ))
                    .toList();

            String localName = entriesForDate.isEmpty() ? "" : entriesForDate.get(0).getProduct().getLocalName();

            response.add(new StockCocinaGroupedDTO(stockDate.toString(), localName, productsDTO));
        }

        // Ordenar por fecha del pedido descendente
        response.sort(Comparator.comparing(StockCocinaGroupedDTO::getDate).reversed());
        return response;
    }

    @GetMapping("/last-stock")
    public ResponseEntity<StockCocinaGroupedDTO> getLastStock() {
        List<StockDateCocinaCentral> lastStockList = stockDateService.findLastStock();

        if (lastStockList == null || lastStockList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        LocalDate lastDate = lastStockList.get(0).getDate();
        String formattedDate = lastDate.toString();

        String localName = Optional.ofNullable(lastStockList.get(0).getProduct())
                .map(ProductsCocinaCentral::getLocalName)
                .orElse("Cocina Central");

        // Agrupar por producto y fecha
        Map<String, Map<String, Integer>> groupedData = new HashMap<>();

        for (StockDateCocinaCentral s : lastStockList) {
            ProductsCocinaCentral pc = s.getProduct();
            if (pc == null || pc.getProduct() == null)
                continue;

            String productName = pc.getProduct().getName();
            String productDate = pc.getDate() != null ? pc.getDate().toString()
                    : s.getDate() != null ? s.getDate().toString()
                            : formattedDate;

            int stock = pc.getStock();

            groupedData
                    .computeIfAbsent(productName, k -> new HashMap<>())
                    .merge(productDate, stock, Integer::sum);
        }

        // Crear DTO plano: un ProductStockCocinaDTO por combinación producto-fecha
        List<ProductStockCocinaDTO> products = groupedData.entrySet().stream()
                .flatMap(entry -> {
                    String productName = entry.getKey();
                    return entry.getValue().entrySet().stream()
                            .map(fechaEntry -> new ProductStockCocinaDTO(
                                    productName,
                                    fechaEntry.getValue(),
                                    fechaEntry.getKey()));
                })
                .sorted(Comparator
                        .comparing(ProductStockCocinaDTO::getProductName)
                        .thenComparing(ProductStockCocinaDTO::getProductDate))
                .toList();

        StockCocinaGroupedDTO dto = new StockCocinaGroupedDTO(formattedDate, localName, products);
        return ResponseEntity.ok(dto);
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

    @PutMapping("/stock/update-last")
    public ResponseEntity<?> editLastStock(@RequestBody List<OrderShipmentItemDTO> request) {
        try {
            System.out.println("ACTUALIZACIÓN ÚLTIMO STOCK");

            // Actualizar stock
            stockDateService.updateLastStockWithProducts(request);

            // Registrar envío SOLO si hay pedido
            request.forEach(item -> {
                System.out.println("ITEM RAW: " + item);
                System.out.println("ORDER ID RAW: " + item.getOrderId());
            });

            if (hasValidOrderId(request)) {
                System.out.println("Registrando envío de pedido");
                stockDateService.registerOrderShipment(request);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al modificar stock: " + e.getMessage());
        }
    }

    @PostMapping("/stock/generate-with-last")
    public ResponseEntity<?> generateWithLast(@RequestBody List<OrderShipmentItemDTO> request) {
        try {
            System.out.println("GENERANDO NUEVO STOCK");

            // Generar stock
            stockDateService.generateNewStockFromLast(request);

            // Registrar envío SOLO si hay pedido
            request.forEach(item -> {
                System.out.println("ITEM RAW: " + item);
                System.out.println("ORDER ID RAW: " + item.getOrderId());
            });

            if (hasValidOrderId(request)) {
                System.out.println("Registrando envío de pedido");
                stockDateService.registerOrderShipment(request);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar stock: " + e.getMessage());
        }
    }

    private boolean hasValidOrderId(List<OrderShipmentItemDTO> items) {
        return items != null
                && !items.isEmpty()
                && items.stream().anyMatch(
                        i -> i.getOrderId() != null && i.getOrderId() > 0);
    }
}
