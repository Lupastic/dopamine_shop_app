package com.dopamineshop.catalog.dto;

import java.util.List;

public record ProductFeedResponse(
        List<ProductResponse> items,
        String nextCursor // base64(createdAt|id), null если страниц больше нет
) {}
