package com.dopamineshop.order.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "Address is required")
        UUID addressId
) {}