package com.anshul.hotel.services;

import com.anshul.hotel.dtos.RoomResponseDTO;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.Room;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.HotelRepository;
import com.anshul.hotel.repositories.RoomRepository;
import com.anshul.hotel.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private UserRepository userRepository;


    public Room addRoom(String hotelIdHex, Room room , String adminEmail){
        ObjectId hotelId = new ObjectId(hotelIdHex);
       User admin =  userRepository.findByEmail(adminEmail)
                .orElseThrow(()-> new RuntimeException("Admin Not Found"));

      Hotel hotel =  hotelRepository.findById(hotelId)
               .orElseThrow(()-> new RuntimeException("Hotel Not Found"));
       if (!hotel.getAdmin().getId().equals(admin.getId())){
           throw new RuntimeException("You cannot add room to another hotel");
       }
       room.setHotel(hotel);
       return roomRepository.save(room);
    }
    public List<Room> getRoomsByHotel(String hotelIdHex, String adminEmail){
        ObjectId hotelId = new ObjectId(hotelIdHex);
        User admin =  userRepository.findByEmail(adminEmail)
                .orElseThrow(()-> new RuntimeException("Admin Not Found"));

        Hotel hotel =  hotelRepository.findById(hotelId)
                .orElseThrow(()-> new RuntimeException("Hotel Not Found"));

        if (!hotel.getAdmin().getId().equals(admin.getId())){
            throw new RuntimeException("You cannot view rooms of another hotel");
        }
        return roomRepository.findByHotel(hotel);
    }

    public Room updateRoom(String roomIdHex, Room reqRoom, String adminEmail){
        ObjectId roomId = new ObjectId(roomIdHex);
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room Not Found"));
        Hotel hotel = existingRoom.getHotel();
        if (!hotel.getAdmin().getEmail().equals(adminEmail)){
            throw new RuntimeException("Not authorized to update this room");
        }

        // update only non-null values
        if (reqRoom.getRoomNumber() != null) {
            existingRoom.setRoomNumber(reqRoom.getRoomNumber());
        }
        if (reqRoom.getType() != null) {
            existingRoom.setType(reqRoom.getType());
        }
        if (reqRoom.getPrice() != null) {
            existingRoom.setPrice(reqRoom.getPrice());
        }
        if (reqRoom.getAvailable() != null) {
            existingRoom.setAvailable(reqRoom.getAvailable());
        }


        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(String roomIdHex, String adminEmail) {
        ObjectId roomId = new ObjectId(roomIdHex);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Hotel hotel = room.getHotel();

        if (!hotel.getAdmin().getEmail().equals(adminEmail)) {
            throw new RuntimeException("Not authorized to delete this room");
        }

        roomRepository.delete(room);
    }

    public List<RoomResponseDTO> searchRooms(ObjectId hotelId, String type, Double minPrice, Double maxPrice){
        if (type != null) {
            return roomRepository.findByHotel_IdAndType(hotelId, type)
                    .stream().map(this::convertToRoomResponseDTO).toList();
        }
        if (minPrice != null && maxPrice != null) {
            return roomRepository.findByHotelAndPriceRange(hotelId, minPrice, maxPrice)
                    .stream().map(this::convertToRoomResponseDTO).toList();
        }
        return roomRepository.findByHotel_IdAndAvailableTrue(hotelId)
                .stream().map(this::convertToRoomResponseDTO).toList();
    }

    private RoomResponseDTO convertToRoomResponseDTO(Room room) {
        return new RoomResponseDTO(
                room.getId().toHexString(),        // ObjectId → String
                room.getType(),
                room.getPrice(),
                room.getAvailable(),
                room.getHotel() != null ? room.getHotel().getName() : null // hotel name
        );
    }
}
