package com.ikea.warehouse_query_api.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(name = "ProductResponse", description = "Response DTO representing a product")
public record ProductResponse(
        String id,
        String name,
        List<ArticleAmount> containArticles,
        Long version,
        Instant createdDate,
        Instant lastModifiedDate,
        String createdBy,
        String lastModifiedBy
) {}
