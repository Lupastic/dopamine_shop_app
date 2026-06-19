package com.dopamineshop.user;

import com.dopamineshop.order.OrderService;
import com.dopamineshop.user.dto.UserStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserStatsController {

    private final OrderService orderService;

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserStatsResponse(
                user.getTotalSavedAmount(),
                user.getTotalOrdersCount(),
                orderService.favoriteCategories(user, 3)
        ));
    }
}
