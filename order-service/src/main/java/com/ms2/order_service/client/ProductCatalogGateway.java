package com.ms2.order_service.client;

import com.ms2.order_service.dto.ProductCatalogResponse;
import com.ms2.order_service.dto.ProductSnapshot;
import com.ms2.order_service.dto.ProductSource;
import com.ms2.order_service.exception.ProductNotFoundException;
import com.ms2.order_service.exception.ProductUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductCatalogGateway.class);

    private final ProductCatalogClient productCatalogClient;

    public ProductCatalogGateway(ProductCatalogClient productCatalogClient) {
        this.productCatalogClient = productCatalogClient;
    }

    // trae la informacion del producto y se usa para crear una orden
    @CircuitBreaker(name = "product-service", fallbackMethod = "fetchRequiredProductFallback")
    public ProductCatalogResponse fetchRequiredProduct(Long productId) {
        try {
            log.info("event=product_lookup_started productId={}", productId);
            ProductCatalogResponse response = productCatalogClient.getProductById(productId);
            log.info("event=product_lookup_succeeded productId={}", productId);
            return response;
        } catch (FeignException exception) {
            log.error("event=product_lookup_failed productId={} reason={}", productId, exception.getMessage());
                throw new ProductUnavailableException("product-service is currently unavailable");
        }
    }

    private ProductCatalogResponse fetchRequiredProductFallback(Long id) {
        log.error("event=product_lookup_failed_fallback productId={}", id);
        throw new ProductUnavailableException("product-service is currently unavailable");
    }

    // se usa para traer la informacion mas reciente del producto
    // y devolverlo en la informacion de la orden
    @CircuitBreaker(name = "product-service", fallbackMethod = "fetchProductSnapshotFallback")
    public ProductSnapshot fetchProductOrFallback(Long productId, ProductSnapshot fallback) {

        ProductCatalogResponse response = productCatalogClient.getProductById(productId);
        log.info("event=product_lookup_live productId={}", productId);
        return new ProductSnapshot(response.id(), response.name(), response.sku(), response.price(), ProductSource.LIVE);
    }

    private ProductSnapshot fetchProductSnapshotFallback(Long productId, ProductSnapshot fallback, Throwable t) {
        log.warn("event=product_lookup_fallback productId={} reason={}", productId, t.getMessage());
        return new ProductSnapshot(fallback.id(), fallback.name(), fallback.sku(), fallback.unitPrice(), ProductSource.FALLBACK);
    }
}
