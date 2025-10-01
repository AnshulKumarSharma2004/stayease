package com.anshul.hotel.repositories;

import com.anshul.hotel.model.LikedHotel;
import com.anshul.hotel.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikedHotelRepository extends MongoRepository<LikedHotel, ObjectId> {
    Optional<LikedHotel> findByCustomer(User customer);

}
