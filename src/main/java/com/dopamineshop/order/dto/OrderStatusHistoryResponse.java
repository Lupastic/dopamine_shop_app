package com.dopamineshop.order.dto;

import com.dopamineshop.order.OrderStatus;

import java.time.Instant;

public record OrderStatusHistoryResponse(
        OrderStatus status,
        Instant changedAt
) {}
