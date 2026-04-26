package com.ms2.order_service.exception;

public record FieldValidationError(
        String field,
        String message
) {
}
