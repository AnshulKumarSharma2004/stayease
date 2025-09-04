package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.RoomResponseDTO;
import com.anshul.hotel.model.Room;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

// Add Room
  @PostMapping("/addRoom/{hotelId}")
    public ResponseEntity<RoomResponseDTO> addroom(@PathVariable String hotelId, @RequestBody Room room, Authentication auth){
        String  email = auth.getName();
        Room savedRoom = roomService.addRoom(hotelId, room, email);
      RoomResponseDTO dto = new RoomResponseDTO(
              savedRoom.getId().toHexString(),
              savedRoom.getType(),
              savedRoom.getPrice(),
              savedRoom.getAvailable(),
              savedRoom.getHotel().getName()
      );
        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }
    @GetMapping("/getRoom/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsOfHotel(@PathVariable String hotelId,Authentication auth){
       String email = auth.getName();
        List<Room> rooms = roomService.getRoomsByHotel(hotelId, email);
        List<RoomResponseDTO> dtoList = rooms.stream()
                .map(room -> new RoomResponseDTO(room.getId().toHexString(),room.getType(),room.getPrice(),room.getAvailable(),room.getHotel().getName())).toList();
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @PutMapping("/updateRoom/{roomId}")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable String roomId , @RequestBody Room room, Authentication auth){
     String  email = auth.getName();
        Room updatedRoom = roomService.updateRoom(roomId, room, email);
        RoomResponseDTO dto = new RoomResponseDTO(
                updatedRoom.getId().toHexString(),
                updatedRoom.getType(),
                updatedRoom.getPrice(),
                updatedRoom.getAvailable(),
                updatedRoom.getHotel().getName()   // sirf hotel ka naam
        );
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
  @DeleteMapping("/deleteRoom/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable String roomId , Authentication auth){
      String email = auth.getName();
      roomService.deleteRoom(roomId,email);
        return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
    }
}
