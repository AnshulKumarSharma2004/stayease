package com.anshul.hotel.controller;

import com.anshul.hotel.dtos.LoginRequest;
import com.anshul.hotel.dtos.LoginResponse;
import com.anshul.hotel.model.User;
import com.anshul.hotel.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    private UserService userService;
 @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user){
     try {
         User savedUser = userService.saveUser(user);

             // Hide password before sending response
             savedUser.setPassword(null);
             return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

     }catch (RuntimeException e) {
         return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
     }catch (Exception e){
         return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
     }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request){

try {
    String token = userService.login(request.getEmail(), request.getPassword());
    return  ResponseEntity.ok(new LoginResponse(token));
}catch (RuntimeException e){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
}catch (Exception e){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
}
    }
}
