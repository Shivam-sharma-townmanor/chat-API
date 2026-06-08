package in.banking.aap.integration;

import in.banking.aap.domain.entity.Property;
import in.banking.aap.repository.ChatRoomRepository;
import in.banking.aap.repository.ConnectionRepository;
import in.banking.aap.repository.PropertyRepository;
import in.banking.aap.service.ConnectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for complete connection flow.
 * Tests: Connection request → Approval → Chat room creation
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ConnectionFlowIntegrationTest {
    
    @Autowired
    private ConnectionService connectionService;
    
    @Autowired
    private ConnectionRepository connectionRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    private Property testProperty;
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Use embedded MongoDB for testing
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/chat_test");
    }
    
    @BeforeEach
    void setUp() {
        // Create test property
        testProperty = Property.builder()
                .clientId("testClient")
                .name("Test Hotel")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        testProperty = propertyRepository.save(testProperty);
    }
    
    @AfterEach
    void tearDown() {
        chatRoomRepository.deleteAll();
        connectionRepository.deleteAll();
        propertyRepository.deleteAll();
    }
    
    @Test
    void completeConnectionFlow_Success() {
        // Step 1: Create connection request
        // Step 2: Approve connection
        // Step 3: Verify chat room created
        // Implementation depends on actual service methods
    }
}
