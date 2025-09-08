package com.ikea.warehouse_query_api.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "ArticleCommandRequest", description = "Request payload to create or update an article")
public record ArticleCommandRequest(
        @NotBlank
        @Schema(description = "Name of the inventory item", example = "leg")
        String name,
        @NotNull @PositiveOrZero
        @Schema(description = "Available stock quantity", example = "12")
        Long stock,
        @Size(max = 255)
        @Schema(description = "Optional idempotency marker for last applied message id")
        String lastMessageId,
        @Schema(description = "Optional file created timestamp")
        Instant fileCreatedAt
) {}
