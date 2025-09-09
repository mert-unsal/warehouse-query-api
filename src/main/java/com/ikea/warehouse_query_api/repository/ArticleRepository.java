package com.ikea.warehouse_query_api.repository;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<ArticleDocument, String> {

    @Query("{ '_id': { $in: ?0 } }")
    List<ArticleDocument> findByArticleIdList(List<String> articleIdList);

}
