package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.LikedHotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.HotelRepository;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.LikedHotelService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users/liked-hotels")
public class LikedHotelController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private LikedHotelService likedHotelService;

    // add hotel to liked
    @PostMapping("/add/{hotelId}")
    public LikedHotel addHotel(@PathVariable String hotelId) {
        ObjectId newHotelId= new ObjectId(hotelId);
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // username of logged-in user
      User user = userRepository.findByEmail(email)
              .orElseThrow(()-> new RuntimeException("User not found"));

        // Get hotel
        Hotel hotel = hotelRepository.findById(newHotelId).orElseThrow(()-> new RuntimeException("Hotel Not found"));

        return likedHotelService.addHotelToLiked(user, hotel);
    }
    @DeleteMapping("/remove/{hotelId}")
    public LikedHotel removeHotel(@PathVariable String hotelId) {
        ObjectId newHotelId = new ObjectId(hotelId);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Hotel hotel = hotelRepository.findById(newHotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        return likedHotelService.removeHotel(user, hotel);
    }
    @GetMapping
    public List<HotelResponseDTO> getLikedHotels() {
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return likedHotelService.getLikedHotels(user);
    }
}
