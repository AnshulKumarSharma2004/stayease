package com.anshul.hotel.repositories;

import com.anshul.hotel.model.Booking;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, ObjectId> {
    List<Booking> findByRoomIdAndCheckOutAfterAndCheckInBefore(
            ObjectId roomId, Date checkIn, Date checkOut);

    List<Booking> findByCustomerId(ObjectId customerId);


    // All bookings of a specific hotel (through room → hotel → id)
    @Query("{ 'room.$id' : { $in : ?0 } }")
    List<Booking> findByRoomIds(List<ObjectId> roomIds);

    // All bookings of a specific room
    List<Booking> findByRoom_Id(ObjectId roomId);


}
