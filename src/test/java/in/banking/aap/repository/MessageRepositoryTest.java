package in.banking.aap.repository;

 
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import in.banking.aap.domain.entity.Message;
import in.banking.aap.domain.enums.MessageStatus;
import in.banking.aap.domain.enums.MessageType;
import in.banking.aap.domain.enums.SenderType;
import in.banking.aap.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for Message entity.
 * Tests MongoDB queries and pagination.
 */
@DataMongoTest
class MessageRepositoryTest {
    
    @Autowired
    private MessageRepository messageRepository;
    
    private Message message1;
    private Message message2;
    
    @BeforeEach
    void setUp() {
        message1 = Message.builder()
                .chatId("chat123")
                .senderId("user456")
                .senderType(SenderType.USER)
                .messageType(MessageType.TEXT)
                .content("First message")
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build();
        
        message2 = Message.builder()
                .chatId("chat123")
                .senderId("client789")
                .senderType(SenderType.CLIENT)
                .messageType(MessageType.TEXT)
                .content("Second message")
                .status(MessageStatus.DELIVERED)
                .createdAt(LocalDateTime.now())
                .build();
        
        messageRepository.saveAll(List.of(message1, message2));
    }
    
    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
    }
    
    @Test
    void findByChatIdOrderByCreatedAtDesc_ReturnsMessagesInCorrectOrder() {
        // When
        Page<Message> result = messageRepository.findByChatIdOrderByCreatedAtDesc(
                "chat123", PageRequest.of(0, 10));
        
        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Second message");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("First message");
    }
}
