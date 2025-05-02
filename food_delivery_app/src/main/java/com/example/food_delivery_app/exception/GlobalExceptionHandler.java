package com.example.food_delivery_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final boolean INCLUDE_STACK_TRACE = false; // Set to true for development, false for production
    
    /**
     * Creates a standard error response body
     */
    private Map<String, Object> createErrorResponse(String message, HttpStatus status, Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("status", status.value());
        
        // Include details for non-500 errors or if stack trace is enabled
        if (status != HttpStatus.INTERNAL_SERVER_ERROR || INCLUDE_STACK_TRACE) {
            body.put("error", ex.getClass().getSimpleName());
            
            if (INCLUDE_STACK_TRACE && ex.getStackTrace().length > 0) {
                body.put("trace", Arrays.stream(ex.getStackTrace())
                    .limit(10)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n")));
            }
        }
        
        return body;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());
        Map<String, Object> body = createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, ex);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.error("Invalid argument: {}", ex.getMessage());
        Map<String, Object> body = createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        
        // Get all validation errors
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField, 
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing + "; " + replacement
            ));
        
        body.put("message", "Validation failed");
        body.put("errors", errors);
        
        logger.error("Validation error: {}", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingRequestHeader(
            MissingRequestHeaderException ex, WebRequest request) {
        String headerName = ex.getHeaderName();
        logger.error("Missing required header: {}", headerName);
        
        Map<String, Object> body = createErrorResponse(
            "Required header '" + headerName + "' is missing", 
            HttpStatus.BAD_REQUEST, 
            ex
        );
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {
        logger.error("Missing required parameter: {}", ex.getParameterName());
        
        Map<String, Object> body = createErrorResponse(
            "Required parameter '" + ex.getParameterName() + "' is missing", 
            HttpStatus.BAD_REQUEST, 
            ex
        );
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            "You don't have permission to access this resource", 
            HttpStatus.FORBIDDEN, 
            ex
        );
        
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        logger.error("Authentication error: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            "Authentication failed: " + ex.getMessage(), 
            HttpStatus.UNAUTHORIZED, 
            ex
        );
        
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        logger.error("Bad credentials: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            "Invalid username or password", 
            HttpStatus.UNAUTHORIZED, 
            ex
        );
        
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        logger.error("Response status exception: {} - {}", ex.getStatusCode(), ex.getReason());
        
        Map<String, Object> body = createErrorResponse(
            ex.getReason() != null ? ex.getReason() : "Request failed", 
            HttpStatus.valueOf(ex.getStatusCode().value()), 
            ex
        );
        
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: ", ex);
        
        // For security, don't expose details of internal server errors
        Map<String, Object> body = createErrorResponse(
            "An unexpected error occurred", 
            HttpStatus.INTERNAL_SERVER_ERROR, 
            ex
        );
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 