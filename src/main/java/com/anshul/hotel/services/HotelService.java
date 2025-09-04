package com.anshul.hotel.services;

import com.anshul.hotel.dtos.BookingResponseDTO;
import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.model.Booking;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.HotelRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;


    public Hotel addHotel(Hotel hotel){
      return hotelRepository.save(hotel);
    }
    public List<Hotel> getHotelsByAdmin(User admin){
        return hotelRepository.findByAdmin(admin);
    }
    public  Hotel updateHotel( String hotelIdHex, Hotel hotelReq , String email){
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

        hotelRepository.delete(hotel);
    }
    // search Hotels
    public List<HotelResponseDTO> searchHotels(String name , String location){
     if (name!=null){
          return hotelRepository.findByNameRegex("(?i).*"+ name + ".*" )
                 .stream().map(this::convertToHotelResponseDTO).toList();
     }
        if (location != null) {
            return hotelRepository.findByLocationRegex("(?i).*" + location + ".*")
                    .stream().map(this::convertToHotelResponseDTO).toList();
        }
        return List.of();
    }
    private HotelResponseDTO convertToHotelResponseDTO(Hotel hotel) {
        return new HotelResponseDTO(
                hotel.getId().toHexString(),       // ObjectId → String
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getAdmin() != null ? hotel.getAdmin().getName() : null // admin username
        );
    }
}
