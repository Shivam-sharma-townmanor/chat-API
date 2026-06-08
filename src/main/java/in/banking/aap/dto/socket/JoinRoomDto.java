package in.banking.aap.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the Socket.IO "join-room" event.
 * Sent by a client to join a specific chat room.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomDto {

    private String chatId;
    private String userId;
}
