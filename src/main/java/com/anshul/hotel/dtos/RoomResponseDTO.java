package com.anshul.hotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseDTO {
    private String id;
    private String roomNumber;
    private String type;
    private Double price;
    private Boolean available;
    private String hotelName;
    private List<String> imageUrls;
}
