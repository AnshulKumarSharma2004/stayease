package com.anshul.hotel.services;

import com.anshul.hotel.dtos.BookingResponseDTO;
import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.model.Booking;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.HotelRepository;
import com.anshul.hotel.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ImageUploadService cloudinaryService;
    @Autowired
    private UserRepository userRepository;


    public Hotel addHotel(Hotel hotel){
      return hotelRepository.save(hotel);
    }
    public List<Hotel> getHotelsByAdmin(User admin){
        return hotelRepository.findByAdmin(admin);
    }
    public  Hotel updateHotel( String hotelIdHex, Hotel hotelReq , String email,List<MultipartFile> images){
        ObjectId hotelId = new ObjectId(hotelIdHex);
        Hotel existingHotel =  hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        if (!existingHotel.getAdmin().getEmail().equals(email)){
            throw new RuntimeException("You are not allowed to update this hotel");
        }
        // only update if non-null in request
        if (hotelReq.getName()!=null){
            existingHotel.setName(hotelReq.getName());
        }
        if (hotelReq.getLocation() != null) {
            existingHotel.setLocation(hotelReq.getLocation());
        }
        if (hotelReq.getDescription() != null) {
            existingHotel.setDescription(hotelReq.getDescription());
        }
        if (hotelReq.getUpiId() != null) {
            existingHotel.setUpiId(hotelReq.getUpiId());
        }
        if (hotelReq.getCheckoutTime() != null) {
            existingHotel.setCheckoutTime(hotelReq.getCheckoutTime());
        }

        if (images != null && !images.isEmpty()) {

            if (existingHotel.getImageUrls() != null && !existingHotel.getImageUrls().isEmpty()) {
                existingHotel.getImageUrls().forEach(url -> {
                    cloudinaryService.deleteImage(url);
                });
            }
            String adminId = existingHotel.getAdmin().getId().toHexString();

            List<String> newImageUrls = images.stream()
                    .map(file -> cloudinaryService.uploadImage(file, "hotels/" + adminId))
                    .toList();

            existingHotel.setImageUrls(newImageUrls);
        }
        return hotelRepository.save(existingHotel);
    }

    // Delete hotel
    public void deleteHotel(String hotelIdHex, String email) {
        ObjectId hotelId = new ObjectId(hotelIdHex);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // ownership check
        if (!hotel.getAdmin().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this hotel");
        }
if (hotel.getImageUrls()!=null && !hotel.getImageUrls().isEmpty()){
    hotel.getImageUrls().forEach(url->{
cloudinaryService.deleteImage(url);
    });
}
        hotelRepository.delete(hotel);
    }
    // search Hotels
    public List<HotelResponseDTO> searchHotels(String name , String location){
        if (name != null && !name.isEmpty()) {
            if (location != null && !location.isEmpty()) {
                // both present
                return hotelRepository.findByNameRegexAndLocationRegex(
                        "(?i).*" + name + ".*",
                        "(?i).*" + location + ".*"
                ).stream().map(this::convertToHotelResponseDTO).toList();
            } else {
                // only name
                return hotelRepository.findByNameRegex("(?i).*" + name + ".*")
                        .stream().map(this::convertToHotelResponseDTO).toList();
            }
        } else if (location != null && !location.isEmpty()) {
            // only location
            return hotelRepository.findByLocationRegex("(?i).*" + location + ".*")
                    .stream().map(this::convertToHotelResponseDTO).toList();
        } else {
            // both null/empty
            return hotelRepository.findAll()
                    .stream().map(this::convertToHotelResponseDTO).toList();
        }

    }
    private HotelResponseDTO convertToHotelResponseDTO(Hotel hotel) {
        return new HotelResponseDTO(
                hotel.getId().toHexString(),       // ObjectId → String
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getAdmin() != null ? hotel.getAdmin().getName() : null, // admin username
                hotel.getImageUrls(),
                hotel.getUpiId(),
                hotel.getCheckoutTime(),
                hotel.getRating(),
                hotel.getRatingCount()
        );
    }
    public HotelResponseDTO verifyUserHotel(String  tokenEmail, String reqEmail){
        if (!tokenEmail.equals(reqEmail)) {
            throw new RuntimeException("Email mismatch. Verification failed.");
        }
        User user = userRepository.findByEmail(tokenEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Hotel> hotels = hotelRepository.findByAdmin(user);
        if (hotels.isEmpty()) {
            throw new RuntimeException("No hotel found for this user");
        }
        Hotel hotel = hotels.get(0);
        return convertToHotelResponseDTO(hotel);




    }
    public List<HotelResponseDTO> searchHotelsByCity(String city){
        if (city == null || city.isEmpty()) {
            return hotelRepository.findAll()
                    .stream()
                    .sorted((h1,h2)-> h2.getId().getTimestamp()-h1.getId().getTimestamp())
                    .limit(5)
                    .map(this::convertToHotelResponseDTO)
                    .toList();
        }
        List<HotelResponseDTO> hotelsByCity = hotelRepository.findByLocationRegex("(?i).*" + city + ".*")
                .stream()
                .map(this::convertToHotelResponseDTO).toList();
        if (hotelsByCity.isEmpty()) {
            return hotelRepository.findAll()
                    .stream()
                    .sorted((h1, h2) -> h2.getId().getTimestamp() - h1.getId().getTimestamp()) // newest first
                    .limit(5)
                    .map(this::convertToHotelResponseDTO)
                    .toList();
        }
        return hotelsByCity;
    }
    public List<HotelResponseDTO> getHotelsSortedByRatingDesc() {
        List<HotelResponseDTO> hotels = hotelRepository.findAll()
                .stream()
                .map(this::convertToHotelResponseDTO)
                .sorted((h1, h2) -> Double.compare(h2.getRating(), h1.getRating())) // Descending
                .toList();

        return hotels;
    }


}
