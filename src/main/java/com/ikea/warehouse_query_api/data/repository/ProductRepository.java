package com.ikea.warehouse_query_api.data.repository;

import com.ikea.warehouse_query_api.data.document.ProductDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<ProductDocument, ObjectId> {
}
