package com.ms2.order_service.client;

import com.ms2.order_service.dto.ProductCatalogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service"
)
public interface ProductCatalogClient {

    @GetMapping("/api/products/{id}")
    ProductCatalogResponse getProductById(@PathVariable("id") Long id);
}
