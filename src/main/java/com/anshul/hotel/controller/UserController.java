package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.UserProfileDTO;
import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
@GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = auth.getName(); // JWT me Username me email set hai
    //fetch from db
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User Not Found"));


    UserProfileDTO dto = new UserProfileDTO(
            user.getName(),
            user.getEmail(),
            user.getPhone()
    );

    return ResponseEntity.ok(dto);
}
@PutMapping("/profile")
public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO updatedDTO){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String emailFromJWT = auth.getName();
    User user = userRepository.findByEmail(emailFromJWT)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
    user.setName(updatedDTO.getName());
    user.setPhone(updatedDTO.getPhone());

    userRepository.save(user);
    UserProfileDTO dto = new UserProfileDTO(user.getName(),user.getEmail(),user.getPhone());

    return ResponseEntity.ok(dto);
}


}
