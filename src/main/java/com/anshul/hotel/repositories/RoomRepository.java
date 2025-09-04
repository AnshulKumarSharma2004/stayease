package com.anshul.hotel.repositories;

import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.Room;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, ObjectId> {
    List<Room> findByHotel(Hotel hotel);
@Query("{'hotel.$id': ?0}")
    List<Room> findByHotelId(ObjectId hotel);

    List<Room> findByHotel_IdAndType(ObjectId hotelId, String type);
    List<Room> findByHotel_IdAndAvailableTrue(ObjectId hotelId);

    @Query("{ 'hotel.$id' : ?0, 'price' : { $gte: ?1, $lte: ?2 } }")
    List<Room> findByHotelAndPriceRange(ObjectId hotelId, double minPrice, double maxPrice);

}
