package com.safra.stock.safra_stock.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.RestController;

import com.safra.stock.safra_stock.entities.EmailPedidoRequest;
import com.safra.stock.safra_stock.entities.Local;
import com.safra.stock.safra_stock.entities.Order;
import com.safra.stock.safra_stock.entities.OrderDTO;
import com.safra.stock.safra_stock.entities.OrderMapper;
import com.safra.stock.safra_stock.entities.Product;
import com.safra.stock.safra_stock.entities.ProductInOrder;
import com.safra.stock.safra_stock.entities.ProductInOrderKey;
import com.safra.stock.safra_stock.entities.ProductPedidoDTO;
import com.safra.stock.safra_stock.entities.ProductQuantityDTO;
import com.safra.stock.safra_stock.entities.User;
import com.safra.stock.safra_stock.repositories.LocalRepository;
import com.safra.stock.safra_stock.repositories.ProductRepository;
import com.safra.stock.safra_stock.services.EmailService;
import com.safra.stock.safra_stock.services.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/safra-stock/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderMapper mapper;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping()
    public List<OrderDTO> list() {
        List<Order> orders = service.findAll();
        return orders.stream()
                .map(order -> mapper.toDto(order))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        Order order = new Order();
        order.setLocal(orderDTO.getLocal());
        order.setActive(true);

        Set<ProductInOrder> productsInOrder = orderDTO.getProducts().stream().map(p -> {
            Product product = productRepository.findByName(p.getProductName())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + p.getProductName()));

            ProductInOrder pio = new ProductInOrder();
            ProductInOrderKey key = new ProductInOrderKey();
            key.setProductId(p.getId());
            pio.setId(key);

            pio.setOrder(order);
            pio.setProduct(product);
            pio.setQuantity(p.getQuantity());

            return pio;
        }).collect(Collectors.toSet());

        order.setProductsInOrder(productsInOrder);
        order.setDate(LocalDateTime.now());

        Order savedOrder = service.save(order);
        OrderDTO responseDTO = mapper.toDto(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult result,
            @PathVariable int id) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        Optional<Order> orderOptional = service.findById(id);
        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Order previousOrder = orderOptional.get();
        previousOrder.setLocal(orderDTO.getLocal());
        previousOrder.setDate(orderDTO.getDate() != null ? orderDTO.getDate() : LocalDateTime.now());

        if (orderDTO.getProducts() != null) {
            previousOrder.getProductsInOrder().clear();

            Set<ProductInOrder> productsInOrder = orderDTO.getProducts().stream().map(p -> {
                Product product = productRepository.findByName(p.getProductName())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + p.getProductName()));

                ProductInOrder pio = new ProductInOrder();
                ProductInOrderKey key = new ProductInOrderKey();
                key.setProductId(product.getId());
                key.setOrderId(previousOrder.getId());
                pio.setId(key);

                pio.setOrder(previousOrder);
                pio.setProduct(product);
                pio.setQuantity(p.getQuantity());

                return pio;
            }).collect(Collectors.toSet());

            previousOrder.getProductsInOrder().addAll(productsInOrder);
        }

        return ResponseEntity.ok(service.save(previousOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        Optional<Order> orderOptional = service.findById(id);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        OrderDTO dto = new OrderDTO();
        dto.setActive(order.isActive());
        dto.setDate(order.getDate());
        dto.setLocal(order.getLocal());
        dto.setOrderId(order.getId());

        // Convertir Set<ProductInOrder> a List<ProductQuantityDTO>
        List<ProductQuantityDTO> productDTOs = order.getProductsInOrder().stream()
                .map(pio -> new ProductQuantityDTO(
                        pio.getProduct().getName(),
                        pio.getQuantity()))
                .collect(Collectors.toList());

        dto.setProducts(productDTOs);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/send-order-notification")
    public ResponseEntity<?> sendOrderNotification(@RequestBody EmailPedidoRequest request) {
        System.out.println(">> Petición recibida para enviar email a local: " + request.getLocalName());

        Local local = localRepository.findByName(request.getLocalName()).orElse(null);
        if (local == null) {
            System.out.println(">> Local NO encontrado");
            return ResponseEntity.badRequest().body("Local no encontrado!");
        }

        List<User> workers = local.getWorkers();
        System.out.println(">> Usuarios del local: " + workers.size());

        // Construir el contenido HTML
        StringBuilder htmlBody = new StringBuilder();
        htmlBody.append("<h2 style='color:#2E8B57;'>Nuevo pedido generado en ")
                .append(request.getLocalName())
                .append("</h2>");

        if (request.getMessage() != null && !request.getMessage().isEmpty()) {
            htmlBody.append("<p>").append(request.getMessage()).append("</p>");
        }

        htmlBody.append("<h3>Productos solicitados:</h3>");
        htmlBody.append("<table style='border-collapse: collapse; width: 100%;'>");
        htmlBody.append(
                "<tr style='background-color:#f2f2f2;'><th style='border: 1px solid #ddd; padding: 8px;'>Producto</th><th style='border: 1px solid #ddd; padding: 8px;'>Cantidad</th></tr>");

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (ProductPedidoDTO p : request.getProducts()) {
                htmlBody.append("<tr>")
                        .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(p.getProductName())
                        .append("</td>")
                        .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(p.getQuantity())
                        .append("</td>")
                        .append("</tr>");
            }
        } else {
            htmlBody.append(
                    "<tr><td colspan='2' style='border: 1px solid #ddd; padding: 8px;'>No se especificaron productos</td></tr>");
        }
        htmlBody.append("</table>");
        htmlBody.append("<p>Por favor, prepara los productos para su entrega.</p>");

        // Enviar correo a cada trabajador
        for (User user : workers) {
            System.out.println(">> Enviando email a: " + user.getEmail());
            emailService.sendEmail(
                    user.getEmail(),
                    "Nuevo pedido realizado",
                    htmlBody.toString(),
                    true // <-- indica que es HTML
            );
        }

        return ResponseEntity.ok("Correos enviados!");
    }

    @PutMapping("/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disable(@PathVariable int id) {
        Optional<Order> orderOptional = service.findById(id);
        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Order order = orderOptional.get();
        return ResponseEntity.ok().body(service.changeActive(order, false));
    }

    @PutMapping("/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enable(@PathVariable int id) {
        Optional<Order> orderOptional = service.findById(id);
        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Order order = orderOptional.get();
        return ResponseEntity.ok().body(service.changeActive(order, true));
    }

    public ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
