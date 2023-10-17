package edu.cornell.repository;

import java.util.List;
import lombok.NonNull;

/**
 * A representation of a Java project to build and test
 * @author Jacob Kerr
 */
public interface Repository {

    /**
     * Builds the repository and generates build artifacts
     * @param commands the shell commands to run, in order, to build the project
     * @throws RepositoryBuildException if the repository fails to build
     */
    void build(@NonNull List<String> commands) throws RepositoryBuildException;

    /**
     * Runs the given JUnit test classes found in the repository
     * @param tests the list of names of test classes to run
     */
    void test(@NonNull List<String> tests);
}
