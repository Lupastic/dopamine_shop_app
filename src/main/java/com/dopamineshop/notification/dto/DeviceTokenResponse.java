package com.dopamineshop.notification.dto;

import java.util.UUID;

public record DeviceTokenResponse(
        UUID id,
        String platform
) {}
