package in.banking.aap.dto.response;

 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import in.banking.aap.domain.enums.NotificationType;

/**
 * DTO for notification response.
 * Contains notification details for push notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    
    private String id;
    private String userId;
    private String clientId;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, String> data;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
