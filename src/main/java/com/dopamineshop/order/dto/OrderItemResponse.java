package com.dopamineshop.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productId,
        String title,
        String imageUrl,
        int quantity,
        BigDecimal priceAtPurchase,
        BigDecimal lineTotal
) {}
