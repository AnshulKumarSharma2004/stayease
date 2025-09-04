package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/hotels")
public class HotelAdminController {
@Autowired
    private HotelService hotelService;
@Autowired
    private UserRepository userRepository;
// add hotel
@PostMapping("/addHotel")
public ResponseEntity<HotelResponseDTO> addHotel(@RequestBody Hotel hotel){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = auth.getName();
    User admin = userRepository.findByEmail(userEmail)
            .orElseThrow(()-> new RuntimeException("Admin Not Found"));
    hotel.setAdmin(admin);
    Hotel savedhotel = hotelService.addHotel(hotel);
    HotelResponseDTO dto = new HotelResponseDTO(
            savedhotel.getId().toHexString(),
            savedhotel.getName(),
            savedhotel.getLocation(),
            savedhotel.getDescription(),
            savedhotel.getAdmin().getName()
    );
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
}
@GetMapping("/get-My-Hotels")
public ResponseEntity<List<HotelResponseDTO>> getMyHotels(Authentication auth){
  String email = auth.getName();
    User admin = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
    List<Hotel> hotels = hotelService.getHotelsByAdmin(admin);
    List<HotelResponseDTO> hotelDTOs = hotels.stream().map(hotel -> new HotelResponseDTO(
            hotel.getId().toHexString(),
            hotel.getName(),
            hotel.getLocation(),
            hotel.getDescription(),
            hotel.getAdmin().getName()
    )).toList();
    return new ResponseEntity<>(hotelDTOs, HttpStatus.OK);
}

// update hotel by its owner
    @PutMapping("/updateHotel/{hotelId}")
    public ResponseEntity<HotelResponseDTO> updateHotel(@PathVariable String hotelId, @RequestBody Hotel hotelReq,Authentication auth){
    // taking email from jwt
      String email = auth.getName();
        Hotel updateHotel = hotelService.updateHotel(hotelId, hotelReq, email);
        HotelResponseDTO dto = new HotelResponseDTO(
                updateHotel.getId().toHexString(),
                updateHotel.getName(),
                updateHotel.getLocation(),
                updateHotel.getDescription(),
                updateHotel.getAdmin().getName()
        );
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
    @DeleteMapping("/deleteHotel/{hotelId}")
    public ResponseEntity<String> deleteHotel(@PathVariable String hotelId, Authentication authentication){
    String email = authentication.getName();
    hotelService.deleteHotel(hotelId,email);
    return new ResponseEntity<>("Hotel Deleted Successfully",HttpStatus.OK);
    }

}
