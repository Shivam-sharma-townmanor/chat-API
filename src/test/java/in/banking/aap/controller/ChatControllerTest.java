package in.banking.aap.controller;

import in.banking.aap.config.SecurityConfig;
import in.banking.aap.dto.response.ChatRoomResponseDto;
import in.banking.aap.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ChatController.
 * Tests REST endpoints for chat room management (No Authentication Required).
 */
@WebMvcTest(ChatController.class)
@Import(SecurityConfig.class)
class ChatControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ChatRoomService chatRoomService;
    
    private ChatRoomResponseDto chatRoomResponse;
    
    @BeforeEach
    void setUp() {
        chatRoomResponse = ChatRoomResponseDto.builder()
                .id("chat001")
                .userId("user123")
                .clientId("client456")
                .propertyId("prop789")
                .connectionId("conn001")
                .isActive(true)
                .unreadCountUser(0)
                .unreadCountClient(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void getChatRoom_Success() throws Exception {
        // Given
        when(chatRoomService.getChatRoomById(anyString(), anyString()))
                .thenReturn(chatRoomResponse);
        
        // When & Then
        mockMvc.perform(get("/api/chats/chat001")
                .param("requesterId", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("chat001"))
                .andExpect(jsonPath("$.userId").value("user123"));
        
        verify(chatRoomService).getChatRoomById("chat001", "user123");
    }
    
    @Test
    void getUserChatRooms_Success() throws Exception {
        // Given
        Page<ChatRoomResponseDto> chatRoomsPage = new PageImpl<>(Arrays.asList(chatRoomResponse));
        when(chatRoomService.getUserChatRooms(anyString(), any()))
                .thenReturn(chatRoomsPage);
        
        // When & Then
        mockMvc.perform(get("/api/chats/user/user123")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("chat001"))
                .andExpect(jsonPath("$.content[0].userId").value("user123"));
        
        verify(chatRoomService).getUserChatRooms(eq("user123"), any());
    }
    
    @Test
    void getClientChatRooms_Success() throws Exception {
        // Given
        Page<ChatRoomResponseDto> chatRoomsPage = new PageImpl<>(Arrays.asList(chatRoomResponse));
        when(chatRoomService.getClientChatRooms(anyString(), any()))
                .thenReturn(chatRoomsPage);
        
        // When & Then
        mockMvc.perform(get("/api/chats/client/client456")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].clientId").value("client456"));
        
        verify(chatRoomService).getClientChatRooms(eq("client456"), any());
    }
    
    @Test
    void markChatAsRead_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/chats/chat001/read")
                .param("userId", "user123"))
                .andExpect(status().isOk());
        
        verify(chatRoomService).resetUnreadCount("chat001", "user123");
    }
}

