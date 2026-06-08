package in.banking.aap.controller;

import in.banking.aap.dto.response.ChatRoomResponseDto;
import in.banking.aap.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for chat room management.
 * 
 * Endpoints:
 * GET /api/chats/{chatId} - Get specific chat room details
 * GET /api/chats/user/{userId} - Get chat rooms for a user
 * GET /api/chats/client/{clientId} - Get chat rooms for a client
 */
@Slf4j
@Tag(name = "Chats", description = "Chat Room APIs")
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatRoomService chatRoomService;
    
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoom(
            @PathVariable String chatId,
            @RequestParam(required = false) String requesterId) {
        
        log.info("Getting chat room: {} for user: {}", chatId, requesterId);
        
        ChatRoomResponseDto chatRoom = chatRoomService.getChatRoomById(chatId, requesterId);
        return ResponseEntity.ok(chatRoom);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ChatRoomResponseDto>> getUserChatRooms(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting chat rooms for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoomResponseDto> chatRooms = chatRoomService.getUserChatRooms(userId, pageable);
        
        return ResponseEntity.ok(chatRooms);
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<ChatRoomResponseDto>> getClientChatRooms(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting chat rooms for client: {}", clientId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoomResponseDto> chatRooms = chatRoomService.getClientChatRooms(
                clientId, pageable);
        
        return ResponseEntity.ok(chatRooms);
    }
    
    @PostMapping("/{chatId}/read")
    public ResponseEntity<Void> markChatAsRead(
            @PathVariable String chatId,
            @RequestParam String userId) {
        
        log.info("Marking chat {} as read for user: {}", chatId, userId);
        
        chatRoomService.resetUnreadCount(chatId, userId);
        return ResponseEntity.ok().build();
    }
}
