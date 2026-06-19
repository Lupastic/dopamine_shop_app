package com.dopamineshop.order;

import com.dopamineshop.order.dto.CreateOrderRequest;
import com.dopamineshop.order.dto.OrderResponse;
import com.dopamineshop.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.checkout(user, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(user, id));
    }
}