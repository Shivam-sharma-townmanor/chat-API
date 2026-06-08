package in.banking.aap.service;

 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.banking.aap.domain.entity.Connection;
import in.banking.aap.domain.entity.Property;
import in.banking.aap.domain.enums.ConnectionStatus;
import in.banking.aap.dto.request.ConnectionRequestDto;
import in.banking.aap.dto.response.ConnectionResponseDto;
import in.banking.aap.dto.response.PropertyResponseDto;
import in.banking.aap.exception.DuplicateResourceException;
import in.banking.aap.exception.InvalidOperationException;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.repository.ConnectionRepository;
import in.banking.aap.repository.PropertyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing connections between users and properties.
 * Handles connection requests, approvals, and chat room creation triggers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionService {
    
    private final ConnectionRepository connectionRepository;
    private final PropertyRepository propertyRepository;
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;
    
    /**
     * Create a new connection request from user to property
     */
    @Transactional
    public ConnectionResponseDto createConnection(ConnectionRequestDto request) {
        log.info("Creating connection for userId: {} to propertyId: {}", 
                request.getUserId(), request.getPropertyId());
        
        // Check if connection already exists
        if (connectionRepository.existsByUserIdAndPropertyId(
                request.getUserId(), request.getPropertyId())) {
            throw new DuplicateResourceException(
                    "Connection already exists between user and property");
        }
        
        // Get property and verify it exists
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Property", "id", request.getPropertyId()));
        
        if (!property.getIsActive()) {
            throw new InvalidOperationException("Cannot connect to inactive property");
        }
        
        // Create connection
        Connection connection = Connection.builder()
                .userId(request.getUserId())
                .propertyId(request.getPropertyId())
                .clientId(property.getClientId())
                .status(ConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        connection = connectionRepository.save(connection);
        
        // Send notification to client
        notificationService.notifyConnectionRequest(connection);
        
        log.info("Connection created with ID: {}", connection.getId());
        
        return mapToResponseDto(connection, property);
    }
    
    /**
     * Approve a connection request and create chat room
     */
    @Transactional
    public ConnectionResponseDto approveConnection(String connectionId) {
        log.info("Approving connection: {}", connectionId);
        
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Connection", "id", connectionId));
        
        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new InvalidOperationException(
                    "Only pending connections can be approved");
        }
        
        // Update connection status
        connection.setStatus(ConnectionStatus.APPROVED);
        connection.setUpdatedAt(LocalDateTime.now());
        connection = connectionRepository.save(connection);
        
        // Create chat room
        chatRoomService.createChatRoom(connection);
        
        // Notify user
        notificationService.notifyConnectionApproved(connection);
        
        log.info("Connection approved and chat room created for connection: {}", connectionId);
        
        Property property = propertyRepository.findById(connection.getPropertyId())
                .orElse(null);
        
        return mapToResponseDto(connection, property);
    }
    
    /**
     * Reject a connection request
     */
    @Transactional
    public ConnectionResponseDto rejectConnection(String connectionId) {
        log.info("Rejecting connection: {}", connectionId);
        
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Connection", "id", connectionId));
        
        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new InvalidOperationException(
                    "Only pending connections can be rejected");
        }
        
        connection.setStatus(ConnectionStatus.REJECTED);
        connection.setUpdatedAt(LocalDateTime.now());
        connection = connectionRepository.save(connection);
        
        // Notify user
        notificationService.notifyConnectionRejected(connection);
        
        log.info("Connection rejected: {}", connectionId);
        
        Property property = propertyRepository.findById(connection.getPropertyId())
                .orElse(null);
        
        return mapToResponseDto(connection, property);
    }
    
    /**
     * Get connections for a user
     */
    public Page<ConnectionResponseDto> getUserConnections(String userId, Pageable pageable) {
        Page<Connection> connections = connectionRepository.findByUserId(userId, pageable);
        return connections.map(connection -> {
            Property property = propertyRepository.findById(connection.getPropertyId())
                    .orElse(null);
            return mapToResponseDto(connection, property);
        });
    }
    
    /**
     * Get connections for a client
     */
    public Page<ConnectionResponseDto> getClientConnections(String clientId, Pageable pageable) {
        Page<Connection> connections = connectionRepository.findByClientId(clientId, pageable);
        return connections.map(connection -> {
            Property property = propertyRepository.findById(connection.getPropertyId())
                    .orElse(null);
            return mapToResponseDto(connection, property);
        });
    }
    
    /**
     * Get pending connections for a client
     */
    public Page<ConnectionResponseDto> getClientPendingConnections(
            String clientId, Pageable pageable) {
        Page<Connection> connections = connectionRepository.findByClientIdAndStatus(
                clientId, ConnectionStatus.PENDING, pageable);
        return connections.map(connection -> {
            Property property = propertyRepository.findById(connection.getPropertyId())
                    .orElse(null);
            return mapToResponseDto(connection, property);
        });
    }
    
    private ConnectionResponseDto mapToResponseDto(Connection connection, Property property) {
        ConnectionResponseDto dto = ConnectionResponseDto.builder()
                .id(connection.getId())
                .userId(connection.getUserId())
                .propertyId(connection.getPropertyId())
                .clientId(connection.getClientId())
                .status(connection.getStatus())
                .createdAt(connection.getCreatedAt())
                .updatedAt(connection.getUpdatedAt())
                .build();
        
        if (property != null) {
            dto.setProperty(PropertyResponseDto.builder()
                    .id(property.getId())
                    .clientId(property.getClientId())
                    .name(property.getName())
                    .address(property.getAddress())
                    .city(property.getCity())
                    .country(property.getCountry())
                    .images(property.getImages())
                    .isActive(property.getIsActive())
                    .createdAt(property.getCreatedAt())
                    .updatedAt(property.getUpdatedAt())
                    .build());
        }
        
        return dto;
    }
}

