package com.dopamineshop.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String title,
        String brand,
        String description,
        BigDecimal price,
        String currency,
        String category,
        String imageUrl,
        boolean exclusive
) {}
