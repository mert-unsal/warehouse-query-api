package com.ikea.warehouse_query_api.data.document;

import io.swagger.v3.oas.annotations.media.Schema;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ArticleInventory")
@Schema(description = "Inventory item representing a warehouse article")
public record ArticleDocument(
    @Id
    @Schema(description = "Unique article identifier", example = "1")
    String id,

    @Schema(description = "Name of the inventory item", example = "leg")
    @Indexed
    String name,

    @Schema(description = "Available stock quantity", example = "12")
    Long stock,

    @Schema(description = "Optional idempotency marker for last applied message id")
    String lastMessageId,

    @Version
    @Schema(description = "Optimistic lock version", example = "0")
    Long version,

    @CreatedDate
    Instant createdDate,

    @LastModifiedDate
    Instant lastModifiedDate,

    Instant fileCreatedAt
) {}
