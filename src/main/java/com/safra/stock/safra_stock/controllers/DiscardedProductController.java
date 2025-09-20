package com.safra.stock.safra_stock.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    public ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
