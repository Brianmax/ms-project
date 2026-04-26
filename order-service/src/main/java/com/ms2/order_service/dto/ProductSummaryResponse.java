package com.ms2.order_service.dto;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,
        String name,
        String sku,
        BigDecimal unitPrice,
        String source
) {
}
