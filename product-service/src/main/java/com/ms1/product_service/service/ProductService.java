package com.ms1.product_service.service;

import com.ms1.product_service.dto.CreateProductRequest;
import com.ms1.product_service.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest request);
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
}
