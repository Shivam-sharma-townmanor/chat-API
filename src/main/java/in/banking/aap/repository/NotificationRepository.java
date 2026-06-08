package in.banking.aap.repository;

 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.Notification;

import java.util.List;

/**
 * Repository for Notification entity operations.
 * Provides methods to retrieve notifications for users and clients.
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Page<Notification> findByClientIdOrderByCreatedAtDesc(String clientId, Pageable pageable);
    
    List<Notification> findByUserIdAndIsRead(String userId, Boolean isRead);
    
    List<Notification> findByClientIdAndIsRead(String clientId, Boolean isRead);
    
    Long countByUserIdAndIsRead(String userId, Boolean isRead);
    
    Long countByClientIdAndIsRead(String clientId, Boolean isRead);
}
