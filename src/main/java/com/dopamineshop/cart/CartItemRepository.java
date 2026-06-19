package com.dopamineshop.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @Query("""
            SELECT ci FROM CartItem ci
            JOIN FETCH ci.product
            WHERE ci.user.id = :userId
            ORDER BY ci.addedAt DESC
            """)
    List<CartItem> findAllByUserId(@Param("userId") UUID userId);

    Optional<CartItem> findByUserIdAndProductId(UUID userId, UUID productId);

    void deleteByUserIdAndProductId(UUID userId, UUID productId);

    void deleteByUserId(UUID userId);
}
