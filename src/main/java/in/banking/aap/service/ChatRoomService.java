package in.banking.aap.service;

 
 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.banking.aap.domain.entity.ChatRoom;
import in.banking.aap.domain.entity.Connection;
import in.banking.aap.dto.response.ChatRoomResponseDto;
import in.banking.aap.dto.response.ClientResponseDto;
import in.banking.aap.dto.response.PropertyResponseDto;
import in.banking.aap.dto.response.UserResponseDto;
import in.banking.aap.exception.DuplicateResourceException;
import in.banking.aap.exception.ResourceNotFoundException;
import in.banking.aap.exception.UnauthorizedAccessException;
import in.banking.aap.repository.ChatRoomRepository;
import in.banking.aap.repository.ClientRepository;
import in.banking.aap.repository.ConnectionRepository;
import in.banking.aap.repository.PropertyRepository;
import in.banking.aap.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * Service for managing chat rooms.
 * Handles chat room creation, retrieval, and updates.
 * Ensures one-to-one chat between user and client per property.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PropertyRepository propertyRepository;
    private final ConnectionRepository connectionRepository;
    
    /**
     * Create a chat room after connection approval
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(Connection connection) {
        log.info("Creating chat room for connection: {}", connection.getId());
        
        // Check if chat room already exists
        if (chatRoomRepository.existsByConnectionId(connection.getId())) {
            throw new DuplicateResourceException(
                    "Chat room already exists for this connection");
        }
        
        ChatRoom chatRoom = ChatRoom.builder()
                .userId(connection.getUserId())
                .clientId(connection.getClientId())
                .propertyId(connection.getPropertyId())
                .connectionId(connection.getId())
                .isActive(true)
                .unreadCountUser(0)
                .unreadCountClient(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        chatRoom = chatRoomRepository.save(chatRoom);
        
        log.info("Chat room created with ID: {}", chatRoom.getId());
        
        return mapToResponseDto(chatRoom);
    }
    
    /**
     * Get chat room by ID with authorization check
     */
    public ChatRoomResponseDto getChatRoomById(String chatId, String requesterId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", "id", chatId));
        
        // Verify requester has access
        if (!chatRoom.getUserId().equals(requesterId) && 
            !chatRoom.getClientId().equals(requesterId)) {
            throw new UnauthorizedAccessException("You don't have access to this chat room");
        }
        
        return mapToResponseDto(chatRoom);
    }
    
    /**
     * Get all chat rooms for a user
     */
    public Page<ChatRoomResponseDto> getUserChatRooms(String userId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findByUserId(userId, pageable);
        return chatRooms.map(this::mapToResponseDto);
    }
    
    /**
     * Get all chat rooms for a client
     */
    public Page<ChatRoomResponseDto> getClientChatRooms(String clientId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findByClientId(clientId, pageable);
        return chatRooms.map(this::mapToResponseDto);
    }
    
    /**
     * Update chat room after new message
     */
    @Transactional
    public void updateChatRoomLastMessage(String chatId, String messageContent, 
                                          String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", "id", chatId));
        
        chatRoom.setLastMessage(messageContent);
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoom.setUpdatedAt(LocalDateTime.now());
        
        // Increment unread count for recipient
        if (chatRoom.getUserId().equals(senderId)) {
            chatRoom.setUnreadCountClient(chatRoom.getUnreadCountClient() + 1);
        } else {
            chatRoom.setUnreadCountUser(chatRoom.getUnreadCountUser() + 1);
        }
        
        chatRoomRepository.save(chatRoom);
    }
    
    /**
     * Reset unread count for user (called when they read messages)
     */
    @Transactional
    public void resetUnreadCount(String chatId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", "id", chatId));
        
        if (chatRoom.getUserId().equals(userId)) {
            chatRoom.setUnreadCountUser(0);
        } else if (chatRoom.getClientId().equals(userId)) {
            chatRoom.setUnreadCountClient(0);
        }
        
        chatRoomRepository.save(chatRoom);
    }

    /**
     * Reset unread count for the RECIPIENT of a message.
     * Called when markAsRead is invoked — the recipient is the party opposite the sender.
     *
     * @param chatId   the chat room ID
     * @param senderId the ID of the original message sender (not the reader)
     */
    @Transactional
    public void resetUnreadCountForRecipient(String chatId, String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", "id", chatId));

        // The recipient is whoever is NOT the sender.
        if (chatRoom.getUserId().equals(senderId)) {
            // Sender is the user → recipient is the client
            chatRoom.setUnreadCountClient(0);
        } else {
            // Sender is the client (or unknown) → recipient is the user
            chatRoom.setUnreadCountUser(0);
        }

        chatRoomRepository.save(chatRoom);
    }
    
    /**
     * Validate if user has access to chat room
     */
    public boolean validateAccess(String chatId, String userId) {
        return chatRoomRepository.findById(chatId)
                .map(room -> room.getUserId().equals(userId) || 
                            room.getClientId().equals(userId))
                .orElse(false);
    }
    
    private ChatRoomResponseDto mapToResponseDto(ChatRoom chatRoom) {
        ChatRoomResponseDto dto = ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .userId(chatRoom.getUserId())
                .clientId(chatRoom.getClientId())
                .propertyId(chatRoom.getPropertyId())
                .connectionId(chatRoom.getConnectionId())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .unreadCountUser(chatRoom.getUnreadCountUser())
                .unreadCountClient(chatRoom.getUnreadCountClient())
                .isActive(chatRoom.getIsActive())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
        
        // Load user details
        userRepository.findById(chatRoom.getUserId()).ifPresent(user -> 
            dto.setUser(UserResponseDto.builder()
                    .id(user.getId())
                    .clerkId(user.getClerkId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImage(user.getProfileImage())
                    .isActive(user.getIsActive())
                    .build()));
        
        // Load client details
        clientRepository.findById(chatRoom.getClientId()).ifPresent(client -> 
            dto.setClient(ClientResponseDto.builder()
                    .id(client.getId())
                    .clerkId(client.getClerkId())
                    .email(client.getEmail())
                    .name(client.getName())
                    .businessName(client.getBusinessName())
                    .phoneNumber(client.getPhoneNumber())
                    .profileImage(client.getProfileImage())
                    .isActive(client.getIsActive())
                    .build()));
        
        // Load property details
        propertyRepository.findById(chatRoom.getPropertyId()).ifPresent(property -> 
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
                    .build()));
        
        return dto;
    }
}

