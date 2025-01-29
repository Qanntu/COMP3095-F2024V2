package ca.gbc.orderservice.service;

import ca.gbc.orderservice.client.InventoryClient;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.dto.OrderResponse;
import ca.gbc.orderservice.event.OrderPlacedEvent;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Override
    public void placeOrder(OrderRequest orderRequest) {

        // check inventory
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .price(orderRequest.price())
                    .skuCode(orderRequest.skuCode())
                    .quantity(orderRequest.quantity())
                    .build();

            orderRepository.save(order);

            //Send message to Kafta on oreder-placed topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());

            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order--placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("Complete - Sent OrderPlacedEvent {} to Kafka topic order--placed", orderPlacedEvent);
            log.info("Schema: {}", orderPlacedEvent.getSchema());

        } else {
            throw new RuntimeException("Product with skuCode " + orderRequest.skuCode() + "is not in stock");
        }
    }



    // Get all orders
    @Override
    public List<OrderResponse> getAllOrders() {
        log.debug("Retrieving all orders");
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }


    // Get order by ID
    @Override
    public OrderResponse getOrderById(Long orderId) {
        log.debug("Retrieving order with id {}", orderId);
        Optional<Order> order = orderRepository.findById(orderId);
        return order.map(this::mapToOrderResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    // Update an existing order
    @Override
    public String updateOrder(Long orderId, OrderRequest orderRequest) {
        log.debug("Updating order with id {}", orderId);

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setOrderNumber(orderRequest.orderNumber());
            order.setSkuCode(orderRequest.skuCode());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            order.setStatus(orderRequest.status());

            // Save the updated order
            orderRepository.save(order);
            return "Order updated successfully with ID: " + orderId;
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    // Delete an order
    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.debug("Deleting order with id {}", orderId);
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            log.info("Order with id {} has been deleted", orderId);
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    // Helper method to map Order entity to OrderResponse DTO
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .skuCode(order.getSkuCode())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();
    }
}