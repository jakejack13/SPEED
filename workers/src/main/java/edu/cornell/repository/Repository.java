package edu.cornell.repository;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A representation of a Java project to build and test
 */
@Slf4j
@EqualsAndHashCode
@ToString
public abstract class Repository {

    /**
     * Returns the Config object representing the config file
     * @return the Config object representing the config file
     */
    public Config getConfig() {
        return null;
    }

    /**
     * Builds the repository and generates build artifacts
     * @param commands the shell commands to run, in order, to build the project
     * @throws RepositoryBuildException if the repository fails to build
     */
    public void build(@NonNull List<String> commands) throws RepositoryBuildException {
        // TODO: Mitch
    }

    /**
     * Runs the given JUnit test classes found in the repository
     * @param tests the list of names of test classes to run
     */
    public abstract void test(@NonNull List<String> tests);
}
