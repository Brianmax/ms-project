package com.ms2.order_service.mapper;

import com.ms2.order_service.dto.OrderResponse;
import com.ms2.order_service.dto.ProductSnapshot;
import com.ms2.order_service.dto.ProductSummaryResponse;
import com.ms2.order_service.entity.CustomerOrder;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(CustomerOrder order, ProductSnapshot snapshot) {
        return new OrderResponse(
                order.getId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt(),
                new ProductSummaryResponse(
                        snapshot.id(),
                        snapshot.name(),
                        snapshot.sku(),
                        snapshot.unitPrice(),
                        snapshot.source().name()
                )
        );
    }
}
