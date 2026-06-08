package in.banking.aap.dto.request;

import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a new message in a chat room.
 * Supports different message types including text, images, and special requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for sending messages")
public class SendMessageRequestDto {
    
	  @Schema(
		        description = "Chat Room ID",
		        example = "CHAT001"
		    )
    @NotBlank(message = "Chat ID is required")
    private String chatId;
    
	  @Schema(
		        description = "Sender User ID",
		        example = "USR001"
		    )
    @NotBlank(message = "Sender ID is required")
    private String senderId;
    
	  @Schema(
		        description = "Message Type",
		        example = "TEXT"
		    )
    @NotNull(message = "Sender type is required")
    private SenderType senderType;
    
	    @Schema(
	            description = "Message Content",
	            example = "Hello Sir, Is room available?"
	        )
    @NotNull(message = "Message type is required")
    private MessageType messageType;
    
    private String content;
    
    private String imageUrl;
}
