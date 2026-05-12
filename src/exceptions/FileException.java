package exceptions;

/**
 * Exception thrown when a file operation fails.
 * This includes file reading, writing, creation, and path validation errors.
 *
 * @author Computer
 * @since 1.0
 */
public class FileException extends Exception {

    /**
     * Constructs a FileException with no detail message.
     */
    public FileException() {
        super();
    }

    /**
     * Constructs a FileException with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    public FileException(String message) {
        super(message);
    }
}
