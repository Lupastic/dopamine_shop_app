package com.dopamineshop.user.dto;

import java.util.UUID;

public record UserAddressDto(
        UUID id,
        String label,
        String city,
        String street,
        String house,
        String apartment,
        String entrance,
        String floor,
        String comment,
        Boolean isDefault
) {}