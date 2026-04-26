package com.ms2.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Integer quantity,
        BigDecimal totalPrice,
        String status,
        LocalDateTime createdAt,
        ProductSummaryResponse product
) {
}
