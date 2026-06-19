package com.dopamineshop.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String title,
        String imageUrl,
        BigDecimal price,
        String currency,
        int quantity,
        BigDecimal lineTotal
) {}
