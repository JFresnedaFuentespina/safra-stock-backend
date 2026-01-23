package com.safra.stock.safra_stock.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safra.stock.safra_stock.entities.CocinaCentralStockRequest;
import com.safra.stock.safra_stock.entities.Order;
import com.safra.stock.safra_stock.entities.OrderShipment;
import com.safra.stock.safra_stock.entities.OrderShipmentItemDTO;
import com.safra.stock.safra_stock.entities.OrderShipmentProduct;
import com.safra.stock.safra_stock.entities.Product;
import com.safra.stock.safra_stock.entities.ProductItem;
import com.safra.stock.safra_stock.entities.ProductsCocinaCentral;
import com.safra.stock.safra_stock.entities.StockDateCocinaCentral;
import com.safra.stock.safra_stock.repositories.OrderRepository;
import com.safra.stock.safra_stock.repositories.OrderShipmentRepository;
import com.safra.stock.safra_stock.repositories.ProductRepository;
import com.safra.stock.safra_stock.repositories.ProductsCocinaCentralRepository;
import com.safra.stock.safra_stock.repositories.StockDateCocinaCentralRepository;

@Service
@Transactional
public class StockDateCocinaCentralServiceImpl implements StockDateCocinaCentralService {
    @Autowired
    private StockDateCocinaCentralRepository stockDateRepo;

    @Autowired
    private ProductsCocinaCentralRepository productsCocinaRepo;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderShipmentRepository orderShipmentRepository;

    public StockDateCocinaCentralServiceImpl(StockDateCocinaCentralRepository stockDateRepo) {
        this.stockDateRepo = stockDateRepo;
    }

    @Override
    public List<StockDateCocinaCentral> findAll() {
        return stockDateRepo.findAll();
    }

    @Override
    public Optional<StockDateCocinaCentral> findById(int id) {
        return stockDateRepo.findById(id);
    }

    @Override
    public StockDateCocinaCentral save(StockDateCocinaCentral stockDate) {
        return stockDateRepo.save(stockDate);
    }

    @Override
    public List<StockDateCocinaCentral> findByDate(LocalDate date) {
        return stockDateRepo.findByDate(date);
    }

    @Override
    @Transactional
    public void createNewStockWithProducts(CocinaCentralStockRequest request) {
        for (ProductItem item : request.getProducts()) {

            Product productEntity = productRepository.findByName(item.getProductName())
                    .orElseGet(() -> {
                        Product newProduct = new Product();
                        newProduct.setName(item.getProductName());
                        return productRepository.save(newProduct);
                    });

            ProductsCocinaCentral productStock = new ProductsCocinaCentral();
            productStock.setLocalName(item.getLocalName() != null ? item.getLocalName() : "Cocina Central");
            productStock.setProduct(productEntity);
            productStock.setStock(item.getQuantity());
            productStock.setDate(item.getDate() != null
                    ? item.getDate().atStartOfDay()
                    : (request.getDate() != null ? request.getDate().atStartOfDay() : LocalDate.now().atStartOfDay()));

            productsCocinaRepo.save(productStock);

            StockDateCocinaCentral relation = new StockDateCocinaCentral();
            relation.setProduct(productStock);
            relation.setDate(LocalDate.now());

            stockDateRepo.save(relation);
        }
    }

    @Override
    public List<StockDateCocinaCentral> findAllWithProducts() {
        return stockDateRepo.findAllWithProducts();
    }

    @Override
    public List<StockDateCocinaCentral> findLastStock() {
        StockDateCocinaCentral lastStockEntry = stockDateRepo.findTopByOrderByDateDesc();

        if (lastStockEntry == null) {
            return List.of();
        }

        LocalDate lastDate = lastStockEntry.getDate();

        return stockDateRepo.findByDate(lastDate);
    }

