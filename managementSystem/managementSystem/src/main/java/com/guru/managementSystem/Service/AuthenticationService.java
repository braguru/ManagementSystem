package com.guru.managementSystem.Service;

import com.guru.managementSystem.Entity.VerificationToken;
import com.guru.managementSystem.Repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guru.managementSystem.Controller.AuthenticationRequest;
import com.guru.managementSystem.Controller.AuthenticationResponse;
import com.guru.managementSystem.Controller.RegisterRequest;
import com.guru.managementSystem.Entity.Role;
import com.guru.managementSystem.Entity.User;
import com.guru.managementSystem.Repository.UserRepository;
import com.guru.managementSystem.securityconfig.JwtService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository tokenRepository;
    @Autowired
    private EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) {

        User user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        repository.save(user);
        String jwtToken = jwtService.generateToken(user);

        // Generate a verification token and send email
        String token = UUID.randomUUID().toString();
        String verification_token = createVerificationToken(user, token);
        emailService.sendVerificationEmail(user.getEmail(), token);
        
        return AuthenticationResponse.builder()
                .token(verification_token)
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println("AuthenticationService bean created");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail(), 
            request.getPassword()));
        User user = repository.findByEmail(request.getEmail())
            .orElseThrow();
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }


    public String confirmToken(String token){

        VerificationToken tokenRepository1 = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));
        if (tokenRepository1.getConfirmedAt() != null){
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiredAt = tokenRepository1.getExpiryDate();
        if (expiredAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("token expired");
        }

        tokenRepository1.setConfirmedAt(LocalDateTime.now());
        User user = tokenRepository1.getUser();
        user.setEnabled(true);
        repository.save(user);
        return user.getEmail();
    }

    private String createVerificationToken(User user, String token) {

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1)); // Set expiration time (adjust as needed)
        tokenRepository.save(verificationToken);
        return token;
    }

}
