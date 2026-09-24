package com.dopamineshop.catalog.seeder;

import com.dopamineshop.catalog.Product;
import com.dopamineshop.catalog.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeederService {

    private final DummyJsonClient dummyJsonClient;
    private final ProductRepository productRepository;

    @Transactional
    public void seedProducts() {
        List<DummyJsonProductDto> dummyProducts = dummyJsonClient.fetchProducts().block();

        if (dummyProducts == null || dummyProducts.isEmpty()) {
            log.warn("No products received from DummyJSON");
            return;
        }

        int created = 0;
        int skipped = 0;

        for (DummyJsonProductDto dto : dummyProducts) {
            if (productRepository.existsByExternalId(dto.id())) {
                skipped++;
                continue;
            }

            Product product = Product.builder()
                    .externalId(dto.id())
                    .title(dto.title())
                    .brand(dto.brand())
                    .description(dto.description())
                    .price(BigDecimal.valueOf(dto.price()).setScale(2, RoundingMode.HALF_UP))
                    .currency("KZT")
                    .category(dto.category())
                    .imageUrl(dto.thumbnail())
                    .exclusive(dto.rating() > 4.5 || dto.price() > 500)
                    .build();

            productRepository.save(product);
            created++;
        }
        log.info("Products: {} created, {} skipped (already exist)", created, skipped);
    }
}
