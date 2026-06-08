package in.banking.aap.controller;

import in.banking.aap.dto.response.NotificationResponseDto;
import in.banking.aap.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for notification management.
 * 
 * Endpoints:
 * GET /api/notifications/user/{userId} - Get user notifications
 * GET /api/notifications/client/{clientId} - Get client notifications
 * PUT /api/notifications/{id}/read - Mark notification as read
 * GET /api/notifications/user/{userId}/unread-count - Get unread count for user
 * GET /api/notifications/client/{clientId}/unread-count - Get unread count for client
 */
@Slf4j
@Tag(name = "Notifications", description = "Notification APIs")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponseDto>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting notifications for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponseDto> notifications = notificationService
                .getUserNotifications(userId, pageable);
        
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<NotificationResponseDto>> getClientNotifications(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting notifications for client: {}", clientId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponseDto> notifications = notificationService
                .getClientNotifications(clientId, pageable);
        
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String id) {
        
        log.info("Marking notification as read: {}", id);
        
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUserUnreadCount(
            @PathVariable String userId) {
        
        Long count = notificationService.getUserUnreadCount(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/client/{clientId}/unread-count")
    public ResponseEntity<Map<String, Long>> getClientUnreadCount(
            @PathVariable String clientId) {
        
        Long count = notificationService.getClientUnreadCount(clientId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }
}

