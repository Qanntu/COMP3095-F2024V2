package ca.gbc.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;


@Builder


public record OrderResponse(
        Long id,
        String orderNumber,
        String skuCode,
        BigDecimal price,
        Integer quantity,
        String status

) {}