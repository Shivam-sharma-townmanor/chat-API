package in.banking.aap.domain.entity;

 


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import in.banking.aap.domain.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * Connection entity representing a connection request from a user to a property.
 * Chat room is only created after connection is approved.
 * Ensures one user can only connect once to a specific property.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "connections")
@CompoundIndex(name = "user_property_unique", def = "{'userId': 1, 'propertyId': 1}", unique = true)
public class Connection {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String propertyId;
    
    @Indexed
    private String clientId;
    
    @Indexed
    @Builder.Default
    private ConnectionStatus status = ConnectionStatus.PENDING;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

