package in.banking.aap.service;

import in.banking.aap.domain.entity.Message;
import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;
import in.banking.aap.dto.request.SendMessageRequestDto;
import in.banking.aap.dto.response.MessageResponseDto;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.exception.UnauthorizedAccessException;
import in.banking.aap.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MessageService.
 * Tests message sending, status updates, and authorization.
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    
    @Mock
    private MessageRepository messageRepository;
    
    @Mock
    private ChatRoomService chatRoomService;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private MessageService messageService;
    
    private SendMessageRequestDto messageRequest;
    private Message message;
    
    @BeforeEach
    void setUp() {
        messageRequest = SendMessageRequestDto.builder()
                .chatId("chat123")
                .senderId("user456")
                .senderType(SenderType.USER)
                .messageType(MessageType.TEXT)
                .content("Hello, I'd like to book a room")
                .build();
        
        message = Message.builder()
                .id("message789")
                .chatId("chat123")
                .senderId("user456")
                .senderType(SenderType.USER)
                .messageType(MessageType.TEXT)
                .content("Hello, I'd like to book a room")
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void sendMessage_Success() {
        // Given
        when(chatRoomService.validateAccess(anyString(), anyString())).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        
        // When
        MessageResponseDto result = messageService.sendMessage(messageRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatId()).isEqualTo("chat123");
        assertThat(result.getSenderId()).isEqualTo("user456");
        assertThat(result.getContent()).isEqualTo("Hello, I'd like to book a room");
        assertThat(result.getStatus()).isEqualTo(MessageStatus.SENT);
        
        verify(chatRoomService).validateAccess("chat123", "user456");
        verify(messageRepository).save(any(Message.class));
        verify(chatRoomService).updateChatRoomLastMessage(anyString(), anyString(), anyString());
        verify(notificationService).notifyNewMessage(any(Message.class));
    }
    
    @Test
    void sendMessage_UnauthorizedAccess_ThrowsException() {
        // Given
        when(chatRoomService.validateAccess(anyString(), anyString())).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> messageService.sendMessage(messageRequest))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("don't have access");
        
        verify(chatRoomService).validateAccess("chat123", "user456");
        verify(messageRepository, never()).save(any(Message.class));
        verify(notificationService, never()).notifyNewMessage(any(Message.class));
    }
    
    @Test
    void sendMessage_BookingInquiry_Success() {
        // Given
        messageRequest.setMessageType(MessageType.BOOKING_INQUIRY);
        messageRequest.setContent("Is a room available for next weekend?");
        message.setMessageType(MessageType.BOOKING_INQUIRY);
        
        when(chatRoomService.validateAccess(anyString(), anyString())).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        
        // When
        MessageResponseDto result = messageService.sendMessage(messageRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessageType()).isEqualTo(MessageType.BOOKING_INQUIRY);
        
        verify(messageRepository).save(any(Message.class));
        verify(notificationService).notifyNewMessage(any(Message.class));
    }
    
    @Test
    void sendMessage_ImageMessage_Success() {
        // Given
        messageRequest.setMessageType(MessageType.IMAGE);
        messageRequest.setContent(null);
        messageRequest.setImageUrl("https://s3.amazonaws.com/bucket/image.jpg");
        message.setMessageType(MessageType.IMAGE);
        message.setImageUrl("https://s3.amazonaws.com/bucket/image.jpg");
        
        when(chatRoomService.validateAccess(anyString(), anyString())).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        
        // When
        MessageResponseDto result = messageService.sendMessage(messageRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessageType()).isEqualTo(MessageType.IMAGE);
        assertThat(result.getImageUrl()).isEqualTo("https://s3.amazonaws.com/bucket/image.jpg");
        
        verify(chatRoomService).updateChatRoomLastMessage("chat123", "Image", "user456");
    }
    
    @Test
    void markAsDelivered_Success() {
        // Given
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        
        // When
        messageService.markAsDelivered("message789");
        
        // Then
        verify(messageRepository).findById("message789");
        verify(messageRepository).save(any(Message.class));
    }
    
    @Test
    void markAsDelivered_MessageNotFound_ThrowsException() {
        // Given
        when(messageRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> messageService.markAsDelivered("message789"))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(messageRepository).findById("message789");
        verify(messageRepository, never()).save(any(Message.class));
    }
    
    @Test
    void markAsRead_Success() {
        // Given
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        
        // When
        messageService.markAsRead("message789");
        
        // Then
        verify(messageRepository).findById("message789");
        verify(messageRepository).save(any(Message.class));
        verify(chatRoomService).resetUnreadCountForRecipient("chat123", "user456");
    }
    
    @Test
    void deleteMessage_Success() {
        // Given
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));
        
        // When
        messageService.deleteMessage("message789", "user456");
        
        // Then
        verify(messageRepository).findById("message789");
        verify(messageRepository).deleteById("message789");
    }
    
    @Test
    void deleteMessage_UnauthorizedUser_ThrowsException() {
        // Given
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));
        
        // When & Then
        assertThatThrownBy(() -> messageService.deleteMessage("message789", "wrongUser"))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("only delete your own messages");
        
        verify(messageRepository).findById("message789");
        verify(messageRepository, never()).deleteById(anyString());
    }
}

