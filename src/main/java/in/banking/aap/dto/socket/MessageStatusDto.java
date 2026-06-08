package in.banking.aap.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Socket.IO "message-delivered" and "message-read" events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusDto {

    private String messageId;
    private String chatId;
}
