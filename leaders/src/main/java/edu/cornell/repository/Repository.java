package edu.cornell.repository;

import java.io.File;
import java.util.List;
import java.util.Set;
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
     * The root directory of the project once it's on disk
     */
    private final @NonNull File rootDir;

    /**
     * The root directory of the project
     * @return File - the root directory of the project
     */
    public File getRootDir() {
        return rootDir;
    }

    /**
     * The configuration for SPEED found in this repository
     */
    private final @NonNull Config config;

    /**
     * The configuration for SPEED
     * @return Config - the configuration object for SPEED
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Creates a new Repository with the given root directory
     * @param rootDir the root directory of the project on disk
     * @throws ConfigSyntaxException if there is a syntax error with the config file
     */
    Repository(@NonNull File rootDir) throws ConfigSyntaxException {
        this.rootDir = rootDir;
        this.config = new Config(rootDir.getAbsolutePath() + "/.speed");
    }

    /**
     * Returns the set of tests found in the repository
     * @return the set of tests found in the repository
     */
    public abstract @NonNull Set<String> getTests();
}
