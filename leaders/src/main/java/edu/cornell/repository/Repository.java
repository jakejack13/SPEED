package edu.cornell.repository;

import java.io.File;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A representation of a Java project to find runnable test files in
 */
@Slf4j
@EqualsAndHashCode
@ToString
public abstract class Repository {

    /**
     * The root directory of the project once it's on disk
     */
    protected final @NonNull File rootDir;

    protected Repository(@NonNull File rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Returns the Config object representing the config file
     * @return the Config object representing the config file
     */
    public Config getConfig() {
        return new Config();
    }

    /**
     * Returns the list of runnable test files in the repository
     * @return the list of runnable test files in the repository
     */
    public abstract @NonNull List<String> getTestFiles();
}
