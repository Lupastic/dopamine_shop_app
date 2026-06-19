package com.dopamineshop.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
        @Size(max = 50) String label,

        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 255) String street,

        @Size(max = 20) String house,
        @Size(max = 20) String apartment,
        @Size(max = 20) String entrance,
        @Size(max = 10) String floor,

        String comment,
        Boolean isDefault
) {}