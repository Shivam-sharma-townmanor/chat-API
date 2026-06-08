package in.banking.aap.dto.response;

 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user response.
 * Contains basic user information for display in chat interfaces.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    
    private String id;
    private String clerkId;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileImage;
    private Boolean isActive;
}
