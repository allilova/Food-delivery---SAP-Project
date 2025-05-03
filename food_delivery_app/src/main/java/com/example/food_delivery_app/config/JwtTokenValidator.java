package com.example.food_delivery_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludedPaths = Arrays.asList(
            "/auth/register",
            "/auth/login",
            "/api/auth/**",
            "/auth/**",
            "/api/test",
            "/api/home",
            "/api/restaurants/**",
            "/api/restaurants/search",
            "/swagger-ui/**", 
            "/v3/api-docs/**"
    );
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtTokenValidator.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(JwtConstant.JWT_HEADER);
        String path = request.getServletPath();
        
        // For debugging purposes
        logger.info("Processing request for path: {}", path);
        logger.info("Auth header present: {}", (authHeader != null));
        
        // If we're in a supplier path, add extra logging
        if (path.startsWith("/api/supplier")) {
            logger.info("Supplier API request - detailed token check");
            logger.info("Auth header: {}", authHeader != null ? authHeader.substring(0, Math.min(15, authHeader.length())) + "..." : "null");
        }
        
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = authHeader.substring(7);
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                String username = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));
                
                // Log the authorities for debugging supplier role issues
                logger.info("JWT claims - username: {}, authorities: {}", username, authorities);
                
                // Clean authorities string if it contains brackets or special characters
                authorities = authorities.replace("[", "").replace("]", "").trim();
                
                // Log all authorities for debugging
                logger.info("Processing authorities: {}", authorities);
                
                // Make sure to handle ROLE_ prefix for Spring Security
                if (authorities.contains("ROLE_RESTAURANT") || authorities.contains("ROLE_DRIVER") || 
                    authorities.contains("ROLE_ADMIN") || authorities.contains("ROLE_CUSTOMER")) {
                    logger.info("Found valid role in authorities: {}", authorities);
                } else if (!authorities.isEmpty()) {
                    // If roles don't have ROLE_ prefix, add it
                    String[] roles = authorities.split(",");
                    StringBuilder roleBuilder = new StringBuilder();
                    for (String role : roles) {
                        String trimmedRole = role.trim();
                        if (!trimmedRole.startsWith("ROLE_")) {
                            roleBuilder.append("ROLE_").append(trimmedRole).append(",");
                        } else {
                            roleBuilder.append(trimmedRole).append(",");
                        }
                    }
                    if (roleBuilder.length() > 0) {
                        roleBuilder.setLength(roleBuilder.length() - 1); // Remove trailing comma
                    }
                    authorities = roleBuilder.toString();
                    logger.info("Modified authorities to ensure ROLE_ prefix: {}", authorities);
                }
                
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                
                // Debug the granted authorities
                if (logger.isDebugEnabled()) {
                    logger.debug("Granted authorities: {}", 
                        auth.stream().map(GrantedAuthority::getAuthority).collect(java.util.stream.Collectors.joining(", ")));
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, auth);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Log successful authentication
                logger.info("Successfully authenticated user: {}", username);
            }
            catch(Exception e) {
                // Log failure details
                logger.error("JWT validation failed: {}", e.getMessage());
                e.printStackTrace();
                
                // Only send error for secured paths
                if (!shouldNotFilter(request)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Invalid JWT token: " + e.getMessage() + "\"}");
                    return;
                }
            }
        } else if (!shouldNotFilter(request)) {
            // Log missing auth header for secured paths
            logger.warn("Missing Authorization header for secured path: {}", path);
            
            // Don't proceed with the filter chain for secured paths without auth
            if (path.startsWith("/api/supplier")) {
                logger.error("Attempt to access supplier API without authorization");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Authentication required for supplier endpoints\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}