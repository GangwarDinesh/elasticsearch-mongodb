package com.elastic.mongo.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.elastic.mongo.app.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

}
