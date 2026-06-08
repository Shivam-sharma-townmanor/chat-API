package in.banking.aap.domain.entity;

 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

 

import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;

import java.time.LocalDateTime;

/**
 * Message entity representing individual messages in a chat room.
 * Supports text, images, and special message types like booking inquiries.
 * Tracks delivery and read status for read receipts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@CompoundIndex(name = "chat_created_idx", def = "{'chatId': 1, 'createdAt': -1}")
public class Message {
    
    @Id
    private String id;
    
    @Indexed
    private String chatId;
    
    @Indexed
    private String senderId;
    
    private SenderType senderType;
    private MessageType messageType;
    
    private String content;
    private String imageUrl;
    
    @Indexed
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;
    
    @Indexed
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
    private LocalDateTime deliveredAt;
}

