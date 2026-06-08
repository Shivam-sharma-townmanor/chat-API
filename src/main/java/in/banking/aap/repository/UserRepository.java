package in.banking.aap.repository;

 
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.banking.aap.domain.entity.User;

import java.util.Optional;

/**
 * Repository for User entity operations.
 * Provides methods to find users by Clerk ID and email.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByClerkId(String clerkId);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByClerkId(String clerkId);
    
    boolean existsByEmail(String email);
}

