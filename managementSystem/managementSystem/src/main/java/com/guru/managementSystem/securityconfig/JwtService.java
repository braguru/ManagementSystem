package com.guru.managementSystem.securityconfig;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


// This annotation declares the class as a Spring service, indicating that it should be managed by the Spring container.
@Service
public class JwtService {

    // The secret key used for JWT signing and verification. It should be kept secure.
    private static final String SECRET_KEY = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    // Extracts the username from the JWT token.
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Generic method to extract a specific claim from the JWT token using a ClaimsResolver function.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        // Extracts all claims from the token using the helper method.
        final Claims claims = extractAllClaims(token);
        // Applies the specified ClaimsResolver function to extract the desired claim.
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

   // Method for generating a JWT token.
   public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
    return Jwts
        .builder()
        // Set custom claims provided in the map.
        .setClaims(extractClaims)
        // Set the subject of the JWT to the username from UserDetails.
        .setSubject(userDetails.getUsername())
        // Set the issued date of the JWT to the current time.
        .setIssuedAt(new Date(System.currentTimeMillis()))
        // Set the expiration date of the JWT to 24 hours from the current time.
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
        // Sign the JWT with the configured signing key and algorithm.
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        // Compact the JWT into its final, serialized form.
        .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Parses and extracts all claims from the JWT token.
    private Claims extractAllClaims(String token){
        // Uses the Jwts parserBuilder to parse the token, validate the signature, and retrieve the claims.
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey()) // Sets the signing key for verification.
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // Converts the base64-encoded secret key into a Key object for JWT signing and verification.
    private Key getSignInKey() {
        // Decodes the base64-encoded secret key into bytes.
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Generates a Key object using the HMAC SHA algorithm for JWT signing and verification.
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
