package com.dopamineshop.catalog.seeder;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DummyJsonProductDto(
        int id,
        String title,
        String description,
        double price,

        @JsonProperty("discountPercentage")
        double discountPercentage,

        double rating,
        int stock,
        String brand,
        String category,
        String thumbnail,
        List<String> images
) {}