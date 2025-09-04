package com.anshul.hotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private String id;
    private String roomNumber;
    private String hotelName;
    private Date checkIn;
    private Date checkOut;
    private int guests;
    private String status;
}
