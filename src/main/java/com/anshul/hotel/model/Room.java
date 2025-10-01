package com.anshul.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    private ObjectId id;
    private String roomNumber;
    private String type;
    private Double price;
    @Builder.Default
    private Boolean available=true;
    private List<String> imageUrls;

    @DBRef
    private Hotel hotel;   // hotel ref
}
