package com.ikea.warehouse_query_api.exception;

public final class ErrorTypes {
    private ErrorTypes() {}

    public static final String FILE_PROCESSING_ERROR = "FILE_PROCESSING_ERROR";
    public static final String INVALID_JSON_FORMAT = "INVALID_JSON_FORMAT";
    public static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";
    public static final String MULTIPART_ERROR = "MULTIPART_ERROR";
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    public static final String OPTIMISTIC_LOCK_CONFLICT = "OPTIMISTIC_LOCK_CONFLICT";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
}
