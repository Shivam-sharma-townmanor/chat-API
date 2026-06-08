package in.banking.aap.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import in.banking.aap.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Notification entity for push notifications.
 * Sent to users and clients for important events like new messages or inquiries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@CompoundIndex(name = "user_read_idx", def = "{'userId': 1, 'isRead': 1}")
@CompoundIndex(name = "client_read_idx", def = "{'clientId': 1, 'isRead': 1}")
public class Notification {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String clientId;
    
    private NotificationType type;
    private String title;
    private String message;
    
    private Map<String, String> data;
    
    @Builder.Default
    @Indexed
    private Boolean isRead = false;
    
    @Indexed
    private LocalDateTime createdAt;
}

