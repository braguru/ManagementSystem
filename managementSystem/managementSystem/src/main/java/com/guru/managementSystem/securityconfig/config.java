package com.guru.managementSystem.securityconfig;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


// This annotation marks the class as a Spring component, allowing it to be automatically discovered and registered in the Spring context.
@Component
// This annotation, from the Lombok library, generates a constructor with required fields, reducing boilerplate code.
@RequiredArgsConstructor
public class config extends OncePerRequestFilter {

    // The JwtService dependency is injected via constructor injection.
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // This method is called for each incoming HTTP request.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the "Authorization" header from the HTTP request.
        final String authHeader = request.getHeader("Authorization");
        
        // Variables to store the JWT token and UserEmail extracted from the token.
        final String jwt;
        final String userEmail;

        // Check if the "Authorization" header is absent or doesn't start with "Bearer ".
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If conditions are not met, continue with the next filter in the chain.
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token from the "Authorization" header by removing the "Bearer " prefix.
        jwt = authHeader.substring(7); // The part after "Bearer " starts from the 7th index of "Authorization" header.

        // Use the JwtService to extract the UserEmail from the JWT token.
        userEmail = jwtService.extractUsername(jwt);

        // At this point, you have the UserEmail extracted from the JWT token and can perform further actions.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)){
                // Create a UsernamePasswordAuthenticationToken with the UserEmail and the UserDetails.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, 
                    userDetails.getAuthorities()
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
        
    }
}
