package edu.cornell.repository;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An exception that is generated when a repository build fails
 * @author Jacob Kerr
 */
public class RepositoryBuildException extends Exception {
    private static final long serialVersionUID = 1234567L;
}
