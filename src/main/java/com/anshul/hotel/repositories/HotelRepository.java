package com.anshul.hotel.repositories;


import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HotelRepository extends MongoRepository<Hotel, ObjectId> {
    List<Hotel> findByAdmin(User admin);

    List<Hotel> findByNameRegex(String regex);
    List<Hotel> findByLocationRegex(String regex);

}
