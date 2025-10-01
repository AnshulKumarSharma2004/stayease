package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.HotelResponseDTO;
import com.anshul.hotel.dtos.VerifyReqDTO;
import com.anshul.hotel.model.Hotel;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.services.HotelService;
import com.anshul.hotel.services.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/hotels")
public class HotelAdminController {
@Autowired
    private HotelService hotelService;
@Autowired
    private UserRepository userRepository;
@Autowired
private ImageUploadService cloudinaryService;
// add hotel
@PostMapping(value = "/addHotel" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<HotelResponseDTO> addHotel(@RequestPart("hotel") Hotel hotel,
                                                 @RequestPart(value = "images", required = false) List<MultipartFile> images){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = auth.getName();
    User admin = userRepository.findByEmail(userEmail)
            .orElseThrow(()-> new RuntimeException("Admin Not Found"));
    hotel.setAdmin(admin);
    if (images!=null && !images.isEmpty()){
        List<String> imageUrls = images.stream()
                .map(file->cloudinaryService.uploadImage(file,"hotels/"+admin.getId().toHexString()))
                .toList();
        hotel.setImageUrls(imageUrls);
    }
    Hotel savedhotel = hotelService.addHotel(hotel);
    HotelResponseDTO dto = new HotelResponseDTO(
            savedhotel.getId().toHexString(),
            savedhotel.getName(),
            savedhotel.getLocation(),
            savedhotel.getDescription(),
            savedhotel.getAdmin().getName(),
            savedhotel.getImageUrls(),
            savedhotel.getUpiId(),
            savedhotel.getCheckoutTime(),
            savedhotel.getRating(),
            savedhotel.getRatingCount()
    );
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
}
@GetMapping("/get-My-Hotels")
public ResponseEntity<List<HotelResponseDTO>> getMyHotels(Authentication auth){
  String email = auth.getName();
    User admin = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
    List<Hotel> hotels = hotelService.getHotelsByAdmin(admin);
    List<HotelResponseDTO> hotelDTOs = hotels.stream().map(hotel -> new HotelResponseDTO(
            hotel.getId().toHexString(),
            hotel.getName(),
            hotel.getLocation(),
            hotel.getDescription(),
            hotel.getAdmin().getName(),
            hotel.getImageUrls(),
            hotel.getUpiId(),
            hotel.getCheckoutTime(),
            hotel.getRating(),
            hotel.getRatingCount()
    )).toList();
    return new ResponseEntity<>(hotelDTOs, HttpStatus.OK);
}

// update hotel by its owner
    @PutMapping(value = "/updateHotel/{hotelId}",consumes = {"multipart/form-data"})
    public ResponseEntity<HotelResponseDTO> updateHotel(@PathVariable String hotelId,  @RequestPart("hotel") Hotel hotelReq,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                        Authentication auth){
    // taking email from jwt
        String email = auth.getName();

        // Hotel update call
        Hotel updatedHotel = hotelService.updateHotel(hotelId, hotelReq, email, images);

        // Response DTO return karo
        HotelResponseDTO dto = new HotelResponseDTO(
                updatedHotel.getId().toHexString(),
                updatedHotel.getName(),
                updatedHotel.getLocation(),
                updatedHotel.getDescription(),
                updatedHotel.getAdmin().getName(),
                updatedHotel.getImageUrls(),
                updatedHotel.getUpiId(),
                updatedHotel.getCheckoutTime(),
                updatedHotel.getRating(),
                updatedHotel.getRatingCount()
        );
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @DeleteMapping("/deleteHotel/{hotelId}")
    public ResponseEntity<String> deleteHotel(@PathVariable String hotelId, Authentication authentication){
    String email = authentication.getName();
    hotelService.deleteHotel(hotelId,email);
    return new ResponseEntity<>("Hotel Deleted Successfully",HttpStatus.OK);
    }

    @PostMapping("/verifyHotel")
    public  ResponseEntity<?> verifyHotelUser(@RequestBody VerifyReqDTO dto,Authentication auth) {
        try {
            String tokenEmail = auth.getName();
            // Service call
            HotelResponseDTO hotelDTO = hotelService.verifyUserHotel(tokenEmail, dto.getEmail());
            return ResponseEntity.ok(hotelDTO);
        }catch (Exception e){
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }


}
