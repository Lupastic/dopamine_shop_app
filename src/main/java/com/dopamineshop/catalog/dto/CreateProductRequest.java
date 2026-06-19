package com.dopamineshop.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank String title,
        String brand,
        String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        String currency,
        String category,
        @NotBlank String imageUrl,
        boolean exclusive
) {}
