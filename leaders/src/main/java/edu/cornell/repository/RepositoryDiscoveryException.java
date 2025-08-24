package edu.cornell.repository;

/**
 * An exception thrown when a repository fails to discover tests.
 */
public class RepositoryDiscoveryException extends RuntimeException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Creates a new RepositoryDiscoveryException.
     * @param message the message of the exception
     * @param cause the cause of the exception
     */
    public RepositoryDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
