package com.anshul.hotel.HealthCheck;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Health")
public class HealthController {

   @GetMapping("/getHealth")
    public String healthCheck(){
        return "Health is Ok";
    }
}
