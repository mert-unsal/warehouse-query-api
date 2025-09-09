package com.ikea.warehouse_query_api.data.dto;

import com.ikea.warehouse_query_api.data.document.ProductDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Product data with product details and available quantity")
public record ProductData(
        @Schema(description = "Product details")
        ProductDocument product,
        @Schema(description = "Available quantity of the product", example = "5")
        Integer quantity
) {}
