package in.banking.aap.exception;
/**
 * Exception thrown when an invalid operation is attempted.
 * Used for business logic violations.
 */
public class InvalidOperationException extends RuntimeException {
    
    public InvalidOperationException(String message) {
        super(message);
    }
}
