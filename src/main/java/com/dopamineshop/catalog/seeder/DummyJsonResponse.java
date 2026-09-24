package com.dopamineshop.catalog.seeder;

import java.util.List;

public record DummyJsonResponse(
        List<DummyJsonProductDto> products,
        int total,
        int skip,
        int limit
) {}