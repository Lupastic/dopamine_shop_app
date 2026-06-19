package com.dopamineshop.order;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void advanceOrderStatuses() {
        orderService.advanceDueStatuses();
    }
}
