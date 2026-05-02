package com.cresensolutions.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Fallback endpoints returned by the circuit breaker when a downstream
 * service is unavailable or the circuit is open.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user-service")
    public ResponseEntity<Map<String, String>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "User Service is currently unavailable.",
                        "message", "The circuit breaker is open. Please try again shortly."
                ));
    }

    @RequestMapping("/leave-service")
    public ResponseEntity<Map<String, String>> leaveServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Leave Service is currently unavailable.",
                        "message", "The circuit breaker is open. Please try again shortly."
                ));
    }

    @RequestMapping("/chatbot")
    public ResponseEntity<Map<String, String>> chatbotFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Chatbot is currently unavailable.",
                        "message", "The AI model is taking too long or the service is down. Please try again in a moment."
                ));
    }
}
