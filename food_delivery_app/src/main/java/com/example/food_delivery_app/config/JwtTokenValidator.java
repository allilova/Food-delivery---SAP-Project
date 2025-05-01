package com.example.food_delivery_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);
    
    @Value("${app.jwt.secret:${JwtConstant.SECRET_KEY}}")
    private String jwtSecret;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludedPaths = Arrays.asList(
        "/api/auth/**",
        "/auth/**",
        "/api/test",
        "/api/home",
        "/api/restaurants",
        "/api/restaurants/**",
        "/api/restaurants/search",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    );
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(JwtConstant.JWT_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = authHeader.substring(7);
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String username = String.valueOf(claims.get("email"));
            String authorities = String.valueOf(claims.get("authorities"));
            
            List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw new BadCredentialsException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new BadCredentialsException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            throw new BadCredentialsException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw new BadCredentialsException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw new BadCredentialsException("JWT claims string is empty");
        } catch (Exception e) {
            logger.error("Unable to validate token: {}", e.getMessage());
            throw new BadCredentialsException("Unable to validate token");
        }
        
        filterChain.doFilter(request, response);
    }
}