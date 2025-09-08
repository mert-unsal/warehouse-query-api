package com.ikea.warehouse_query_api.mapper;

import com.ikea.warehouse_query_api.data.document.ProductDocument;
import com.ikea.warehouse_query_api.data.dto.ProductCommandRequest;
import com.ikea.warehouse_query_api.data.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    ProductDocument toDocument(ProductCommandRequest productCommandRequest);

    ProductResponse toResponse(ProductDocument productDocument);
}
