package com.anshul.hotel.services;


import com.anshul.hotel.model.User;
import com.anshul.hotel.repositories.UserRepository;
import com.anshul.hotel.utilities.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtility jwtUtility;

    // to register user
    public User saveUser(User user){
       // check email exists or not
        if (userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        // Hash Password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userRepository.save(user);
    }

    // for login
    public String login(String email , String rawPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Invalid Credentials"));

        if (!passwordEncoder.matches(rawPassword,user.getPassword())){

   throw new RuntimeException("Invalid Credentials");
        }
        String role = user.getRole();
        String id = user.getId().toHexString();
        return jwtUtility.generateTokenForUser(user.getEmail(),role,id,user.getName());
    }
}
