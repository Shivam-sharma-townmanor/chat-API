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
 * Client entity representing hotel owners who manage properties.
 * Can chat with users who initiate connection to their properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clients")
public class Client {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String clerkId;
    
    @Indexed(unique = true)
    private String email;
    
    private String name;
    private String businessName;
    private String phoneNumber;
    private String profileImage;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

