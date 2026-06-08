package in.banking.aap.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for chat room response.
 * Contains chat room details with participant information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    
    private String id;
    private String userId;
    private String clientId;
    private String propertyId;
    private String connectionId;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCountUser;
    private Integer unreadCountClient;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional details
    private UserResponseDto user;
    private ClientResponseDto client;
    private PropertyResponseDto property;
}

