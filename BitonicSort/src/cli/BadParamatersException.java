package cli;

/**
 * Custom Exception class for use when bad parameters are input in to the cli.
 */
public class BadParamatersException extends Exception {
    public BadParamatersException(String errorMessage) {
        super(errorMessage);
    }
    public BadParamatersException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
