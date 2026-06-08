package in.banking.aap.repository;

 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.Connection;
import in.banking.aap.domain.enums.ConnectionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Connection entity operations.
 * Provides methods to manage connection requests between users and properties.
 */
@Repository
public interface ConnectionRepository extends MongoRepository<Connection, String> {
    
    Optional<Connection> findByUserIdAndPropertyId(String userId, String propertyId);
    
    List<Connection> findByUserId(String userId);
    
    Page<Connection> findByUserId(String userId, Pageable pageable);
    
    List<Connection> findByClientId(String clientId);
    
    Page<Connection> findByClientId(String clientId, Pageable pageable);
    
    List<Connection> findByUserIdAndStatus(String userId, ConnectionStatus status);
    
    List<Connection> findByClientIdAndStatus(String clientId, ConnectionStatus status);
    
    Page<Connection> findByClientIdAndStatus(String clientId, ConnectionStatus status, Pageable pageable);
    
    boolean existsByUserIdAndPropertyId(String userId, String propertyId);
}

