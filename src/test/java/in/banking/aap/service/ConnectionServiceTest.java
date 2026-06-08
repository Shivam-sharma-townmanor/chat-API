package in.banking.aap.service;

import in.banking.aap.domain.entity.Connection;
import in.banking.aap.domain.entity.Property;
import in.banking.aap.domain.enums.ConnectionStatus;
import in.banking.aap.dto.request.ConnectionRequestDto;
import in.banking.aap.dto.response.ConnectionResponseDto;
import in.banking.aap.exception.DuplicateResourceException;
import in.banking.aap.exception.InvalidOperationException;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.repository.ConnectionRepository;
import in.banking.aap.repository.PropertyRepository;
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
 * Unit tests for ConnectionService.
 * Tests connection creation, approval, rejection, and validation logic.
 */
@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {
    
    @Mock
    private ConnectionRepository connectionRepository;
    
    @Mock
    private PropertyRepository propertyRepository;
    
    @Mock
    private ChatRoomService chatRoomService;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private ConnectionService connectionService;
    
    private ConnectionRequestDto connectionRequest;
    private Property property;
    private Connection connection;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        connectionRequest = ConnectionRequestDto.builder()
                .userId("user123")
                .propertyId("property456")
                .build();
        
        property = Property.builder()
                .id("property456")
                .clientId("client789")
                .name("Test Hotel")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        connection = Connection.builder()
                .id("connection001")
                .userId("user123")
                .propertyId("property456")
                .clientId("client789")
                .status(ConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void createConnection_Success() {
        // Given
        when(connectionRepository.existsByUserIdAndPropertyId(anyString(), anyString()))
                .thenReturn(false);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(property));
        when(connectionRepository.save(any(Connection.class))).thenReturn(connection);
        
        // When
        ConnectionResponseDto result = connectionService.createConnection(connectionRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getPropertyId()).isEqualTo("property456");
        assertThat(result.getClientId()).isEqualTo("client789");
        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.PENDING);
        
        verify(connectionRepository).existsByUserIdAndPropertyId("user123", "property456");
        verify(propertyRepository).findById("property456");
        verify(connectionRepository).save(any(Connection.class));
        verify(notificationService).notifyConnectionRequest(any(Connection.class));
    }
    
    @Test
    void createConnection_DuplicateConnection_ThrowsException() {
        // Given
        when(connectionRepository.existsByUserIdAndPropertyId(anyString(), anyString()))
                .thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> connectionService.createConnection(connectionRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Connection already exists");
        
        verify(connectionRepository).existsByUserIdAndPropertyId("user123", "property456");
        verify(propertyRepository, never()).findById(anyString());
        verify(connectionRepository, never()).save(any(Connection.class));
    }
    
    @Test
    void createConnection_PropertyNotFound_ThrowsException() {
        // Given
        when(connectionRepository.existsByUserIdAndPropertyId(anyString(), anyString()))
                .thenReturn(false);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> connectionService.createConnection(connectionRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property");
        
        verify(connectionRepository).existsByUserIdAndPropertyId("user123", "property456");
        verify(propertyRepository).findById("property456");
        verify(connectionRepository, never()).save(any(Connection.class));
    }
    
    @Test
    void createConnection_InactiveProperty_ThrowsException() {
        // Given
        property.setIsActive(false);
        when(connectionRepository.existsByUserIdAndPropertyId(anyString(), anyString()))
                .thenReturn(false);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(property));
        
        // When & Then
        assertThatThrownBy(() -> connectionService.createConnection(connectionRequest))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("inactive property");
        
        verify(connectionRepository).existsByUserIdAndPropertyId("user123", "property456");
        verify(propertyRepository).findById("property456");
        verify(connectionRepository, never()).save(any(Connection.class));
    }
    
    @Test
    void approveConnection_Success() {
        // Given
        when(connectionRepository.findById(anyString())).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenReturn(connection);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(property));
        
        // When
        ConnectionResponseDto result = connectionService.approveConnection("connection001");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.APPROVED);
        
        verify(connectionRepository).findById("connection001");
        verify(connectionRepository).save(any(Connection.class));
        verify(chatRoomService).createChatRoom(any(Connection.class));
        verify(notificationService).notifyConnectionApproved(any(Connection.class));
    }
    
    @Test
    void approveConnection_AlreadyApproved_ThrowsException() {
        // Given
        connection.setStatus(ConnectionStatus.APPROVED);
        when(connectionRepository.findById(anyString())).thenReturn(Optional.of(connection));
        
        // When & Then
        assertThatThrownBy(() -> connectionService.approveConnection("connection001"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("pending connections");
        
        verify(connectionRepository).findById("connection001");
        verify(connectionRepository, never()).save(any(Connection.class));
        verify(chatRoomService, never()).createChatRoom(any(Connection.class));
    }
    
    @Test
    void rejectConnection_Success() {
        // Given
        when(connectionRepository.findById(anyString())).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenReturn(connection);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(property));
        
        // When
        ConnectionResponseDto result = connectionService.rejectConnection("connection001");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.REJECTED);
        
        verify(connectionRepository).findById("connection001");
        verify(connectionRepository).save(any(Connection.class));
        verify(notificationService).notifyConnectionRejected(any(Connection.class));
    }
    
    @Test
    void rejectConnection_ConnectionNotFound_ThrowsException() {
        // Given
        when(connectionRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> connectionService.rejectConnection("connection001"))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(connectionRepository).findById("connection001");
        verify(connectionRepository, never()).save(any(Connection.class));
    }
}

