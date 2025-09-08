package com.ikea.warehouse_query_api.mapper;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleCommandRequest;
import com.ikea.warehouse_query_api.data.dto.ArticleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ArticleDocument toDocument(ArticleCommandRequest articleDocument);

    @Mapping(target = "id", ignore = true)
    ArticleResponse toResponse(ArticleDocument articleDocument);
}
