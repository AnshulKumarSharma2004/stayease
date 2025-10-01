package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.RoomResponseDTO;
import com.anshul.hotel.model.Room;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/admin/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

// Add Room
  @PostMapping(value= "/addRoom/{hotelId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomResponseDTO> addroom(@PathVariable String hotelId,
                                                   @RequestPart("room") Room room,
                                                   @RequestPart(value = "images",required = false)List<MultipartFile>images,
                                                   Authentication auth){
        String  email = auth.getName();
        Room savedRoom = roomService.addRoom(hotelId, room, email,images);
      RoomResponseDTO dto = new RoomResponseDTO(
              savedRoom.getId().toHexString(),
              savedRoom.getRoomNumber(),
              savedRoom.getType(),
              savedRoom.getPrice(),
              savedRoom.getAvailable(),
              savedRoom.getHotel().getName(),
              savedRoom.getImageUrls()
      );
        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }
    @GetMapping("/getRoom/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsOfHotel(@PathVariable String hotelId,Authentication auth){
       String email = auth.getName();
        List<Room> rooms = roomService.getRoomsByHotel(hotelId, email);
        List<RoomResponseDTO> dtoList = rooms.stream()
                .map(room -> new RoomResponseDTO(room.getId().toHexString(), room.getRoomNumber(), room.getType(),room.getPrice(),room.getAvailable(),room.getHotel().getName(),room.getImageUrls())).toList();
        return new ResponseEntity<>(dtoList,HttpStatus.OK);
    }
    @PutMapping(value = "/updateRoom/{roomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomResponseDTO> updateRoom( @PathVariable String roomId,
                                                       @RequestPart("room") Room room,
                                                       @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                       Authentication auth){
     String  email = auth.getName();
        Room updatedRoom = roomService.updateRoom(roomId, room, email,images);
        RoomResponseDTO dto = new RoomResponseDTO(
                updatedRoom.getId().toHexString(),
                updatedRoom.getRoomNumber(),
                updatedRoom.getType(),
                updatedRoom.getPrice(),
                updatedRoom.getAvailable(),
                updatedRoom.getHotel().getName()  ,
                updatedRoom.getImageUrls()
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
