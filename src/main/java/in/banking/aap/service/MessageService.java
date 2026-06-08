package in.banking.aap.service;

 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.banking.aap.domain.entity.Message;
import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.dto.request.SendMessageRequestDto;
import in.banking.aap.dto.response.MessageResponseDto;
import in.banking.aap.dto.response.SocketMessageDto;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.exception.UnauthorizedAccessException;
import in.banking.aap.repository.MessageRepository;

import java.time.LocalDateTime;

/**
 * Service for managing messages in chat rooms.
 * Handles message creation, retrieval, status updates, and deletion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;
    
    /**
     * Send a new message in a chat room
     */
    @Transactional
    public MessageResponseDto sendMessage(SendMessageRequestDto request) {
        log.info("Sending message to chatId: {}", request.getChatId());
        
        // Validate sender has access to chat room
        if (!chatRoomService.validateAccess(request.getChatId(), request.getSenderId())) {
            throw new UnauthorizedAccessException("You don't have access to this chat room");
        }
        
        Message message = Message.builder()
                .chatId(request.getChatId())
                .senderId(request.getSenderId())
                .senderType(request.getSenderType())
                .messageType(request.getMessageType())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()) 
                .build();
        
        message = messageRepository.save(message);
        
        // Update chat room last message
        chatRoomService.updateChatRoomLastMessage(
                request.getChatId(), 
                request.getContent() != null ? request.getContent() : "Image",
                request.getSenderId());
        
        // Send notification
        notificationService.notifyNewMessage(message);
        
        log.info("Message sent with ID: {}", message.getId());
        
        return mapToResponseDto(message);
    }
    
    /**
     * Send message from Socket.IO event
     */
    @Transactional
    public MessageResponseDto sendMessage(SocketMessageDto socketDto) {
        SendMessageRequestDto request = SendMessageRequestDto.builder()
                .chatId(socketDto.getChatId())
                .senderId(socketDto.getSenderId())
                .senderType(socketDto.getSenderType())
                .messageType(socketDto.getMessageType())
                .content(socketDto.getContent())
                .imageUrl(socketDto.getImageUrl())
                .build();
        
        return sendMessage(request);
    }
    
    /**
     * Get messages for a chat room with pagination
     */
    public Page<MessageResponseDto> getChatMessages(String chatId, String requesterId, 
                                                    Pageable pageable) {
        // Validate requester has access
        if (!chatRoomService.validateAccess(chatId, requesterId)) {
            throw new UnauthorizedAccessException("You don't have access to this chat room");
        }
        
        Page<Message> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(
                chatId, pageable);
        
        return messages.map(this::mapToResponseDto);
    }
    
    /**
     * Mark message as delivered
     */
    @Transactional
    public void markAsDelivered(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        if (message.getStatus() == MessageStatus.SENT) {
            message.setStatus(MessageStatus.DELIVERED);
            message.setDeliveredAt(LocalDateTime.now());
            message.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(message);
            
            log.debug("Message {} marked as delivered", messageId);
        }
    }
    
    /**
     * Mark message as read.
     * Resets the unread count for the recipient who is reading the message
     * (i.e. the party that is NOT the original sender).
     */
    @Transactional
    public void markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        message.setStatus(MessageStatus.READ);
        message.setReadAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);

        // The reader is the party opposite to the sender.
        // ChatRoomService.resetUnreadCount resolves the correct counter based on the ID passed.
        // We pass the senderId here so the service knows which counter NOT to reset —
        // actually we need to pass the RECIPIENT id. ChatRoomService checks userId vs clientId,
        // so we use the chatRoom to resolve who the recipient is. Since MessageService does not
        // have direct access to chatRoom participants, we delegate to ChatRoomService using a
        // dedicated resetUnreadCountForRecipient that accepts the senderId to determine the
        // counter to reset.
        chatRoomService.resetUnreadCountForRecipient(message.getChatId(), message.getSenderId());

        log.debug("Message {} marked as read", messageId);
    }
    
    /**
     * Delete a message
     */
    @Transactional
    public void deleteMessage(String messageId, String requesterId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        // Only sender can delete their message
        if (!message.getSenderId().equals(requesterId)) {
            throw new UnauthorizedAccessException("You can only delete your own messages");
        }
        
        messageRepository.deleteById(messageId);
        
        log.info("Message {} deleted by user {}", messageId, requesterId);
    }
    
    private MessageResponseDto mapToResponseDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .chatId(message.getChatId())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .imageUrl(message.getImageUrl())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .readAt(message.getReadAt())
                .deliveredAt(message.getDeliveredAt())
                .build();
    }
}
