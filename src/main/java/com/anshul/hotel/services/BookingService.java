package com.anshul.hotel.services;

import com.anshul.hotel.dtos.BookingRequestDTO;
import com.anshul.hotel.dtos.BookingResponseDTO;
import com.anshul.hotel.model.Booking;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.Room;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.BookingRepository;
import com.anshul.hotel.repositories.HotelRepository;
import com.anshul.hotel.repositories.RoomRepository;
import com.anshul.hotel.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;

    public Booking createBooking(String customerIdHex , BookingRequestDTO  requestDTO){
        ObjectId customerId = new ObjectId(customerIdHex);
        ObjectId roomId = new ObjectId(requestDTO.getRoomId());
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room Not Found"));
// checkIn and checkOut check krta hai overlaps ke liye
        List<Booking> conflicts = bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, requestDTO.getCheckIn(), requestDTO.getCheckOut());
        if (!conflicts.isEmpty()){
            throw new RuntimeException("Room not available for selected dates");
        }
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not Found"));
  // create Booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckIn(requestDTO.getCheckIn());
        booking.setCheckOut(requestDTO.getCheckOut());
        booking.setGuests(requestDTO.getGuests());
        booking.setStatus("BOOKED");

        return bookingRepository.save(booking);
    }

    public List<Booking> getCustomerBookings(String customerIdHex){
        ObjectId customerId = new ObjectId(customerIdHex);
    return  bookingRepository.findByCustomerId(customerId);
    }

    public List<BookingResponseDTO> getCustomerBookingDTO(String customerIdHex){
        List<Booking> customerBookings = getCustomerBookings(customerIdHex);
        return customerBookings.stream().map(
                booking-> new BookingResponseDTO(
                        booking.getId().toHexString(),
                        booking.getRoom().getRoomNumber(),
                        booking.getRoom().getHotel().getName(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getGuests(),
                        booking.getStatus()
                )
        ).toList();
    }
    public BookingResponseDTO cancelBooking(String bookingIdHex, String customerIdHex){
        ObjectId bookingId = new ObjectId(bookingIdHex);
        ObjectId customerId = new ObjectId(customerIdHex);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking Not Found"));

        if (!booking.getCustomer().getId().equals(customerId)){
            throw new RuntimeException("Not allowed to cancel this booking");
        }
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        return new BookingResponseDTO(
                booking.getId().toHexString(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getHotel().getName(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getGuests(),
                booking.getStatus()
        );

    }
    // get all bookings of hotel
    public List<BookingResponseDTO> getBookingByHotel(String hotelIdHex){
        ObjectId hotelId = new ObjectId(hotelIdHex);
      // hotel all rooms fetching
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<ObjectId> roomIds = rooms.stream().map(Room::getId).toList();
        // fetch bookings of rooms
        List<Booking> bookings = bookingRepository.findByRoomIds(roomIds);

        // convert to DTO and return
        return bookings.stream().map(this::convertToDTO).toList();

    }
    public List<BookingResponseDTO> getBookingsByRoom(String roomIdHex,String hotelIdHex){
        ObjectId roomId = new ObjectId(roomIdHex);
        ObjectId hotelId = new ObjectId(hotelIdHex);
        List<Booking> bookings = bookingRepository.findByRoom_Id(roomId);
          bookings.forEach(booking -> {
              if (!booking.getRoom().getHotel().getId().equals(hotelId)){
                  throw new RuntimeException("Unauthorized: Room not part of your hotel");
              }
          });
        return bookings.stream().map(this::convertToDTO).toList();
    }
    private BookingResponseDTO convertToDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId().toHexString(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getHotel().getName(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getGuests(),
                booking.getStatus()
        );
    }
}
