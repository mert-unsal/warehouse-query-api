package com.ikea.warehouse_query_api.exception;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String INVALID_INVENTORY_DATA = "Invalid inventory data";
    public static final String INVALID_PRODUCTS_DATA = "Invalid products data";

    public static final String INVALID_JSON_FORMAT_PREFIX = "Invalid JSON format in uploaded file: ";
    public static final String CONTENT_TYPE_NOT_SUPPORTED = "Content type not supported. Please use multipart/form-data for file uploads.";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
}
