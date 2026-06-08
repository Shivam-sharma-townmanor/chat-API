package in.banking.aap.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Socket.IO "typing-start" and "typing-stop" events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingEventDto {

    private String chatId;
    private String userId;
}
