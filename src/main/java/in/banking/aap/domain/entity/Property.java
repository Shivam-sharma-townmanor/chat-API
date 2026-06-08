package in.banking.aap.domain.entity;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Property entity representing hotels/accommodations owned by clients.
 * Users can initiate connections to properties to start chatting.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "properties")
public class Property {
    
    @Id
    private String id;
    
    @Indexed
    private String clientId;
    
    private String name;
    private String address;
    private String city;
    private String country;
    private List<String> images;
    
    @Builder.Default
    @Indexed
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
