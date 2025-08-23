package edu.cornell.repository;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A representation of a Java project to build and test.
 */
@Slf4j
@EqualsAndHashCode
@ToString
public abstract class Repository {

    /**
     * The root directory of the project once it's on disk.
     */
    private final @NonNull File rootDir;

    /**
     * The root directory of the project.
     * @return File - the root directory of the project
     */
    public @NonNull File getRootDir() {
        return rootDir;
    }

    /**
     * The configuration for SPEED found in this repository.
     */
    private final @NonNull Config config;

    /**
     * The configuration for SPEED.
     * @return Config - the configuration object for SPEED
     */
    public @NonNull Config getConfig() {
        return config;
    }

    /**
     * Creates a new Repository with the given root directory.
     * @param rootDir the root directory of the project on disk
     * @throws ConfigSyntaxException if there is a syntax error with the config file
     * @throws IOException if there is an error reading the repository on disk
     */
    Repository(@NonNull File rootDir) throws ConfigSyntaxException, IOException {
        this.rootDir = rootDir;
        this.config = new Config(rootDir.getAbsolutePath() + "/.speed");
    }

    /**
     * Returns the set of tests found in the repository.
     * @return the set of tests found in the repository
     */
    public abstract @NonNull Set<String> getTests();

    /**
     * Builds the repository and generates build artifacts.
     * @param commands the shell commands to run, in order, to build the project
     * @throws RepositoryBuildException if the repository fails to build
     */
    public void build(@NonNull List<String> commands) throws RepositoryBuildException {
        for (String command : commands) {
            try {
                ProcessBuilder builder = new ProcessBuilder().inheritIO();
                builder.directory(rootDir);
                if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    builder.command("cmd.exe", "/c", command);
                } else {
                    builder.command("sh", "-c", command);
                }
                Process process = builder.start();
                if (process.waitFor() != 0) {
                    LOGGER.error("Command exited with nonzero exit code: {}", command);
                    throw new RepositoryBuildException("Error executing command: " + command,
                        null);
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Error thrown when executing command: {}", command, e);
                throw new RepositoryBuildException("Error executing command: " + command, e);
            }
        }
    }
}
