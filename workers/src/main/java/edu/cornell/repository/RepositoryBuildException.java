package edu.cornell.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An exception that is generated when a repository build fails
 * @author Jacob Kerr
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RepositoryBuildException extends Exception {
    public RepositoryBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
