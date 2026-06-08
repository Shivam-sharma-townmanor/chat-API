package in.banking.aap.repository;

 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ChatRoom entity operations.
 * Provides methods to find chat rooms for users and clients.
 */
@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    
    Optional<ChatRoom> findByConnectionId(String connectionId);
    
    List<ChatRoom> findByUserId(String userId);
    
    Page<ChatRoom> findByUserId(String userId, Pageable pageable);
    
    List<ChatRoom> findByClientId(String clientId);
    
    Page<ChatRoom> findByClientId(String clientId, Pageable pageable);
    
    Optional<ChatRoom> findByUserIdAndClientIdAndPropertyId(String userId, String clientId, String propertyId);
    
    boolean existsByConnectionId(String connectionId);
}

