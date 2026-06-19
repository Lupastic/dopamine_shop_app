package com.dopamineshop.order;

import com.dopamineshop.user.User;
import com.dopamineshop.user.UserAddress;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private UserAddress address;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "KZT";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OrderStatus status = OrderStatus.PROCESSING;

    @Column(name = "placed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant placedAt;

    @Column(name = "estimated_arrival")
    private Instant estimatedArrival;

    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Column(name = "next_status_at")
    private Instant nextStatusAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public void recordStatusChange(OrderStatus status, Instant changedAt) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(this)
                .status(status)
                .changedAt(changedAt)
                .build();
        statusHistory.add(history);
    }
}