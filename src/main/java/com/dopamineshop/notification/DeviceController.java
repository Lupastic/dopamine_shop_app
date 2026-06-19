package com.dopamineshop.notification;

import com.dopamineshop.notification.dto.DeviceTokenResponse;
import com.dopamineshop.notification.dto.RegisterDeviceRequest;
import com.dopamineshop.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceTokenRepository deviceTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<DeviceTokenResponse> register(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RegisterDeviceRequest request) {
        DeviceToken token = deviceTokenRepository.findByFcmToken(request.fcmToken())
                .map(existing -> {
                    existing.setUser(user);
                    existing.setPlatform(request.platform());
                    existing.setUpdatedAt(Instant.now());
                    return existing;
                })
                .orElseGet(() -> DeviceToken.builder()
                        .user(user)
                        .fcmToken(request.fcmToken())
                        .platform(request.platform())
                        .build());

        DeviceToken saved = deviceTokenRepository.save(token);
        return ResponseEntity.ok(new DeviceTokenResponse(saved.getId(), saved.getPlatform()));
    }
}
