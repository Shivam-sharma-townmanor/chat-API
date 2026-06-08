package in.banking.aap.dto.request;

 
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new connection request.
 * User initiates connection to a property owned by a client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequestDto {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Property ID is required")
    private String propertyId;
}
