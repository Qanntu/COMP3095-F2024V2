package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    void placeOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long orderId);
    String updateOrder(Long orderId, OrderRequest orderRequest);
    void deleteOrder(Long orderId);


    //ProductResponse getOneProduct(String productId);

}