    @Override
    @Transactional
    public void updateLastStockWithProducts(List<OrderShipmentItemDTO> products) {

        // 1. Obtener el último registro de stockDate
        StockDateCocinaCentral lastStockEntry = stockDateRepo.findTopByOrderByDateDesc();

        if (lastStockEntry == null) {
            throw new RuntimeException("No existe ningún stock registrado.");
        }

        LocalDate lastDate = lastStockEntry.getDate();

        // 2. Obtener todos los registros del último día
        List<StockDateCocinaCentral> lastStockList = stockDateRepo.findByDate(lastDate);

        // 3. Crear un mapa por nombre de producto para acceso rápido
        // Agrupar por nombre pero guardando todos los registros
        Map<String, List<StockDateCocinaCentral>> stockMap = lastStockList.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getProduct().getProduct().getName().trim().toLowerCase()));

        // Procesar productos recibidos
        for (OrderShipmentItemDTO item : products) {

            String key = item.getProductName().trim().toLowerCase();
            int quantityToSubtract = item.getQuantity();

            if (!stockMap.containsKey(key))
                continue;

            // Obtener todos los registros de ese producto
            List<StockDateCocinaCentral> stockEntries = stockMap.get(key);

            // Filtrar SOLO los que coinciden con la fecha
            LocalDate targetDate = item.getDate();

            List<ProductsCocinaCentral> matchingStocks = stockEntries.stream()
                    .map(StockDateCocinaCentral::getProduct)
                    .filter(p -> p.getDate().toLocalDate().isEqual(targetDate))
                    .toList();

            // Restar cantidades en orden
            for (ProductsCocinaCentral productStock : matchingStocks) {

                if (quantityToSubtract <= 0)
                    break;

                int available = productStock.getStock();
                int subtract = Math.min(available, quantityToSubtract);

                productStock.setStock(available - subtract);
                productsCocinaRepo.save(productStock);

                quantityToSubtract -= subtract;
            }
        }

        System.out.println("ACTUALIZACIÓN ÚLTIMO STOCK!");
        lastStockList.forEach(item -> System.out.println(" - " + item));

        // 5. Guardar relaciones StockDate (por si cambió algo)
        stockDateRepo.saveAll(lastStockList);
    }

    @Override
    @Transactional
    public void generateNewStockFromLast(List<OrderShipmentItemDTO> products) {

        // 1. Último stock
        StockDateCocinaCentral lastStockEntry = stockDateRepo.findTopByOrderByDateDesc();
        if (lastStockEntry == null) {
            throw new RuntimeException("No existe stock previo");
        }

        LocalDate lastDate = lastStockEntry.getDate();
        List<StockDateCocinaCentral> lastStockList = stockDateRepo.findByDate(lastDate);

        LocalDate today = LocalDate.now();

        // 2. DEDUPLICAR por producto (CLAVE IMPORTANTE)
        Map<Integer, ProductsCocinaCentral> stockPorProducto = lastStockList.stream()
                .map(StockDateCocinaCentral::getProduct)
                .collect(Collectors.toMap(
                        p -> p.getProduct().getId(),
                        p -> p,
                        (a, b) -> a.getStock() >= b.getStock() ? a : b));

        // 3. Clonar stock
        Map<Integer, ProductsCocinaCentral> newStockMap = stockPorProducto.values()
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getProduct().getId(),
                        p -> {
                            ProductsCocinaCentral copy = new ProductsCocinaCentral();
                            copy.setLocalName(p.getLocalName());
                            copy.setProduct(p.getProduct());
                            copy.setStock(p.getStock());
                            copy.setDate(today.atStartOfDay());
                            return productsCocinaRepo.save(copy);
                        }));

        // 4. Aplicar restas
        for (OrderShipmentItemDTO item : products) {

            Product product = productRepository.findByName(item.getProductName())
                    .orElse(null);

            if (product == null)
                continue;

            ProductsCocinaCentral productStock = newStockMap.get(product.getId());
            if (productStock == null)
                continue;

            int available = productStock.getStock();
            int subtract = Math.min(available, item.getQuantity());

            productStock.setStock(available - subtract);
            productsCocinaRepo.save(productStock);
        }

        // 5. Crear relaciones StockDate
        for (ProductsCocinaCentral product : newStockMap.values()) {
            StockDateCocinaCentral relation = new StockDateCocinaCentral();
            relation.setProduct(product);
            relation.setDate(today);
            stockDateRepo.save(relation);
        }

        System.out.println("NUEVO STOCK GENERADO PARA FECHA: " + today);
    }

    @Transactional
    public void registerOrderShipment(List<OrderShipmentItemDTO> items) {

        if (items.isEmpty())
            return;

        Integer orderId = items.get(0).getOrderId();
        if (orderId == null || orderId == 0) {
            throw new RuntimeException("orderId inválido en payload");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));

        OrderShipment shipment = new OrderShipment();
        shipment.setOrder(order);
        shipment.setShipmentDate(LocalDateTime.now());

        List<OrderShipmentProduct> products = items.stream()
                .map(item -> {
                    Product product = productRepository.findByName(item.getProductName())
                            .orElseThrow(
                                    () -> new RuntimeException("Producto no encontrado: " + item.getProductName()));

                    OrderShipmentProduct p = new OrderShipmentProduct();
                    p.setProduct(product);
                    p.setQuantity(item.getQuantity());
                    p.setStockDate(item.getDate());
                    p.setShipment(shipment);
                    return p;
                })
                .toList();

        shipment.setProducts(products);

        orderShipmentRepository.save(shipment);
    }

}
