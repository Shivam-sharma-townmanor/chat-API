package in.banking.aap.dto.response;

import in.banking.aap.domain.enums.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for broadcasting online/offline status over Socket.IO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineStatusDto {

    private String userId;
    private SenderType userType;
    private Boolean isOnline;
}
