package com.ikea.warehouse_query_api.data.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ProductResponse (
    String id,
    String name,
    Integer stock
) {
}
