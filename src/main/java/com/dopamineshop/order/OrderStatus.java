package com.dopamineshop.order;

import java.time.Duration;
import java.util.Optional;

public enum OrderStatus {
    PROCESSING(Duration.ofHours(2)),
    SORTING(Duration.ofHours(12)),
    IN_TRANSIT(Duration.ofDays(2)),
    ARRIVED(null);

    private final Duration timeToNext;

    OrderStatus(Duration timeToNext) {
        this.timeToNext = timeToNext;
    }

    public Optional<OrderStatus> next() {
        return switch (this) {
            case PROCESSING -> Optional.of(SORTING);
            case SORTING -> Optional.of(IN_TRANSIT);
            case IN_TRANSIT -> Optional.of(ARRIVED);
            case ARRIVED -> Optional.empty();
        };
    }

    public Optional<Duration> timeToNext() {
        return Optional.ofNullable(timeToNext);
    }
}
