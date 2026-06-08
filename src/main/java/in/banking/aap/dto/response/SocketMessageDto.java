package in.banking.aap.dto.response;

import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Socket.IO send-message events.
 * Mirrors SendMessageRequestDto but used for real-time socket communication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessageDto {

    private String chatId;
    private String senderId;
    private SenderType senderType;
    private MessageType messageType;
    private String content;
    private String imageUrl;
}
