package com.example.usagereporting.repository;

import com.example.usagereporting.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository interface for User Entities.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

}