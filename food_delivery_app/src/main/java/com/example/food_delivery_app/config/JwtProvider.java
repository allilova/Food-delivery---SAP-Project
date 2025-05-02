package com.example.food_delivery_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Service
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);
        
        logger.info("Generating token for user: {} with roles: {}", auth.getName(), roles);

        String jwt = Jwts.builder().setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpiration))
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(getKey())
                .compact();

        return jwt;
    }

    public String getEmailFromToken(String jwt) {
        try {
            // Check if the token still has the "Bearer " prefix and remove it
            if (jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
            }
            
            Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(jwt).getBody();
            
            String email = String.valueOf(claims.get("email"));
            String authorities = String.valueOf(claims.get("authorities"));
            
            logger.debug("Extracted email: {} and authorities: {} from token", email, authorities);
            
            return email;
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }
    
    public Claims getAllClaimsFromToken(String jwt) {
        try {
            // Check if the token still has the "Bearer " prefix and remove it
            if (jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
            }
            
            Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(jwt).getBody();
            logger.debug("Successfully parsed JWT token with claims: {}", claims);
            return claims;
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();

        for(GrantedAuthority authority: authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
