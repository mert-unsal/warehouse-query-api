package com.ikea.warehouse_query_api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleJsonProcessingException_returnsBadRequest() {
        JsonProcessingException ex = new JsonProcessingException("bad json") {};
        ResponseEntity<ErrorResponse> response = handler.handleJsonProcessingException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.INVALID_JSON_FORMAT);
    }

    @Test
    void handleHttpMediaTypeNotSupported_returnsUnsupportedMediaType() {
        HttpMediaTypeNotSupportedExceptionStub ex = new HttpMediaTypeNotSupportedExceptionStub("unsupported");
        ResponseEntity<ErrorResponse> response = handler.handleHttpMediaTypeNotSupportedException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void handleIllegalArgument_notFoundMessageMapsTo404() {
        IllegalArgumentException ex = new IllegalArgumentException("Resource not found");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.NOT_FOUND);
    }

    @Test
    void handleIllegalArgument_otherMessageMapsTo400() {
        IllegalArgumentException ex = new IllegalArgumentException("Some invalid arg");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.INVALID_ARGUMENT);
    }

    @Test
    void handleMethodArgumentNotValid_inventoryPathMessage() {
        var ex = Mockito.mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");
        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.FILE_PROCESSING_ERROR);
        assertThat(response.getBody().message()).isEqualTo(ErrorMessages.INVALID_INVENTORY_DATA);
    }

    @Test
    void handleMethodArgumentNotValid_productsPathMessage() {
        var ex = Mockito.mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
        when(request.getRequestURI()).thenReturn("/api/v1/products");
        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.FILE_PROCESSING_ERROR);
        assertThat(response.getBody().message()).isEqualTo(ErrorMessages.INVALID_PRODUCTS_DATA);
    }

    @Test
    void handleOptimisticLocking_returnsConflict() {
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("conflict");
        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLocking(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.OPTIMISTIC_LOCK_CONFLICT);
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new Exception("boom");
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(ErrorTypes.INTERNAL_SERVER_ERROR);
    }

    // Minimal stub to avoid bringing Spring MVC machinery
    static class HttpMediaTypeNotSupportedExceptionStub extends org.springframework.web.HttpMediaTypeNotSupportedException {
        public HttpMediaTypeNotSupportedExceptionStub(String message) {
            super(message);
        }
    }
}
