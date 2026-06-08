package in.banking.aap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.banking.aap.domain.entity.ChatRoom;
import in.banking.aap.domain.entity.Connection;
import in.banking.aap.domain.entity.Message;
import in.banking.aap.domain.entity.Notification;
import in.banking.aap.domain.enums.NotificationType;
import in.banking.aap.dto.response.NotificationResponseDto;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.repository.ChatRoomRepository;
import in.banking.aap.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing push notifications.
 * Handles notification creation and delivery for various events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final ChatRoomRepository chatRoomRepository;
    
    /**
     * Send notification for new connection request
     */
    @Transactional
    public void notifyConnectionRequest(Connection connection) {
        Map<String, String> data = new HashMap<>();
        data.put("connectionId", connection.getId());
        data.put("propertyId", connection.getPropertyId());
        
        Notification notification = Notification.builder()
                .clientId(connection.getClientId())
                .type(NotificationType.CONNECTION_REQUEST)
                .title("New Connection Request")
                .message("A user wants to connect with your property")
                .data(data)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        
        log.info("Connection request notification sent to client: {}", connection.getClientId());
    }
    
    /**
     * Send notification for approved connection
     */
    @Transactional
    public void notifyConnectionApproved(Connection connection) {
        Map<String, String> data = new HashMap<>();
        data.put("connectionId", connection.getId());
        data.put("propertyId", connection.getPropertyId());
        
        Notification notification = Notification.builder()
                .userId(connection.getUserId())
                .type(NotificationType.CONNECTION_APPROVED)
                .title("Connection Approved")
                .message("Your connection request has been approved! You can now chat.")
                .data(data)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        
        log.info("Connection approved notification sent to user: {}", connection.getUserId());
    }
    
    /**
     * Send notification for rejected connection
     */
    @Transactional
    public void notifyConnectionRejected(Connection connection) {
        Map<String, String> data = new HashMap<>();
        data.put("connectionId", connection.getId());
        data.put("propertyId", connection.getPropertyId());
        
        Notification notification = Notification.builder()
                .userId(connection.getUserId())
                .type(NotificationType.CONNECTION_REJECTED)
                .title("Connection Not Approved")
                .message("Your connection request was not approved")
                .data(data)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        
        log.info("Connection rejected notification sent to user: {}", connection.getUserId());
    }
    
    /**
     * Send notification for new message
     */
    @Transactional
    public void notifyNewMessage(Message message) {
        Map<String, String> data = new HashMap<>();
        data.put("messageId", message.getId());
        data.put("chatId", message.getChatId());
        
        NotificationType type;
        String title;
        String messageText;
        
        // Determine notification type based on message type
        switch (message.getMessageType()) {
            case BOOKING_INQUIRY:
                type = NotificationType.BOOKING_INQUIRY;
                title = "New Booking Inquiry";
                messageText = "You have received a booking inquiry";
                break;
            case COMPLAINT:
                type = NotificationType.COMPLAINT;
                title = "New Complaint";
                messageText = "You have received a complaint";
                break;
            default:
                type = NotificationType.NEW_MESSAGE;
                title = "New Message";
                messageText = message.getContent() != null ? 
                        message.getContent() : "You have a new message";
        }
        
        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(messageText)
                .data(data)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Look up the chat room to find the actual recipient IDs
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatId())
                .orElse(null);

        if (chatRoom != null) {
            switch (message.getSenderType()) {
                case USER:
                    // Sender is the user → recipient is the client
                    notification.setClientId(chatRoom.getClientId());
                    break;
                case CLIENT:
                    // Sender is the client → recipient is the user
                    notification.setUserId(chatRoom.getUserId());
                    break;
            }
        } else {
            log.warn("Chat room not found for message: {}. Notification recipient unknown.", message.getId());
        }
        
        notificationRepository.save(notification);
        
        log.info("New message notification sent for message: {}", message.getId());
    }
    
    /**
     * Get notifications for user
     */
    public Page<NotificationResponseDto> getUserNotifications(String userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::mapToResponseDto);
    }
    
    /**
     * Get notifications for client
     */
    public Page<NotificationResponseDto> getClientNotifications(String clientId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository
                .findByClientIdOrderByCreatedAtDesc(clientId, pageable);
        return notifications.map(this::mapToResponseDto);
    }
    
    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification", "id", notificationId));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
        
        log.debug("Notification {} marked as read", notificationId);
    }
    
    /**
     * Get unread count for user
     */
    public Long getUserUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
    
    /**
     * Get unread count for client
     */
    public Long getClientUnreadCount(String clientId) {
        return notificationRepository.countByClientIdAndIsRead(clientId, false);
    }
    
    private NotificationResponseDto mapToResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .clientId(notification.getClientId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .data(notification.getData())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
