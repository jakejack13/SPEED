package edu.cornell.repository;

import java.io.Serial;

/**
 * An exception that is generated when a repository build fails.
 */
public class RepositoryCloneException extends Exception {
    @Serial
    private static final long serialVersionUID = 1234565L;

    /**
     * Creates a new RepositoryCloneException with the given message and backtrace.
     * @param message the message of the exception
     * @param backtrace the backtrace of the exception
     */
    public RepositoryCloneException(String message, Throwable backtrace) {
        super(message, backtrace);
    }
}
