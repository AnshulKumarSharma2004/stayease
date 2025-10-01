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
import org.springframework.web.bind.annotation.*;

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
    //  Get pending check-ins of a hotel
    @GetMapping("/pending-checkins/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getPendingCheckIns(@PathVariable String hotelId,Authentication auth){
        String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        Hotel hotel = hotelRepository.findById(new ObjectId(hotelId))
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        if (!hotel.getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("Unauthorized: This hotel does not belong to you");
        }
        List<BookingResponseDTO> pendingBookings = bookingService.getPendingCheckInsForHotel(hotelId);
        return new ResponseEntity<>(pendingBookings, HttpStatus.OK);
    }
    @PostMapping("/approve-checkin/{bookingId}")
    public ResponseEntity<BookingResponseDTO> approveCheckIn(
            @PathVariable String bookingId,
            Authentication auth) {

        String email = auth.getName();
        BookingResponseDTO booking = bookingService.approveCheckIn(bookingId, email);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }
    //  Reject check-in
    @PostMapping("/reject-checkin/{bookingId}")
    public ResponseEntity<BookingResponseDTO> rejectCheckIn(
            @PathVariable String bookingId,
            Authentication auth) {

        String email = auth.getName();
        BookingResponseDTO booking = bookingService.rejectCheckIn(bookingId, email);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/active-bookings/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getActiveBookings(@PathVariable String hotelId, Authentication auth) {

        String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin Not Found"));


        Hotel hotel = hotelRepository.findById(new ObjectId(hotelId))
                .orElseThrow(() -> new RuntimeException("Hotel Not Found"));

        if (!hotel.getAdmin().getId().equals(admin.getId()))
            throw new RuntimeException("❌ Not allowed to access this hotel's bookings");


        List<BookingResponseDTO> activeBookings = bookingService.getActiveBookingsByHotel(hotelId);


        return new ResponseEntity<>(activeBookings, HttpStatus.OK);
    }
    //  Get pending check-outs of a hotel
    @GetMapping("/pending-checkouts/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getPendingCheckOuts(
            @PathVariable String hotelId,
            Authentication auth) {
        String email = auth.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        Hotel hotel = hotelRepository.findById(new ObjectId(hotelId))
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        if (!hotel.getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("Unauthorized: This hotel does not belong to you");
        }
        List<BookingResponseDTO> pendingBookings = bookingService.getPendingCheckOutsForHotel(hotelId);
        return new ResponseEntity<>(pendingBookings, HttpStatus.OK);
    }

    //  Approve check-out
    @PostMapping("/approve-checkout/{bookingId}")
    public ResponseEntity<BookingResponseDTO> approveCheckOut(
            @PathVariable String bookingId,
            Authentication auth) {

        String email = auth.getName();
        BookingResponseDTO booking = bookingService.approveCheckOut(bookingId, email);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    //  Reject check-out
    @PostMapping("/reject-checkout/{bookingId}")
    public ResponseEntity<BookingResponseDTO> rejectCheckOut(
            @PathVariable String bookingId,
            Authentication auth) {

        String email = auth.getName();
        BookingResponseDTO booking = bookingService.rejectCheckOut(bookingId, email);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }
    @GetMapping("/current-guests/{hotelId}")
    public ResponseEntity<Long> getCurrentGuests(@PathVariable String hotelId) {
        ObjectId hotelObjectId = new ObjectId(hotelId);
        long count = bookingService.getCurrentGuestsCount(hotelObjectId);
        return ResponseEntity.ok(count);
    }

}
