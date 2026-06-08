package in.banking.aap.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * ChatRoom entity representing a one-to-one chat between user and client.
 * Created automatically when a connection is approved.
 * One connection can have only one chat room.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_rooms")
public class ChatRoom {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String clientId;
    
    @Indexed
    private String propertyId;
    
    @Indexed(unique = true)
    private String connectionId;
    
    private String lastMessage;
    
    @Indexed
    private LocalDateTime lastMessageAt;
    
    @Builder.Default
    private Integer unreadCountUser = 0;
    
    @Builder.Default
    private Integer unreadCountClient = 0;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

