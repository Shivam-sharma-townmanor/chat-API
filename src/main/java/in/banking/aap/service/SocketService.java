package in.banking.aap.service;

import com.corundumstudio.socketio.SocketIOServer;

import in.banking.aap.domain.enums.SenderType;
import in.banking.aap.dto.response.OnlineStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for Socket.IO operations.
 * Manages user presence, room validation, and event broadcasting.
 * Uses in-memory storage for online users (replace with Redis in production).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {
    
    private final SocketIOServer socketServer;
    private final ChatRoomService chatRoomService;
    
    // In-memory storage for online users (use Redis in production)
    private final Map<String, Boolean> onlineUsers = new ConcurrentHashMap<>();
    
    /**
     * Mark user as online
     */
    public void notifyUserOnline(String userId) {
        onlineUsers.put(userId, true);
        
        // Broadcast online status to relevant rooms
        broadcastOnlineStatus(userId, true);
        
        log.debug("User {} is now online", userId);
    }
    
    /**
     * Mark user as offline
     */
    public void notifyUserOffline(String userId) {
        onlineUsers.remove(userId);
        
        // Broadcast offline status to relevant rooms
        broadcastOnlineStatus(userId, false);
        
        log.debug("User {} is now offline", userId);
    }
    
    /**
     * Check if user is online
     */
    public boolean isUserOnline(String userId) {
        return onlineUsers.getOrDefault(userId, false);
    }
    
    /**
     * Validate if user has access to chat room
     */
    public boolean validateChatRoomAccess(String chatId, String userId) {
        return chatRoomService.validateAccess(chatId, userId);
    }
    
    /**
     * Broadcast online/offline status to relevant chat rooms
     */
    private void broadcastOnlineStatus(String userId, boolean isOnline) {
        OnlineStatusDto statusDto = OnlineStatusDto.builder()
                .userId(userId)
                .userType(SenderType.USER)
                .isOnline(isOnline)
                .build();
        
        // Broadcast to all connected clients
        socketServer.getBroadcastOperations().sendEvent(
                isOnline ? "user-online" : "user-offline", 
                statusDto
        );
    }
    
    /**
     * Send event to specific chat room
     */
    public void sendEventToRoom(String roomId, String eventName, Object data) {
        socketServer.getRoomOperations(roomId).sendEvent(eventName, data);
        log.debug("Event {} sent to room {}", eventName, roomId);
    }
    
    /**
     * Get online users count
     */
    public long getOnlineUsersCount() {
        return onlineUsers.size();
    }
}
