package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.BookingResponseDTO;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.Room;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.HotelRepository;
import com.anshul.hotel.repositories.RoomRepository;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.BookingService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/get-all-bookings/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookingsofHotel(Authentication auth,@PathVariable String hotelId){

        String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin Not Found"));
        ObjectId hotelObjId = new ObjectId(hotelId);
        Hotel hotel = hotelRepository.findById(hotelObjId)
                .orElseThrow(() -> new RuntimeException("Hotel Not Found"));
        if (!hotel.getAdmin().getId().equals(admin.getId())){
             throw new RuntimeException("❌ You are not allowed to access this hotel's bookings");
        }
        List<BookingResponseDTO> bookingByHotel = bookingService.getBookingByHotel(hotelId);
        return new ResponseEntity<>(bookingByHotel,HttpStatus.OK);

    }
    @GetMapping("/booking-of-room/{roomId}")
    public ResponseEntity<List<BookingResponseDTO>> getAllRoomBookings(@PathVariable String roomId,Authentication auth){
    String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin Not Found"));
        ObjectId roomObjId = new ObjectId(roomId);
        Room room = roomRepository.findById(roomObjId)
                .orElseThrow(() -> new RuntimeException("Room Not Found"));

        Hotel hotel = room.getHotel();
        if (!hotel.getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("❌ You are not allowed to access bookings of this room");
        }
        List<BookingResponseDTO> bookingsByRoom = bookingService.getBookingsByRoom(roomId, hotel.getId().toHexString());
        return new ResponseEntity<>(bookingsByRoom,HttpStatus.OK);
    }
}
