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
    protected final @NonNull File rootDir;

    protected Repository(@NonNull File rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Returns the Config object representing the config file
     * @return the Config object representing the config file
     */
    public @NonNull Config getConfig() {
        return new Config();
    }

    /**
     * Builds the repository and generates build artifacts
     * @param commands the shell commands to run, in order, to build the project
     * @throws RepositoryBuildException if the repository fails to build
     */
    public void build(@NonNull List<String> commands) throws RepositoryBuildException {
        for (String command : commands) {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
                    builder.command("cmd.exe", "/c", command);
                else
                    builder.command("sh", "-c", command);

                Process process = builder.start();
                if (process.waitFor() != 0)
                    throw new RepositoryBuildException("Error executing command: " + command, null);
            } catch (IOException | InterruptedException e) {
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
