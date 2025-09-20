package com.safra.stock.safra_stock.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;

import com.safra.stock.safra_stock.entities.Product;
import com.safra.stock.safra_stock.entities.ProductDTO;
import com.safra.stock.safra_stock.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductDTO> list() {
        List<Product> productos = productService.findAll();
        List<ProductDTO> productosDTO = new ArrayList<>();
        String base64Image = null;
        for (Product product : productos) {
            if (product.getImage() != null) {
                base64Image = Base64.getEncoder().encodeToString(product.getImage());
            }
            ProductDTO productDTO = new ProductDTO(product.getId(), product.getName(), product.getDescription(),
                    base64Image, product.isActive());
            productosDTO.add(productDTO);
        }
        return productosDTO;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> create(@Valid @RequestBody ProductDTO productDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        // Decodificar la imagen base64 a byte[]
        byte[] imageBytes = null;
        if (productDto.getImage() != null && productDto.getImage().startsWith("data:")) {
            String base64Image = productDto.getImage().substring(productDto.getImage().indexOf(",") + 1);
            imageBytes = Base64.getDecoder().decode(base64Image);
        }

        // Crear objeto Product y asignar valores
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImage(imageBytes);

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isEmpty())
            return ResponseEntity.notFound().build();

        Product product = productOptional.get();
        product.setName(name);
        product.setDescription(description);

        if (image != null && !image.isEmpty()) {
            try {
                product.setImage(image.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error leyendo la imagen");
            }
        }

        return ResponseEntity.ok(productService.update(product).orElseThrow());
    }

    @PutMapping("/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disable(@PathVariable int id) {
        boolean result = productService.changeActive(id, false);
        if (result) {
            return ResponseEntity.ok().body("Producto desactivado correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enable(@PathVariable int id) {
        boolean result = productService.changeActive(id, true);
        if (result) {
            return ResponseEntity.ok().body("Producto desactivado correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
