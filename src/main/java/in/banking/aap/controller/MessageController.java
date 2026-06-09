package in.banking.aap.controller;

import in.banking.aap.dto.request.SendMessageRequestDto;
import in.banking.aap.dto.response.MessageResponseDto;
import in.banking.aap.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * REST Controller for message management.
 * 
 * Endpoints:
 * POST /api/messages - Send a new message
 * GET /api/messages/chat/{chatId} - Get messages for a chat
 * DELETE /api/messages/{messageId} - Delete a message
 * PUT /api/messages/{messageId}/delivered - Mark message as delivered
 * PUT /api/messages/{messageId}/read - Mark message as read
 */



@Tag(name = "Messages", description = "Message Management APIs")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @Operation(
            summary = "Send Message",
            description = "Send a new message to a chat room"
    )
    @ApiResponse(responseCode = "201", description = "Message sent successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @PostMapping
    public ResponseEntity<MessageResponseDto> sendMessage(
            @Valid @RequestBody SendMessageRequestDto request) {
        
        log.info("Sending message to chat: {}", request.getChatId());
        
        MessageResponseDto response = messageService.sendMessage(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Page<MessageResponseDto>> getChatMessages(
            @PathVariable String chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String requesterId) {
        
        log.info("Getting messages for chat: {} by user: {}", chatId, requesterId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponseDto> messages = messageService.getChatMessages(
                chatId, requesterId, pageable);
        
        return ResponseEntity.ok(messages);
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable String messageId,
            @RequestParam String requesterId) {
        
        log.info("Deleting message: {} by user: {}", messageId, requesterId);
        
        messageService.deleteMessage(messageId, requesterId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{messageId}/delivered")
    public ResponseEntity<Void> markAsDelivered(
            @PathVariable String messageId) {
        
        log.info("Marking message as delivered: {}", messageId);
        
        messageService.markAsDelivered(messageId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String messageId) {
        
        log.info("Marking message as read: {}", messageId);
        
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
    
 
   
}
