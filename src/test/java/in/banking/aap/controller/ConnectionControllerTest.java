package in.banking.aap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.banking.aap.config.SecurityConfig;
import in.banking.aap.domain.enums.ConnectionStatus;
import in.banking.aap.dto.request.ConnectionRequestDto;
import in.banking.aap.dto.response.ConnectionResponseDto;
import in.banking.aap.service.ConnectionService;
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
 * Integration tests for ConnectionController.
 * Tests REST endpoints for connection management (No Authentication Required).
 */
@WebMvcTest(ConnectionController.class)
@Import(SecurityConfig.class)
class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConnectionService connectionService;

    private ConnectionRequestDto connectionRequest;
    private ConnectionResponseDto connectionResponse;

    @BeforeEach
    void setUp() {
        connectionRequest = ConnectionRequestDto.builder()
                .userId("user123")
                .propertyId("property456")
                .build();

        connectionResponse = ConnectionResponseDto.builder()
                .id("connection001")
                .userId("user123")
                .propertyId("property456")
                .clientId("client789")
                .status(ConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createConnection_Success() throws Exception {
        // Given
        when(connectionService.createConnection(any(ConnectionRequestDto.class)))
                .thenReturn(connectionResponse);

        // When & Then
        mockMvc.perform(post("/api/connections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("connection001"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(connectionService).createConnection(any(ConnectionRequestDto.class));
    }

    @Test
    void createConnection_ValidationError_ReturnsBadRequest() throws Exception {
        // Given - Invalid request (missing required fields)
        connectionRequest.setUserId(null);

        // When & Then
        mockMvc.perform(post("/api/connections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveConnection_Success() throws Exception {
        // Given
        connectionResponse.setStatus(ConnectionStatus.APPROVED);
        when(connectionService.approveConnection(anyString()))
                .thenReturn(connectionResponse);

        // When & Then
        mockMvc.perform(put("/api/connections/connection001/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("connection001"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(connectionService).approveConnection("connection001");
    }

    @Test
    void rejectConnection_Success() throws Exception {
        // Given
        connectionResponse.setStatus(ConnectionStatus.REJECTED);
        when(connectionService.rejectConnection(anyString()))
                .thenReturn(connectionResponse);

        // When & Then
        mockMvc.perform(put("/api/connections/connection001/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("connection001"))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(connectionService).rejectConnection("connection001");
    }

    @Test
    void getUserConnections_Success() throws Exception {
        // Given
        Page<ConnectionResponseDto> connectionsPage = new PageImpl<>(Arrays.asList(connectionResponse));
        when(connectionService.getUserConnections(anyString(), any()))
                .thenReturn(connectionsPage);

        // When & Then
        mockMvc.perform(get("/api/connections/user/user123")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("connection001"))
                .andExpect(jsonPath("$.content[0].userId").value("user123"));

        verify(connectionService).getUserConnections(eq("user123"), any());
    }

    @Test
    void getClientConnections_Success() throws Exception {
        // Given
        Page<ConnectionResponseDto> connectionsPage = new PageImpl<>(Arrays.asList(connectionResponse));
        when(connectionService.getClientConnections(anyString(), any()))
                .thenReturn(connectionsPage);

        // When & Then
        mockMvc.perform(get("/api/connections/client/client789")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].clientId").value("client789"));

        verify(connectionService).getClientConnections(eq("client789"), any());
    }

    @Test
    void getClientPendingConnections_Success() throws Exception {
        // Given
        Page<ConnectionResponseDto> connectionsPage = new PageImpl<>(Arrays.asList(connectionResponse));
        when(connectionService.getClientPendingConnections(anyString(), any()))
                .thenReturn(connectionsPage);

        // When & Then
        mockMvc.perform(get("/api/connections/client/client789/pending")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));

        verify(connectionService).getClientPendingConnections(eq("client789"), any());
    }
}
