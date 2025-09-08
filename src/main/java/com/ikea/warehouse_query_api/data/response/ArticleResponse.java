package com.ikea.warehouse_query_api.data.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ArticleResponse", description = "Article Inventory response payload")
public record ArticleResponse(
        @Schema(description = "Unique article identifier", example = "650f2c5f1a2b3c4d5e6f7a8b")
        String id,
        String name,
        Long stock,
        String lastMessageId,
        Long version,
        Instant createdDate,
        Instant lastModifiedDate,
        Instant fileCreatedAt
) {
}