package com.dopamineshop.user;

import com.dopamineshop.user.dto.CreateAddressRequest;
import com.dopamineshop.user.dto.UserAddressDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService addressService;

    @GetMapping
    public ResponseEntity<List<UserAddressDto>> getMyAddresses(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(addressService.getMyAddresses(user.getId()));
    }

    @PostMapping
    public ResponseEntity<UserAddressDto> createAddress(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createAddress(user.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAddressDto> updateAddress(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        addressService.deleteAddress(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        addressService.setDefaultAddress(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}