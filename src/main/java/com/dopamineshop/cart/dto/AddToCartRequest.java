package com.dopamineshop.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddToCartRequest(
        @NotNull UUID productId,
        @Min(1) Integer quantity // null допустим — по умолчанию 1, обрабатывается в сервисе
) {}
