package com.dopamineshop.catalog;

import com.dopamineshop.catalog.dto.CreateProductRequest;
import com.dopamineshop.catalog.dto.ProductFeedResponse;
import com.dopamineshop.catalog.dto.ProductResponse;
import com.dopamineshop.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ProductRepository productRepository;

    public ProductFeedResponse getFeed(String category, boolean exclusiveOnly, String cursor) {
        Instant cursorCreatedAt = null;
        UUID cursorId = null;

        if (cursor != null && !cursor.isBlank()) {
            String[] parts = decodeCursor(cursor);
            cursorCreatedAt = Instant.parse(parts[0]);
            cursorId = UUID.fromString(parts[1]);
        }

        // запрашиваем на 1 больше, чтобы понять, есть ли следующая страница
        List<Product> products = productRepository.findFeed(
                category, exclusiveOnly, cursorCreatedAt, cursorId,
                PageRequest.of(0, DEFAULT_PAGE_SIZE + 1)
        );

        boolean hasMore = products.size() > DEFAULT_PAGE_SIZE;
        List<Product> pageItems = hasMore ? products.subList(0, DEFAULT_PAGE_SIZE) : products;

        String nextCursor = null;
        if (hasMore) {
            Product last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getCreatedAt(), last.getId());
        }

        List<ProductResponse> items = pageItems.stream().map(this::toResponse).toList();
        return new ProductFeedResponse(items, nextCursor);
    }

    public ProductResponse getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Товар не найден"));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = Product.builder()
                .title(request.title())
                .brand(request.brand())
                .description(request.description())
                .price(request.price())
                .currency(request.currency() != null ? request.currency() : "KZT")
                .category(request.category())
                .imageUrl(request.imageUrl())
                .exclusive(request.exclusive())
                .build();

        productRepository.save(product);
        return toResponse(product);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getTitle(), p.getBrand(), p.getDescription(),
                p.getPrice(), p.getCurrency(), p.getCategory(), p.getImageUrl(), p.isExclusive()
        );
    }

    private String encodeCursor(Instant createdAt, UUID id) {
        String raw = createdAt.toString() + "|" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String[] decodeCursor(String cursor) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            return raw.split("\\|", 2);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Невалидный курсор пагинации");
        }
    }
}
