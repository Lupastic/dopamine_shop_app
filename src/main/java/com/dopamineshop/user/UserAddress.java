package com.dopamineshop.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 50)
    private String label; // "Дом", "Работа", "Дача"

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 255)
    private String street;

    @Column(length = 20)
    private String house;

    @Column(length = 20)
    private String apartment;

    @Column(length = 20)
    private String entrance;

    @Column(length = 10)
    private String floor;

    @Column
    private String comment;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}