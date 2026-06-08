package in.banking.aap.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.Message;
import in.banking.aap.domain.enums.MessageStatus;

import java.util.List;

/**
 * Repository for Message entity operations.
 * Provides methods to retrieve and manage chat messages with pagination.
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    Page<Message> findByChatIdOrderByCreatedAtDesc(String chatId, Pageable pageable);
    
    List<Message> findByChatIdOrderByCreatedAtDesc(String chatId);
    
    Long countByChatIdAndStatus(String chatId, MessageStatus status);
    
    List<Message> findByChatIdAndStatus(String chatId, MessageStatus status);
}
