package com.anshul.hotel.services;

import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.LikedHotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.LikedHotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikedHotelService {

    @Autowired
    private LikedHotelRepository likedHotelRepository;
    public LikedHotel addHotelToLiked(User user, Hotel hotel){
        LikedHotel likedHotel = likedHotelRepository.findByCustomer(user)
                .orElse(LikedHotel.builder()
                        .customer(user)
                        .hotels(new ArrayList<>())
                        .createdAt(LocalDateTime.now())
                        .build());
        if (!likedHotel.getHotels().contains(hotel)){
    likedHotel.getHotels().add(hotel);
        }
        return likedHotelRepository.save(likedHotel);
    }

    public List<HotelResponseDTO> getLikedHotels(User user) {
        return likedHotelRepository.findByCustomer(user)
                .map(LikedHotel::getHotels)
                .orElse(new ArrayList<>())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public LikedHotel removeHotel(User user, Hotel hotel) {
        LikedHotel likedHotel = likedHotelRepository.findByCustomer(user)
                .orElseThrow(() -> new RuntimeException("No liked hotels found for this user"));

        likedHotel.getHotels().remove(hotel);
        return likedHotelRepository.save(likedHotel);
    }
    private HotelResponseDTO convertToDTO(Hotel hotel) {
        return new HotelResponseDTO(
                hotel.getId() != null ? hotel.getId().toHexString() : null,
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getAdmin() != null ? hotel.getAdmin().getName() : null,
                hotel.getImageUrls() != null
                        ? hotel.getImageUrls().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                        : new ArrayList<>(),
                hotel.getUpiId(),
                hotel.getCheckoutTime(),
                hotel.getRating(),
                hotel.getRatingCount()
        );
    }
}
