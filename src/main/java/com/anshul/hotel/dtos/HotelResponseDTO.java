package com.anshul.hotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponseDTO {
    private String id;
    private String name;
    private String location;
    private String description;
    private String adminUsername;
}

