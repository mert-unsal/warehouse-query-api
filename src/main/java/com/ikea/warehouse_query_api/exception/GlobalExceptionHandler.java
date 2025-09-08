package com.ikea.warehouse_query_api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ikea.warehouse_query_api.exception.ErrorTypes.*;


@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex, HttpServletRequest request) {
        log.error("JSON Processing Exception occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            INVALID_JSON_FORMAT,
            ErrorMessages.INVALID_JSON_FORMAT_PREFIX + ex.getOriginalMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("HTTP Media Type Not Supported Exception occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            UNSUPPORTED_MEDIA_TYPE,
            ErrorMessages.CONTENT_TYPE_NOT_SUPPORTED,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            request.getRequestURI(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Illegal Argument Exception occurred: {}", ex.getMessage(), ex);

        String path = request.getRequestURI();
        String message = ex.getMessage();
        boolean notFound = message != null && message.toLowerCase().contains("not found");
        HttpStatus status = notFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        String errorCode = notFound ? NOT_FOUND : INVALID_ARGUMENT;
        String clientMessage = notFound ? ErrorMessages.RESOURCE_NOT_FOUND : message;

        ErrorResponse errorResponse = new ErrorResponse(
            errorCode,
            clientMessage,
            status.value(),
            path,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation failed: {}", ex.getMessage(), ex);
        String uri = request.getRequestURI();
        String message = uri != null && uri.contains("/inventory")
                ? ErrorMessages.INVALID_INVENTORY_DATA
                : ErrorMessages.INVALID_PRODUCTS_DATA;
        ErrorResponse errorResponse = new ErrorResponse(
                FILE_PROCESSING_ERROR,
                message,
                HttpStatus.BAD_REQUEST.value(),
                uri,
                LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(OptimisticLockingFailureException ex, HttpServletRequest request) {
        log.error("Optimistic locking conflict: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                OPTIMISTIC_LOCK_CONFLICT,
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            INTERNAL_SERVER_ERROR,
            ErrorMessages.INTERNAL_SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
