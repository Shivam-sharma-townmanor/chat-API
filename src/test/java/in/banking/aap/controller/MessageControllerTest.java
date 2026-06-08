package in.banking.aap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.banking.aap.config.SecurityConfig;
import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;
import in.banking.aap.dto.request.SendMessageRequestDto;
import in.banking.aap.dto.response.MessageResponseDto;
import in.banking.aap.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MessageController.
 * Tests REST endpoints for message operations (No Authentication Required).
 */
@WebMvcTest(MessageController.class)
@Import(SecurityConfig.class)
class MessageControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MessageService messageService;
    
    private SendMessageRequestDto messageRequest;
    private MessageResponseDto messageResponse;
    
    @BeforeEach
    void setUp() {
        messageRequest = SendMessageRequestDto.builder()
                .chatId("chat123")
                .senderId("user456")
                .senderType(SenderType.USER)
                .messageType(MessageType.TEXT)
                .content("Hello")
                .build();
        
        messageResponse = MessageResponseDto.builder()
                .id("message789")
                .chatId("chat123")
                .senderId("user456")
                .senderType(SenderType.USER)
                .messageType(MessageType.TEXT)
                .content("Hello")
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void sendMessage_Success() throws Exception {
        // Given
        when(messageService.sendMessage(any(SendMessageRequestDto.class)))
                .thenReturn(messageResponse);
        
        // When & Then
        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("message789"))
                .andExpect(jsonPath("$.chatId").value("chat123"))
                .andExpect(jsonPath("$.content").value("Hello"));
        
        verify(messageService).sendMessage(any(SendMessageRequestDto.class));
    }
    
    @Test
    void sendMessage_ValidationError_ReturnsBadRequest() throws Exception {
        // Given - Invalid request (missing required fields)
        messageRequest.setChatId(null);
        
        // When & Then
        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getChatMessages_Success() throws Exception {
        // Given
        Page<MessageResponseDto> messagesPage = new PageImpl<>(Arrays.asList(messageResponse));
        when(messageService.getChatMessages(anyString(), anyString(), any()))
                .thenReturn(messagesPage);
        
        // When & Then
        mockMvc.perform(get("/api/messages/chat/chat123")
                .param("requesterId", "user456")
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("message789"))
                .andExpect(jsonPath("$.content[0].content").value("Hello"));
        
        verify(messageService).getChatMessages(eq("chat123"), eq("user456"), any());
    }
    
    @Test
    void markAsDelivered_Success() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/messages/message789/delivered"))
                .andExpect(status().isOk());
        
        verify(messageService).markAsDelivered("message789");
    }
    
    @Test
    void markAsRead_Success() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/messages/message789/read"))
                .andExpect(status().isOk());
        
        verify(messageService).markAsRead("message789");
    }
    
    @Test
    void deleteMessage_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/messages/message789")
                .param("requesterId", "user456"))
                .andExpect(status().isNoContent());
        
        verify(messageService).deleteMessage("message789", "user456");
    }
}
