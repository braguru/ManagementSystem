package com.guru.managementSystem.Controller;

import com.guru.managementSystem.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class VerificationEmailController {


    private final AuthenticationService authenticationService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {

        // Check if the token exists in the database
        try{
            String userEmail = authenticationService.confirmToken(token);
            return ResponseEntity.ok("Email address confirmed " + userEmail);
        } catch (IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
