package com.dopamineshop.catalog.seeder;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class DummyJsonClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://dummyjson.com")
            .build();

    /**
     * Получает 100 товаров (максимум бесплатно)
     */
    public Mono<List<DummyJsonProductDto>> fetchProducts() {
        return webClient.get()
                .uri("/products?limit=100")
                .retrieve()
                .bodyToMono(DummyJsonResponse.class)
                .map(DummyJsonResponse::products);
    }

    /**
     * Получает список категорий
     */
    public Mono<List<String>> fetchCategories() {
        return webClient.get()
                .uri("/products/categories")
                .retrieve()
                .bodyToMono(String[].class)
                .map(List::of);
    }
}