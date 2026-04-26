package com.ms2.order_service.service;

import com.ms2.order_service.client.ProductCatalogGateway;
import com.ms2.order_service.dto.CreateOrderRequest;
import com.ms2.order_service.dto.OrderResponse;
import com.ms2.order_service.dto.ProductCatalogResponse;
import com.ms2.order_service.dto.ProductSnapshot;
import com.ms2.order_service.dto.ProductSource;
import com.ms2.order_service.entity.CustomerOrder;
import com.ms2.order_service.entity.OrderStatus;
import com.ms2.order_service.exception.OrderNotFoundException;
import com.ms2.order_service.mapper.OrderMapper;
import com.ms2.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductCatalogGateway productCatalogGateway;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, ProductCatalogGateway productCatalogGateway, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productCatalogGateway = productCatalogGateway;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        ProductCatalogResponse product = productCatalogGateway.fetchRequiredProduct(request.productId());
        BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(request.quantity()));

        CustomerOrder order = new CustomerOrder(
                product.id(),
                request.quantity(),
                product.name(),
                product.sku(),
                product.price(),
                totalPrice,
                OrderStatus.CREATED
        );

        CustomerOrder saved = orderRepository.save(order);
        log.info("event=order_created orderId={} productId={} quantity={}", saved.getId(), saved.getProductId(), saved.getQuantity());

        ProductSnapshot snapshot = new ProductSnapshot(product.id(), product.name(), product.sku(), product.price(), ProductSource.LIVE);
        return orderMapper.toResponse(saved, snapshot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(this::mapWithFallback).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapWithFallback(order);
    }

    private OrderResponse mapWithFallback(CustomerOrder order) {
        ProductSnapshot fallback = new ProductSnapshot(
                order.getProductId(),
                order.getProductNameSnapshot(),
                order.getProductSkuSnapshot(),
                order.getUnitPriceSnapshot(),
                ProductSource.SNAPSHOT
        );
        ProductSnapshot resolved = productCatalogGateway.fetchProductOrFallback(order.getProductId(), fallback);
        return orderMapper.toResponse(order, resolved);
    }
}
