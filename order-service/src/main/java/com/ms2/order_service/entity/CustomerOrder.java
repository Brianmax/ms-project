package com.ms2.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 120)
    private String productNameSnapshot;

    @Column(nullable = false, length = 40)
    private String productSkuSnapshot;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceSnapshot;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected CustomerOrder() {
    }

    public CustomerOrder(
            Long productId,
            Integer quantity,
            String productNameSnapshot,
            String productSkuSnapshot,
            BigDecimal unitPriceSnapshot,
            BigDecimal totalPrice,
            OrderStatus status
    ) {
        this.productId = productId;
        this.quantity = quantity;
        this.productNameSnapshot = productNameSnapshot;
        this.productSkuSnapshot = productSkuSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public String getProductNameSnapshot() { return productNameSnapshot; }
    public String getProductSkuSnapshot() { return productSkuSnapshot; }
    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
