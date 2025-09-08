package com.ikea.warehouse_query_api.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Schema(name = "ProductCommandRequest", description = "Request payload to create or update a product")
public record ProductCommandRequest(
        @NotBlank
        @Schema(description = "Product name", example = "Dining Chair")
        String name,
        @NotEmpty
        @Valid
        @Schema(description = "List of articles required to build this product")
        List<ArticleAmount> containArticles
) {}
