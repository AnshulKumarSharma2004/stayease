package com.anshul.hotel.repositories;


import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends MongoRepository<Hotel, ObjectId> {
    List<Hotel> findByAdmin(User admin);

    List<Hotel> findByNameRegex(String regex);
    List<Hotel> findByLocationRegex(String regex);
    List<Hotel> findByNameRegexAndLocationRegex(String nameRegex, String locationRegex);


}
