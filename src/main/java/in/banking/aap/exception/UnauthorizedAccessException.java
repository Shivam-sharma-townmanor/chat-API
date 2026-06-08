package in.banking.aap.exception;
 

/**
 * Exception thrown when a user attempts unauthorized access.
 * Used for security violations like accessing someone else's chat.
 */
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

