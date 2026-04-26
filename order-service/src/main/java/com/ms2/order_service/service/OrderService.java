package com.ms2.order_service.service;

import com.ms2.order_service.dto.CreateOrderRequest;
import com.ms2.order_service.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request);
    List<OrderResponse> findAll();
    OrderResponse findById(Long id);
}
