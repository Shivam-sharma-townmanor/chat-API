package in.banking.aap.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import in.banking.aap.dto.response.SocketMessageDto;
import in.banking.aap.dto.socket.JoinRoomDto;
import in.banking.aap.dto.socket.MessageStatusDto;
import in.banking.aap.dto.socket.TypingEventDto;
import in.banking.aap.service.MessageService;
import in.banking.aap.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Socket.IO Event Handler.
 * Manages WebSocket connections, room joining, messaging, and presence.
 * 
 * Events handled:
 * - connect: Client connection
 * - disconnect: Client disconnection
 * - join-room: Join a chat room
 * - send-message: Send a message
 * - typing-start: User started typing
 * - typing-stop: User stopped typing
 * - message-delivered: Message delivered confirmation
 * - message-read: Message read confirmation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SocketIOEventHandler {
    
    private final SocketIOServer server;
    private final SocketService socketService;
    private final MessageService messageService;
    
    @PostConstruct
    public void init() {
        server.addConnectListener(onConnect());
        server.addDisconnectListener(onDisconnect());
        
        // Join room event
        server.addEventListener("join-room", JoinRoomDto.class, 
                (client, data, ackSender) -> onJoinRoom(client, data));
        
        // Message events
        server.addEventListener("send-message", SocketMessageDto.class,
                (client, data, ackSender) -> onSendMessage(client, data));
        
        server.addEventListener("message-delivered", MessageStatusDto.class,
                (client, data, ackSender) -> onMessageDelivered(client, data));
        
        server.addEventListener("message-read", MessageStatusDto.class,
                (client, data, ackSender) -> onMessageRead(client, data));
        
        // Typing events
        server.addEventListener("typing-start", TypingEventDto.class,
                (client, data, ackSender) -> onTypingStart(client, data));
        
        server.addEventListener("typing-stop", TypingEventDto.class,
                (client, data, ackSender) -> onTypingStop(client, data));
        
        server.start();
        log.info("Socket.IO server started successfully");
    }
    
    @PreDestroy
    public void destroy() {
        server.stop();
        log.info("Socket.IO server stopped");
    }
    
    private ConnectListener onConnect() {
        return client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            log.info("Client connected: {} with userId: {}", client.getSessionId(), userId);
            
            // Store user ID in client session
            client.set("userId", userId);
            
            // Notify online status
            socketService.notifyUserOnline(userId);
        };
    }
    
    private DisconnectListener onDisconnect() {
        return client -> {
            String userId = client.get("userId");
            log.info("Client disconnected: {} with userId: {}", client.getSessionId(), userId);
            
            // Notify offline status
            if (userId != null) {
                socketService.notifyUserOffline(userId);
            }
        };
    }
    
    private void onJoinRoom(SocketIOClient client, JoinRoomDto data) {
        try {
            log.info("Client {} joining room: {}", client.getSessionId(), data.getChatId());
            
            // Validate user access to chat room
            boolean hasAccess = socketService.validateChatRoomAccess(data.getChatId(), data.getUserId());
            
            if (!hasAccess) {
                client.sendEvent("error", "Unauthorized access to chat room");
                return;
            }
            
            // Join the room
            client.joinRoom(data.getChatId());
            client.set("currentChatId", data.getChatId());
            
            // Send confirmation
            client.sendEvent("room-joined", data.getChatId());
            
            log.info("Client {} successfully joined room: {}", client.getSessionId(), data.getChatId());
            
        } catch (Exception e) {
            log.error("Error joining room", e);
            client.sendEvent("error", "Failed to join room: " + e.getMessage());
        }
    }
    
    private void onSendMessage(SocketIOClient client, SocketMessageDto data) {
        try {
            log.info("Message received from client {}: {}", client.getSessionId(), data.getChatId());
            
            // Save message to database
            var savedMessage = messageService.sendMessage(data);
            
            // Broadcast to room participants
            server.getRoomOperations(data.getChatId()).sendEvent("receive-message", savedMessage);
            
            log.info("Message sent to room: {}", data.getChatId());
            
        } catch (Exception e) {
            log.error("Error sending message", e);
            client.sendEvent("error", "Failed to send message: " + e.getMessage());
        }
    }
    
    private void onMessageDelivered(SocketIOClient client, MessageStatusDto data) {
        try {
            messageService.markAsDelivered(data.getMessageId());
            
            // Notify sender
            server.getRoomOperations(data.getChatId()).sendEvent("message-status-updated", data);
            
        } catch (Exception e) {
            log.error("Error marking message as delivered", e);
        }
    }
    
    private void onMessageRead(SocketIOClient client, MessageStatusDto data) {
        try {
            messageService.markAsRead(data.getMessageId());
            
            // Notify sender
            server.getRoomOperations(data.getChatId()).sendEvent("message-status-updated", data);
            
        } catch (Exception e) {
            log.error("Error marking message as read", e);
        }
    }
    
    private void onTypingStart(SocketIOClient client, TypingEventDto data) {
        try {
            // Broadcast to other participants
            client.getNamespace().getRoomOperations(data.getChatId())
                    .sendEvent("user-typing", data);
            
        } catch (Exception e) {
            log.error("Error broadcasting typing start", e);
        }
    }
    
    private void onTypingStop(SocketIOClient client, TypingEventDto data) {
        try {
            // Broadcast to other participants
            client.getNamespace().getRoomOperations(data.getChatId())
                    .sendEvent("user-stopped-typing", data);
            
        } catch (Exception e) {
            log.error("Error broadcasting typing stop", e);
        }
    }
}

