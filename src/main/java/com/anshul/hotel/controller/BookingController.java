package com.anshul.hotel.controller;
import com.anshul.hotel.dtos.BookingRequestDTO;
import com.anshul.hotel.dtos.BookingResponseDTO;
import com.anshul.hotel.model.Booking;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
@PostMapping("/create-booking")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO dto, Authentication auth){
        String email = auth.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not Found"));
        Booking booking = bookingService.createBooking(customer.getId().toHexString(), dto);
       BookingResponseDTO responseDTO = new BookingResponseDTO(
               booking.getId().toHexString(),
               booking.getRoom().getRoomNumber(),
               booking.getRoom().getHotel().getName(),
               booking.getCheckIn(),
               booking.getCheckOut(),
               booking.getGuests(),
               booking.getStatus(),
               booking.getTotalPrice(),
               booking.getCustomer().getName(),
               booking.getCheckOutTime(),
               booking.getRating()
       );
       return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

@DeleteMapping("/cancelBooking/{bookingId}")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable String bookingId, Authentication auth){
     String email = auth.getName();
    User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
    BookingResponseDTO responseDTO = bookingService.cancelBooking(bookingId, customer.getId().toHexString());
    return new ResponseEntity<>(responseDTO,HttpStatus.OK);
}

    @GetMapping("/pastBookings")
    public ResponseEntity<List<BookingResponseDTO>> getPastBookings(Authentication auth) {
        String email = auth.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        List<BookingResponseDTO> response = bookingService.getPastBookings(customer.getId().toHexString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/currentBookings")
    public ResponseEntity<List<BookingResponseDTO>> getCurrentBookings(Authentication auth) {
        String email = auth.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        List<BookingResponseDTO> response = bookingService.getCurrentBookings(customer.getId().toHexString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("requestCheckIn/{bookingId}")
    public ResponseEntity<BookingResponseDTO> requestCheckIn(@PathVariable String bookingId, Authentication auth){
        String email = auth.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BookingResponseDTO updatedBooking = bookingService
                .requestCheckIn(bookingId, customer.getId().toHexString());
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    @PostMapping("/requestCheckOut/{bookingId}")
    public ResponseEntity<BookingResponseDTO> requestCheckOut(@PathVariable String bookingId, Authentication auth) {
        String email = auth.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookingResponseDTO updatedBooking = bookingService
                .requestCheckOut(bookingId, customer.getId().toHexString());

        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }
    @PostMapping("rate/{bookingId}")
    public ResponseEntity<BookingResponseDTO> rateBooking( @PathVariable String bookingId,
                                                           @RequestParam int rating,
                                                           Authentication auth){
    String email = auth.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BookingResponseDTO responseDTO = bookingService.rateBooking(
                bookingId,
                customer.getId().toHexString(),
                rating
        );
        return ResponseEntity.ok(responseDTO);

    }

}
