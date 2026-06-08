package in.banking.aap.service;

import in.banking.aap.domain.entity.ChatRoom;
import in.banking.aap.domain.entity.Connection;
import in.banking.aap.dto.response.ChatRoomResponseDto;
import in.banking.aap.exception.DuplicateResourceException;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.exception.UnauthorizedAccessException;
import in.banking.aap.repository.ChatRoomRepository;
import in.banking.aap.repository.ClientRepository;
import in.banking.aap.repository.ConnectionRepository;
import in.banking.aap.repository.PropertyRepository;
import in.banking.aap.repository.UserRepository;
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
 * Unit tests for ChatRoomService.
 * Tests chat room creation, access validation, and unread count management.
 */
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    
    @Mock
    private ChatRoomRepository chatRoomRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private PropertyRepository propertyRepository;
    
    @Mock
    private ConnectionRepository connectionRepository;
    
    @InjectMocks
    private ChatRoomService chatRoomService;
    
    private Connection connection;
    private ChatRoom chatRoom;
    
    @BeforeEach
    void setUp() {
        connection = Connection.builder()
                .id("connection123")
                .userId("user456")
                .clientId("client789")
                .propertyId("property999")
                .build();
        
        chatRoom = ChatRoom.builder()
                .id("chat001")
                .userId("user456")
                .clientId("client789")
                .propertyId("property999")
                .connectionId("connection123")
                .isActive(true)
                .unreadCountUser(0)
                .unreadCountClient(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void createChatRoom_Success() {
        // Given
        when(chatRoomRepository.existsByConnectionId(anyString())).thenReturn(false);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        
        // When
        ChatRoomResponseDto result = chatRoomService.createChatRoom(connection);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user456");
        assertThat(result.getClientId()).isEqualTo("client789");
        assertThat(result.getConnectionId()).isEqualTo("connection123");
        assertThat(result.getIsActive()).isTrue();
        
        verify(chatRoomRepository).existsByConnectionId("connection123");
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }
    
    @Test
    void createChatRoom_AlreadyExists_ThrowsException() {
        // Given
        when(chatRoomRepository.existsByConnectionId(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> chatRoomService.createChatRoom(connection))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
        
        verify(chatRoomRepository).existsByConnectionId("connection123");
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }
    
    @Test
    void getChatRoomById_Success() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(clientRepository.findById(anyString())).thenReturn(Optional.empty());
        when(propertyRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When
        ChatRoomResponseDto result = chatRoomService.getChatRoomById("chat001", "user456");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("chat001");
        
        verify(chatRoomRepository).findById("chat001");
    }
    
    @Test
    void getChatRoomById_UnauthorizedAccess_ThrowsException() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        
        // When & Then
        assertThatThrownBy(() -> chatRoomService.getChatRoomById("chat001", "wrongUser"))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("don't have access");
        
        verify(chatRoomRepository).findById("chat001");
    }
    
    @Test
    void getChatRoomById_NotFound_ThrowsException() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> chatRoomService.getChatRoomById("chat001", "user456"))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(chatRoomRepository).findById("chat001");
    }
    
    @Test
    void updateChatRoomLastMessage_UserSender_IncrementsClientUnread() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        
        // When
        chatRoomService.updateChatRoomLastMessage("chat001", "New message", "user456");
        
        // Then
        verify(chatRoomRepository).findById("chat001");
        verify(chatRoomRepository).save(argThat(room -> 
            room.getLastMessage().equals("New message") && 
            room.getUnreadCountClient() == 1
        ));
    }
    
    @Test
    void updateChatRoomLastMessage_ClientSender_IncrementsUserUnread() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        
        // When
        chatRoomService.updateChatRoomLastMessage("chat001", "Reply message", "client789");
        
        // Then
        verify(chatRoomRepository).findById("chat001");
        verify(chatRoomRepository).save(argThat(room -> 
            room.getLastMessage().equals("Reply message") && 
            room.getUnreadCountUser() == 1
        ));
    }
    
    @Test
    void resetUnreadCount_ForUser_Success() {
        // Given
        chatRoom.setUnreadCountUser(5);
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        
        // When
        chatRoomService.resetUnreadCount("chat001", "user456");
        
        // Then
        verify(chatRoomRepository).findById("chat001");
        verify(chatRoomRepository).save(argThat(room -> 
            room.getUnreadCountUser() == 0
        ));
    }
    
    @Test
    void resetUnreadCount_ForClient_Success() {
        // Given
        chatRoom.setUnreadCountClient(3);
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        
        // When
        chatRoomService.resetUnreadCount("chat001", "client789");
        
        // Then
        verify(chatRoomRepository).findById("chat001");
        verify(chatRoomRepository).save(argThat(room -> 
            room.getUnreadCountClient() == 0
        ));
    }
    
    @Test
    void validateAccess_UserHasAccess_ReturnsTrue() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        
        // When
        boolean result = chatRoomService.validateAccess("chat001", "user456");
        
        // Then
        assertThat(result).isTrue();
        verify(chatRoomRepository).findById("chat001");
    }
    
    @Test
    void validateAccess_ClientHasAccess_ReturnsTrue() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        
        // When
        boolean result = chatRoomService.validateAccess("chat001", "client789");
        
        // Then
        assertThat(result).isTrue();
        verify(chatRoomRepository).findById("chat001");
    }
    
    @Test
    void validateAccess_UnauthorizedUser_ReturnsFalse() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.of(chatRoom));
        
        // When
        boolean result = chatRoomService.validateAccess("chat001", "wrongUser");
        
        // Then
        assertThat(result).isFalse();
        verify(chatRoomRepository).findById("chat001");
    }
    
    @Test
    void validateAccess_ChatNotFound_ReturnsFalse() {
        // Given
        when(chatRoomRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When
        boolean result = chatRoomService.validateAccess("chat001", "user456");
        
        // Then
        assertThat(result).isFalse();
        verify(chatRoomRepository).findById("chat001");
    }
}

