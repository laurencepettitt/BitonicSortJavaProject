package cli;

/**
 * Custom exception class for use when there is an issue constructing a class, in a factory method.
 */
public class FactoryException extends Exception {
    public FactoryException(String errorMessage) {
        super(errorMessage);
    }
    public FactoryException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
