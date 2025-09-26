package com.safra.stock.safra_stock.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safra.stock.safra_stock.entities.DiscardedProduct;
import com.safra.stock.safra_stock.entities.DiscardedProductDTO;
import com.safra.stock.safra_stock.entities.DisposedProduct;
import com.safra.stock.safra_stock.entities.DisposedProductDTO;
import com.safra.stock.safra_stock.entities.Local;
import com.safra.stock.safra_stock.entities.Product;
import com.safra.stock.safra_stock.services.DiscardedProductService;
import com.safra.stock.safra_stock.services.LocalService;
import com.safra.stock.safra_stock.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/discarded")
public class DiscardedProductController {

    @Autowired
    private DiscardedProductService discardedProductService;

    @Autowired
    private ProductService productService;

    @Autowired
    private LocalService localService;

    @GetMapping()
    public List<DiscardedProduct> list() {
        return discardedProductService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscardedProduct> getById(@PathVariable int id) {
        return discardedProductService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody DiscardedProductDTO dto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        // Crear la entidad principal
        DiscardedProduct discardedProduct = new DiscardedProduct();
        discardedProduct.setReason(dto.getReason());
        discardedProduct.setDisposalDate(dto.getDisposalDate());

        // Buscar el local si viene informado
        if (dto.getLocalId() >= 0) {
            Optional<Local> optionalLocal = localService.findById(dto.getLocalId());
            if (optionalLocal.isPresent()) {
                discardedProduct.setLocal(optionalLocal.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Local con id " + dto.getLocalId() + " no encontrado");
            }
        }

        // Procesar los productos descartados
        List<DisposedProduct> disposedProducts = new ArrayList<>();

        for (DisposedProductDTO dpDTO : dto.getProducts()) {
            Optional<Product> optionalProduct = productService.findById(dpDTO.getProductId());
            if (!optionalProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto con id " + dpDTO.getProductId() + " no encontrado");
            }

            Product product = optionalProduct.get();

            DisposedProduct disposedProduct = new DisposedProduct();
            disposedProduct.setProduct(product);
            disposedProduct.setQuantity(dpDTO.getQuantity());
            disposedProduct.setDiscardedProduct(discardedProduct);

            disposedProducts.add(disposedProduct);
        }

        discardedProduct.setProducts(disposedProducts);

        // Guardar en la base de datos
        discardedProductService.save(discardedProduct);

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Descarte guardado correctamente");

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody DiscardedProductDTO dto,
            BindingResult result, @PathVariable int id) {
        // Validación de campos del DTO
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        // Buscamos el descarte existente
        Optional<DiscardedProduct> optional = discardedProductService.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Descarte con id " + id + " no encontrado");
        }

        DiscardedProduct discardedProduct = optional.get();

        // Buscamos el local
        Optional<Local> optionalLocal = localService.findById(dto.getLocalId());
        if (!optionalLocal.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Local con id " + dto.getLocalId() + " no encontrado");
        }

        // Actualizamos los campos
        discardedProduct.setLocal(optionalLocal.get());
        discardedProduct.setDisposalDate(dto.getDisposalDate());
        discardedProduct.setReason(dto.getReason());
        discardedProduct.setActive(true);

        // Convertimos DTO de productos a entidades
        List<DisposedProduct> products = dto.getProducts().stream()
                .map(p -> {
                    DisposedProduct dp = new DisposedProduct();
                    dp.setQuantity(p.getQuantity());
                    dp.setProduct(productService.findById(p.getProductId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + p.getProductId())));
                    dp.setDiscardedProduct(discardedProduct);
                    return dp;
                })
                .collect(Collectors.toList()); // mutable
        discardedProduct.getProducts().clear();
        discardedProduct.getProducts().addAll(products);

        // Guardamos en la base de datos
        discardedProductService.save(discardedProduct);

        return ResponseEntity.ok("Descarte actualizado correctamente");
    }

    @PutMapping("/set-active/{id}/{active}")
    public ResponseEntity<Map<String, String>> setActive(@PathVariable int id, @PathVariable boolean active) {
        Optional<DiscardedProduct> optional = discardedProductService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        discardedProductService.setActive(id, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Estado actualizado correctamente");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
