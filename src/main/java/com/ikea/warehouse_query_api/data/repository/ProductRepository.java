package com.ikea.warehouse_query_api.data.repository;

import com.ikea.warehouse_query_api.data.document.ProductDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductDocument, ObjectId> {

    @Query("{}, skip: ?0, limit: ?1 }")
    public List<ProductDocument> findAll(Integer skip, Integer limit);

}
