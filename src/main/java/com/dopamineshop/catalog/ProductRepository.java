package com.dopamineshop.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Keyset-пагинация ленты товаров по (createdAt, id) desc — стабильна
     * при добавлении новых товаров (в отличие от OFFSET-пагинации).
     * cursorCreatedAt/cursorId передаются null для первой страницы.
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (CAST(:category AS string) IS NULL OR p.category = :category)
              AND (:exclusiveOnly = false OR p.exclusive = true)
              AND (
                    CAST(:cursorCreatedAt AS timestamp) IS NULL
                    OR p.createdAt < :cursorCreatedAt
                    OR (p.createdAt = :cursorCreatedAt AND p.id < CAST(:cursorId AS uuid))
                  )
            ORDER BY p.createdAt DESC, p.id DESC
            """)
    List<Product> findFeed(
            @Param("category") String category,
            @Param("exclusiveOnly") boolean exclusiveOnly,
            @Param("cursorCreatedAt") Instant cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable
    );
}
