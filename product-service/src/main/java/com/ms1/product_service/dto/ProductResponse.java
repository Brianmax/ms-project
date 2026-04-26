package com.ms1.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
