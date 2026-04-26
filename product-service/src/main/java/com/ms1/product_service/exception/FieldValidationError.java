package com.ms1.product_service.exception;

public record FieldValidationError(
        String field,
        String message
) {
}
