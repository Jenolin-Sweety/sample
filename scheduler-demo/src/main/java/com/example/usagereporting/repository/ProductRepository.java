package com.example.usagereporting.repository;


import com.example.usagereporting.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository interface for Product Entities.
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}