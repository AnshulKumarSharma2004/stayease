package com.anshul.hotel.repositories;

import com.anshul.hotel.model.Booking;
import com.anshul.hotel.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
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

//  Past bookings = user booking and checkout date less than today and status completed
List<Booking> findByCustomerAndCheckOutBeforeAndStatus(User user, Date date, String status);

    // Current bookings =  user booking and checkout date greater than today and status Booked
    List<Booking> findByCustomer_IdAndCheckOutGreaterThanEqualAndStatusIn(ObjectId customerId, Date date, List<String> statusList);

    Booking findByIdAndCustomerId(ObjectId bookingId, ObjectId customerId);

    // Pending check-ins by roomIds
    @Query("{ 'room.$id' : { $in : ?0 }, 'status' : 'PENDING_CHECKIN' }")
    List<Booking> findPendingCheckInsByRoomIds(List<ObjectId> roomIds);

    @Query("{ 'room.$id' : { $in : ?0 }, 'status' : 'CHECKED_IN' }")
    List<Booking> findActiveBookingsByRoomIds(List<ObjectId> roomIds);

    List<Booking> findByStatus(String status);

    // Pending check-outs by roomIds
    @Query("{ 'room.$id' : { $in : ?0 }, 'status' : 'PENDING_CHECKOUT' }")
    List<Booking> findPendingCheckOutsByRoomIds(List<ObjectId> roomIds);

    List<Booking> findByRoom_IdInAndStatusIn(List<ObjectId> roomIds,List<String> statuses);

}
