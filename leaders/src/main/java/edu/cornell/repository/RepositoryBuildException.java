package edu.cornell.repository;

import java.io.Serial;

/**
 * An exception that is generated when a repository build fails.
 */
public class RepositoryBuildException extends Exception {
    @Serial
    private static final long serialVersionUID = 1234567L;

    /**
     * Creates a new RepositoryBuildException with the given message and backtrace.
     * @param message the message of the exception
     * @param backtrace the backtrace of the exception
     */
    public RepositoryBuildException(String message, Throwable backtrace) {
        super(message, backtrace);
    }
}
