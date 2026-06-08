package in.banking.aap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;

/**
 * DTO for message response.
 * Contains message details including delivery status and timestamps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    
    private String id;
    private String chatId;
    private String senderId;
    private SenderType senderType;
    private MessageType messageType;
    private String content;
    private String imageUrl;
    private MessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
    private LocalDateTime deliveredAt;
}

