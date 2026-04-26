package com.ms2.order_service.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        String message,
        String path,
        String errorCode,
        List<FieldValidationError> validationErrors
) {
}
