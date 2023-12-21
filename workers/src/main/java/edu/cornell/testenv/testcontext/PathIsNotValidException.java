package edu.cornell.testenv.testcontext;

/**
 * Exception is thrown when the given path is not a directory or does not exist
 */
public class PathIsNotValidException extends RuntimeException {

    /**
     * Creates a new PathIsNotValidException with the given message and backtrace
     * @param message the message of the exception
     * @param backtrace the backtrace of the exception
     */
    public PathIsNotValidException(String message, Throwable backtrace) {
        super(message, backtrace);
    }
}
