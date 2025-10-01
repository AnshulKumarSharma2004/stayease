package com.anshul.hotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private String roomId;
    private Date checkIn;
    private Date checkOut;
        private int guests;
        private double totalPrice;
}
