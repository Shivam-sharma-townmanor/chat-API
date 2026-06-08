package in.banking.aap.repository;

 
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.Client;

import java.util.Optional;

/**
 * Repository for Client entity operations.
 * Provides methods to find hotel owners by Clerk ID and email.
 */
@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
    
    Optional<Client> findByClerkId(String clerkId);
    
    Optional<Client> findByEmail(String email);
    
    boolean existsByClerkId(String clerkId);
    
    boolean existsByEmail(String email);
}
