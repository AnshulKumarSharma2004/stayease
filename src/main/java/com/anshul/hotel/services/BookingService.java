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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
        booking.setTotalPrice(requestDTO.getTotalPrice());

        // ✅ Fetch checkoutTime from hotel and set in booking
        Hotel hotel = room.getHotel();
        if(hotel.getCheckoutTime()!=null){
            booking.setCheckOutTime(hotel.getCheckoutTime());
        }else {
            booking.setCheckOutTime("12:00 PM"); // default if not set
        }

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
                        booking.getStatus(),
                        booking.getTotalPrice(),
                        booking.getCustomer().getName(),
                        booking.getCheckOutTime(),
                        booking.getRating()
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
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCustomer().getName(),
                booking.getCheckOutTime(),
                booking.getRating()
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
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCustomer().getName(),
                booking.getCheckOutTime(),
                booking.getRating()
        );
    }
    public List<BookingResponseDTO> getPastBookings(String customerIdHex) {
        ObjectId customerId = new ObjectId(customerIdHex);
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Date today = new Date();
        List<Booking> pastBookings = bookingRepository
                .findByCustomerAndCheckOutBeforeAndStatus(customer, today, "COMPLETED");
        return pastBookings.stream().map(
                this::convertToDTO
        ).toList();
    }
    public List<BookingResponseDTO> getCurrentBookings(String customerIdHex) {
        ObjectId customerId = new ObjectId(customerIdHex);
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now(); // only date
        LocalDateTime startOfDay = today.atStartOfDay();
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
       List<String> currentStatuses=List.of("BOOKED","PENDING_CHECKIN","CHECKED_IN","PENDING_CHECKOUT");
        List<Booking> currentBookings = bookingRepository.findByCustomer_IdAndCheckOutGreaterThanEqualAndStatusIn(customerId, startDate, currentStatuses);

        return currentBookings.stream()
                .map(this::convertToDTO)
                .toList();
    }
    // user request checkIn
    public BookingResponseDTO requestCheckIn(String bookingIdHex, String customerIdHex){
        ObjectId bookingId = new ObjectId(bookingIdHex);
        ObjectId customerId = new ObjectId(customerIdHex);
        // fetch booking for user
        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, customerId);
        if (booking == null)
            throw new RuntimeException("Booking not found for this user");
        Date today = new Date();
        if (today.before(booking.getCheckIn()))
            throw new RuntimeException("Check-in not allowed before check-in date");
        // ensure current status is booked
        if (!"BOOKED".equals(booking.getStatus()))
            throw new RuntimeException("Only booked reservations can request check-in");
        // change status
        booking.setStatus("PENDING_CHECKIN");
        // save in db
        bookingRepository.save(booking);
        return  convertToDTO(booking);
    }
    // Get all pendingCheckIn's of a hotel
    public List<BookingResponseDTO> getPendingCheckInsForHotel(String hotelIdHex){
        ObjectId hotelId = new ObjectId(hotelIdHex);
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<ObjectId> roomIds= rooms.stream().map(Room::getId).toList();
        List<Booking> pendingBookings = bookingRepository.findPendingCheckInsByRoomIds(roomIds);
        return pendingBookings.stream().map(this::convertToDTO).toList();

    }
    // approve checkIn
    public BookingResponseDTO approveCheckIn(String bookingIdHex, String adminEmail) {
        ObjectId bookingId = new ObjectId(bookingIdHex);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        // validate admin ownership
        Hotel hotel = booking.getRoom().getHotel();
        if (!hotel.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("Unauthorized: You cannot approve this booking");
        }
        if (!"PENDING_CHECKIN".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is not pending check-in");
        }
        booking.setStatus("CHECKED_IN");
        bookingRepository.save(booking);
        return convertToDTO(booking);
    }
    //  Reject check-in
    public BookingResponseDTO rejectCheckIn(String bookingIdHex, String adminEmail) {
        ObjectId bookingId = new ObjectId(bookingIdHex);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // validate admin ownership
        Hotel hotel = booking.getRoom().getHotel();
        if (!hotel.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("Unauthorized: You cannot reject this booking");
        }

        if (!"PENDING_CHECKIN".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is not pending check-in");
        }

        // revert to BOOKED
        booking.setStatus("BOOKED");
        bookingRepository.save(booking);
        return convertToDTO(booking);
    }
    //  fetch active bookings of a hotel
    public List<BookingResponseDTO> getActiveBookingsByHotel(String hotelIdHex) {
        ObjectId hotelId = new ObjectId(hotelIdHex);


        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<ObjectId> roomIds = rooms.stream().map(Room::getId).toList();


        List<Booking> activeBookings = bookingRepository.findActiveBookingsByRoomIds(roomIds);


        return activeBookings.stream()
                .map(this::convertToDTO)
                .toList();
    }
    // user request checkout
    public BookingResponseDTO requestCheckOut(String bookingIdHex, String customerIdHex) {
        ObjectId bookingId = new ObjectId(bookingIdHex);
        ObjectId customerId = new ObjectId(customerIdHex);

        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, customerId);
        if (booking == null)
            throw new RuntimeException("Booking not found for this user");

        // ensure current status is CHECKED_IN
        if (!"CHECKED_IN".equals(booking.getStatus()))
            throw new RuntimeException("Checkout can only be requested after check-in");

        // change status
        booking.setStatus("PENDING_CHECKOUT");
        bookingRepository.save(booking);

        return convertToDTO(booking);
    }
    //  Get all pending checkouts of a hotel
    public List<BookingResponseDTO> getPendingCheckOutsForHotel(String hotelIdHex) {
        ObjectId hotelId = new ObjectId(hotelIdHex);
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<ObjectId> roomIds= rooms.stream().map(Room::getId).toList();
        List<Booking> pendingBookings = bookingRepository.findPendingCheckOutsByRoomIds(roomIds);
        return pendingBookings.stream().map(this::convertToDTO).toList();
    }
    //  Approve checkout
    public BookingResponseDTO approveCheckOut(String bookingIdHex, String adminEmail) {
        ObjectId bookingId = new ObjectId(bookingIdHex);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // validate admin ownership
        Hotel hotel = booking.getRoom().getHotel();
        if (!hotel.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("Unauthorized: You cannot approve this booking");
        }
        if (!"PENDING_CHECKOUT".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is not pending check-out");
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);
        return convertToDTO(booking);
    }

    //  Reject checkout
    public BookingResponseDTO rejectCheckOut(String bookingIdHex, String adminEmail) {
        ObjectId bookingId = new ObjectId(bookingIdHex);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // validate admin ownership
        Hotel hotel = booking.getRoom().getHotel();
        if (!hotel.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("Unauthorized: You cannot reject this booking");
        }

        if (!"PENDING_CHECKOUT".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is not pending check-out");
        }

        // revert to CHECKED_IN
        booking.setStatus("CHECKED_IN");
        bookingRepository.save(booking);
        return convertToDTO(booking);
    }
    public long getCurrentGuestsCount(ObjectId hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<ObjectId> roomIds = rooms.stream()
                .map(Room::getId)
                .toList();
        if (roomIds.isEmpty()) return 0;
        List<String> statuses = List.of("CHECKED_IN", "PENDING_CHECKOUT");
        List<Booking> bookings = bookingRepository.findByRoom_IdInAndStatusIn(roomIds,statuses);

        return bookings.stream()
                .mapToLong(Booking::getGuests)
                .sum();
    }
    // rateBooking
    public BookingResponseDTO rateBooking(String bookingIdHex, String customerIdHex, int rating) {
        ObjectId bookingId = new ObjectId(bookingIdHex);
        ObjectId customerId = new ObjectId(customerIdHex);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking not found"));
        // check OwnerShip
        if(!booking.getCustomer().getId().equals(customerId)){
            throw new RuntimeException(" It is Not your booking!");
        }
        // check if booking is completed
        if(!"COMPLETED".equals(booking.getStatus())){
            throw new RuntimeException("You can only rate completed bookings");
        }
        Integer oldRating = booking.getRating();
        if (oldRating != null) {
            throw new RuntimeException("You have already rated this booking");
        }
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        booking.setRating(rating);
        bookingRepository.save(booking);

        // Update Hotel Rating
        Hotel hotel = booking.getRoom().getHotel();
        double oldAvg = hotel.getRating();
        int count = hotel.getRatingCount();
        double newAvg = (oldAvg * count+rating)/(count+1);
        hotel.setRating(newAvg);
        hotel.setRatingCount(count+1);
        hotelRepository.save(hotel);
        return  convertToDTO(booking);
    }
}
