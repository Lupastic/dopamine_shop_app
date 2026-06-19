package com.dopamineshop.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
            SELECT DISTINCT o FROM Order o
            LEFT JOIN FETCH o.items
            LEFT JOIN FETCH o.address
            WHERE o.user.id = :userId
            ORDER BY o.placedAt DESC
            """)
    List<Order> findAllByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.items
            LEFT JOIN FETCH o.address
            WHERE o.id = :orderId AND o.user.id = :userId
            """)
    Optional<Order> findDetailsByIdAndUserId(@Param("orderId") UUID orderId, @Param("userId") UUID userId);

    @Query("""
            SELECT o FROM Order o
            WHERE o.nextStatusAt IS NOT NULL AND o.nextStatusAt <= :now
            """)
    List<Order> findOrdersDueForStatusChange(@Param("now") Instant now);

    @Query("""
            SELECT i.product.category, SUM(i.quantity)
            FROM Order o
            JOIN o.items i
            WHERE o.user.id = :userId AND i.product.category IS NOT NULL
            GROUP BY i.product.category
            ORDER BY SUM(i.quantity) DESC
            """)
    List<Object[]> findFavoriteCategories(@Param("userId") UUID userId, Pageable pageable);
}