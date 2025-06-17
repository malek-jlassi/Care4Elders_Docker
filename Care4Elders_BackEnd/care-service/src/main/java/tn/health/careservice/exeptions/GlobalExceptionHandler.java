package tn.health.careservice.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException e) {
        log.error("Resource not found: {}", e.getMessage());
        Map<String, Object> body = createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Resource not found",
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("Invalid argument: {}", e.getMessage());
        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid argument",
            e.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException e) {
        log.error("External service error: {}", e.getMessage());
        Map<String, Object> body = createErrorResponse(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "External service error",
            "Error communicating with external service: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        Map<String, Object> body = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error",
            "An unexpected error occurred. Please try again later."
        );
        return ResponseEntity.internalServerError().body(body);
    }

    private Map<String, Object> createErrorResponse(int status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
