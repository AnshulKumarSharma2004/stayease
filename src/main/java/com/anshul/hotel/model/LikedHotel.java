package com.anshul.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "liked_hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedHotel {
    @Id
    private ObjectId id;
   @DBRef
    private User customer;     // Reference to User
  @DBRef
    private List<Hotel> hotels;  // Reference to Hotel
    private LocalDateTime createdAt;
}
