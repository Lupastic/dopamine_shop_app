package com.dopamineshop.cart;

import com.dopamineshop.cart.dto.AddToCartRequest;
import com.dopamineshop.cart.dto.CartResponse;
import com.dopamineshop.cart.dto.UpdateQuantityRequest;
import com.dopamineshop.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItem(user, request));
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(user, productId, request.quantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(cartService.removeItem(user, productId));
    }
}
