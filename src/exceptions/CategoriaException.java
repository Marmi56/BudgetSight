package exceptions;

/**
 * Exception thrown when a category operation fails.
 * This includes category search, removal, and validation errors.
 *
 * @author Computer
 * @since 1.0
 */
public class CategoriaException extends Exception {

    /**
     * Constructs a CategoriaException with no detail message.
     */
    public CategoriaException() {
    }

    /**
     * Constructs a CategoriaException with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    public CategoriaException(String message) {
        super(message);
    }
}
