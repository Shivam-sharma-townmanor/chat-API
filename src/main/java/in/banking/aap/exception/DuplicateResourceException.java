package in.banking.aap.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * Used when a connection already exists between user and property.
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
}
