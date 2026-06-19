package com.dopamineshop.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDeviceRequest(
        @NotBlank String fcmToken,
        @Size(max = 20) String platform
) {}
