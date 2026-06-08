package in.banking.aap.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring application status.
 * Provides basic health endpoint to verify application startup.
 */
@RestController
@Tag(name = "Health", description = "Application Health APIs")
@RequestMapping("/health")
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("application", "Chat System");
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Chat System API");
        info.put("description", "Real-time chat system for user-client communication");
        info.put("features", new String[]{
            "Socket.IO real-time messaging",
            "File uploads",
            "Connection management",
            "Notifications"
        });
        return ResponseEntity.ok(info);
    }
}