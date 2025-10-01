package com.anshul.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private ObjectId id;

    private Date checkIn;
    private Date checkOut;
    private int guests;
    private String status;
   @DBRef
    private User customer;
   @DBRef
    private Room room;
   private double totalPrice;
    private String checkOutTime;
    private Integer rating;
}
