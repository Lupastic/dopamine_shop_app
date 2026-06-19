package com.dopamineshop.order.dto;

import com.dopamineshop.order.OrderStatus;
import com.dopamineshop.user.dto.UserAddressDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        BigDecimal totalAmount,
        String currency,
        OrderStatus status,
        Instant placedAt,
        Instant estimatedArrival,
        Instant arrivedAt,
        UserAddressDto address,
        List<OrderItemResponse> items,
        List<OrderStatusHistoryResponse> statusHistory
) {}