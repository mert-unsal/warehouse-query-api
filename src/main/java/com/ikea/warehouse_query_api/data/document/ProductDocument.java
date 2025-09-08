package com.ikea.warehouse_query_api.data.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikea.warehouse_query_api.data.dto.ArticleAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "products")
@CompoundIndexes({
    @CompoundIndex(name = "idx_products_containArticles_artId", def = "{ 'containArticles.artId': 1 }")
})
@Schema(description = "Product definition with required articles")
public record ProductDocument(
    @Id
    ObjectId id,

    @Schema(description = "Product name", example = "Dining Chair")
    String name,

    @JsonProperty("contain_articles")
    @Schema(description = "List of articles required to build this product")
    @Field("containArticles")
    List<ArticleAmount> containArticles,

    @Version
    @Schema(description = "Optimistic lock version", example = "0")
    Long version,

    @CreatedDate
    Instant createdDate,

    @LastModifiedDate
    Instant lastModifiedDate,

    @CreatedBy
    String createdBy,

    @LastModifiedBy
    String lastModifiedBy
) {}
