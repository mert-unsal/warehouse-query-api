package com.ikea.warehouse_query_api.data.dto;

import com.ikea.warehouse_query_api.data.document.ProductDocument;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Wrapper for products data containing list of products")
public record ProductsData(
    @Schema(description = "List of products")
    List<ProductDocument> productDocuments
) {}
