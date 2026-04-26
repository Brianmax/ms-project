package com.ms1.product_service.mapper;

import com.ms1.product_service.dto.CreateProductRequest;
import com.ms1.product_service.dto.ProductResponse;
import com.ms1.product_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(CreateProductRequest request) {
        return new Product(request.name(), request.sku(), request.price());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}
