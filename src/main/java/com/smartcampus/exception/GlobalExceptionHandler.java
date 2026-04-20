package com.smartcampus.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({
            DuplicateRegistrationException.class,
            EventCapacityExceededException.class,
            IllegalArgumentException.class
    })
    public Object handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedOperationException.class)
    public Object handleAccessDenied(AccessDeniedOperationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest request) {
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Something went wrong while processing your request"
                : ex.getMessage();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
    }

    private Object buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", Instant.now().toString());
            body.put("status", status.value());
            body.put("error", status.getReasonPhrase());
            body.put("message", message);
            body.put("path", request.getRequestURI());
            return ResponseEntity.status(status).body(body);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(status);
        modelAndView.setViewName("error");
        modelAndView.addObject("statusCode", status.value());
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }
}
