package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.dtos.RoomResponseDTO;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.HotelService;
import com.anshul.hotel.services.RoomService;
import org.bson.types.ObjectId;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/search")
public class SearchController {
    @Autowired
    private HotelService hotelService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserRepository userRepository;

    //  Search hotels by name or location
     @GetMapping("/hotels")
    public ResponseEntity<List<HotelResponseDTO>> searchHotels(@RequestParam(required = false) String name , @RequestParam(required = false)String location){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<HotelResponseDTO> hotelResponseDTOS = hotelService.searchHotels(name, location);
        if (hotelResponseDTOS!=null){
            return new ResponseEntity<>(hotelResponseDTOS, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
@GetMapping("/rooms")
    public ResponseEntity<List<RoomResponseDTO>> searchRooms(@RequestParam  ObjectId hotelId,@RequestParam(required = false)String type, @RequestParam(required = false)double minPrice,@RequestParam(required = false) double maxPrice){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RoomResponseDTO> rooms = roomService.searchRooms(hotelId, type, minPrice, maxPrice);
        if (rooms != null && !rooms.isEmpty()) {
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
