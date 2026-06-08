package in.banking.aap.dto.response;

 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for property response.
 * Contains property details including owner information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDto {
    
    private String id;
    private String clientId;
    private String name;
    private String address;
    private String city;
    private String country;
    private List<String> images;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
