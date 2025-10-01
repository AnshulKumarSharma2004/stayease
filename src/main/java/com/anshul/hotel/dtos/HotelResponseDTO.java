package com.anshul.hotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponseDTO {
    private String id;
    private String name;
    private String location;
    private String description;
    private String adminUsername;
    private List<String> images;
    private String upiId;
    private String checkoutTime;
    private double rating;
    private int ratingCount;
}

