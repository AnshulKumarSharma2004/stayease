package com.anshul.hotel.services;

import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.LikedHotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.LikedHotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<Hotel> getLikedHotels(User user) {
        return likedHotelRepository.findByCustomer(user)
                .map(LikedHotel::getHotels)
                .orElse(new ArrayList<>());
    }
    public LikedHotel removeHotel(User user, Hotel hotel) {
        LikedHotel likedHotel = likedHotelRepository.findByCustomer(user)
                .orElseThrow(() -> new RuntimeException("No liked hotels found for this user"));

        likedHotel.getHotels().remove(hotel);
        return likedHotelRepository.save(likedHotel);
    }
}
