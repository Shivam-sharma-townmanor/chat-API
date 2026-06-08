package in.banking.aap.repository;

 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.Property;

import java.util.List;

/**
 * Repository for Property entity operations.
 * Provides methods to find properties by client and active status.
 */
@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    
    List<Property> findByClientId(String clientId);
    
    Page<Property> findByClientId(String clientId, Pageable pageable);
    
    List<Property> findByIsActive(Boolean isActive);
    
    Page<Property> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<Property> findByClientIdAndIsActive(String clientId, Boolean isActive, Pageable pageable);
}

