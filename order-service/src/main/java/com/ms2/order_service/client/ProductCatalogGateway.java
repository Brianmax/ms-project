package com.ms2.order_service.client;

import com.ms2.order_service.dto.ProductCatalogResponse;
import com.ms2.order_service.dto.ProductSnapshot;
import com.ms2.order_service.dto.ProductSource;
import com.ms2.order_service.exception.ProductNotFoundException;
import com.ms2.order_service.exception.ProductUnavailableException;
import feign.FeignException;
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

    public ProductCatalogResponse fetchRequiredProduct(Long productId) {
        try {
            log.info("event=product_lookup_started productId={}", productId);
            ProductCatalogResponse response = productCatalogClient.getProductById(productId);
            log.info("event=product_lookup_succeeded productId={}", productId);
            return response;
        } catch (FeignException.NotFound exception) {
            log.warn("event=product_lookup_not_found productId={}", productId);
            throw new ProductNotFoundException(productId);
        } catch (FeignException exception) {
            log.error("event=product_lookup_failed productId={} reason={}", productId, exception.getMessage());
            throw new ProductUnavailableException("product-service is currently unavailable");
        }
    }

    public ProductSnapshot fetchProductOrFallback(Long productId, ProductSnapshot fallback) {
        try {
            ProductCatalogResponse response = productCatalogClient.getProductById(productId);
            log.info("event=product_lookup_live productId={}", productId);
            return new ProductSnapshot(response.id(), response.name(), response.sku(), response.price(), ProductSource.LIVE);
        } catch (FeignException exception) {
            log.warn("event=product_lookup_fallback productId={} reason={}", productId, exception.getMessage());
            return new ProductSnapshot(fallback.id(), fallback.name(), fallback.sku(), fallback.unitPrice(), ProductSource.FALLBACK);
        }
    }
}
