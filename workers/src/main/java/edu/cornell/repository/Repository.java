package edu.cornell.repository;

import java.io.IOException;
import edu.cornell.testoutputstream.TestOutputStream;
import java.io.File;
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
     * The root directory of the project once it's on disk
     */
    private final @NonNull File rootDir;

    private final @NonNull Config config;

    Repository(@NonNull File rootDir) throws ConfigSyntaxException {
        this.rootDir = rootDir;
        this.config = new Config(rootDir.getAbsolutePath() + "/.speed");
    }

    /**
     * Returns the Config object representing the config file
     * @return the Config object representing the config file
     */
    public @NonNull Config getConfig() {
        return config;
    }

    /**
     * Returns the root directory of the repository on disk
     * @return the root directory of the repository on disk
     */
    public @NonNull File getRootDir() { return rootDir; }

    /**
     * Builds the repository and generates build artifacts
     * @param commands the shell commands to run, in order, to build the project
     * @throws RepositoryBuildException if the repository fails to build
     */
    public void build(@NonNull List<String> commands) throws RepositoryBuildException {
        for (String command : commands) {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.directory(rootDir);
                if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
                    builder.command("cmd.exe", "/c", command);
                else
                    builder.command("sh", "-c", command);

                Process process = builder.start();
                if (process.waitFor() != 0) {
                    LOGGER.error("Command exited with nonzero exit code: " + command);
                    throw new RepositoryBuildException("Error executing command: " + command, null);
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Error thrown when executing command: " +
                        command, e);
                throw new RepositoryBuildException("Error executing command: " + command, e);
            }
        }
    }

    /**
     * Runs the given JUnit test classes found in the repository
     * @param tests the list of names of test classes to run
     * @param output the output stream to log test results to
     */
    public abstract void test(@NonNull List<String> tests, @NonNull TestOutputStream output);
}
