package in.banking.aap.controller;

import in.banking.aap.dto.request.ConnectionRequestDto;
import in.banking.aap.dto.response.ConnectionResponseDto;
import in.banking.aap.service.ConnectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for connection management.
 * 
 * Endpoints:
 * POST /api/connections - Create connection request
 * PUT /api/connections/{id}/approve - Approve connection
 * PUT /api/connections/{id}/reject - Reject connection
 * GET /api/connections/user/{userId} - Get user connections
 * GET /api/connections/client/{clientId} - Get client connections
 * GET /api/connections/client/{clientId}/pending - Get pending requests
 */
@Slf4j
@CrossOrigin
@Tag(name = "Connections", description = "Connection Management APIs")
@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionController {
    
    private final ConnectionService connectionService;
    
    @PostMapping
    public ResponseEntity<ConnectionResponseDto> createConnection(
            @Valid @RequestBody ConnectionRequestDto request) {
        
        log.info("Creating connection request from user: {}", request.getUserId());
        
        ConnectionResponseDto response = connectionService.createConnection(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<ConnectionResponseDto> approveConnection(
            @PathVariable String id) {
        
        log.info("Approving connection: {}", id);
        
        ConnectionResponseDto response = connectionService.approveConnection(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<ConnectionResponseDto> rejectConnection(
            @PathVariable String id) {
        
        log.info("Rejecting connection: {}", id);
        
        ConnectionResponseDto response = connectionService.rejectConnection(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ConnectionResponseDto>> getUserConnections(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting connections for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConnectionResponseDto> connections = connectionService.getUserConnections(
                userId, pageable);
        
        return ResponseEntity.ok(connections);
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<ConnectionResponseDto>> getClientConnections(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting connections for client: {}", clientId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConnectionResponseDto> connections = connectionService.getClientConnections(
                clientId, pageable);
        
        return ResponseEntity.ok(connections);
    }
    
    @GetMapping("/client/{clientId}/pending")
    public ResponseEntity<Page<ConnectionResponseDto>> getClientPendingConnections(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting pending connections for client: {}", clientId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConnectionResponseDto> connections = 
                connectionService.getClientPendingConnections(clientId, pageable);
        
        return ResponseEntity.ok(connections);
    }
}
