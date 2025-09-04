package com.anshul.hotel.repositories;

import com.anshul.hotel.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
       Optional<User> findByEmail(String email);
       boolean existsByEmail(String email);
}
