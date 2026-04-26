package com.ms2.order_service.dto;

import java.math.BigDecimal;

public record ProductSnapshot(
        Long id,
        String name,
        String sku,
        BigDecimal unitPrice,
        ProductSource source
) {
}
