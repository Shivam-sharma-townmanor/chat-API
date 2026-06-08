package in.banking.aap.dto.response;
 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for client response.
 * Contains basic client (hotel owner) information for display in chat interfaces.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {
    
    private String id;
    private String clerkId;
    private String email;
    private String name;
    private String businessName;
    private String phoneNumber;
    private String profileImage;
    private Boolean isActive;
}
