package com.example.rewards.exception;

import com.example.rewards.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            CustomerNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleBadRequest(
            Exception exception, HttpServletRequest request) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        if (exception instanceof MethodArgumentNotValidException validationException) {
            validationException.getBindingResult().getFieldErrors().forEach(error ->
                    validationErrors.put(error.getField(), error.getDefaultMessage()));
        }
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        log.error("Unexpected API error on path={}", request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.", request, Map.of());
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status, String message, HttpServletRequest request,
            Map<String, String> validationErrors) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), status.getReasonPhrase(),
                message == null ? status.getReasonPhrase() : message,
                request.getRequestURI(), validationErrors));
    }
}
