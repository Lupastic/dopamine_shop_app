package com.dopamineshop.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        String currency,
        int totalItemsCount
) {}
