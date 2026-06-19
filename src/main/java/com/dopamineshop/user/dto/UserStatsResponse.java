package com.dopamineshop.user.dto;

import java.math.BigDecimal;
import java.util.List;

public record UserStatsResponse(
        BigDecimal totalSavedAmount,
        int totalOrdersCount,
        List<String> favoriteCategories
) {}
