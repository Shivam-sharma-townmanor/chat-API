package in.banking.aap.dto.response;

 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import in.banking.aap.domain.enums.ConnectionStatus;

/**
 * DTO for connection response.
 * Contains connection details including status and timestamps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionResponseDto {
    
    private String id;
    private String userId;
    private String propertyId;
    private String clientId;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional property details
    private PropertyResponseDto property;
}

