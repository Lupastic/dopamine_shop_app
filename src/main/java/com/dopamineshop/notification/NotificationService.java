package com.dopamineshop.notification;

import com.dopamineshop.order.Order;
import com.dopamineshop.order.OrderStatus;
import com.dopamineshop.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendStatusUpdate(User user, Order order, OrderStatus status) {
        log.info("Order {} for user {} moved to {}", order.getId(), user.getId(), status);
    }
}
