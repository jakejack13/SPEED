package edu.cornell;

import edu.cornell.repository.Config;
import edu.cornell.repository.Repository;
import edu.cornell.repository.RepositoryFactory;
import edu.cornell.testoutputstream.TestOutputStream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;

/**
 * The main worker application.
 * Environment variables:
 * SPEED_REPO_URL: the url of the repository to clone
 * SPEED_REPO_BRANCH: the branch of the repository to clone
 * SPEED_REPO_TESTS: the comma-separated list of tests to run
 * SPEED_KAFKA_ADDRESS: the address of the message bus to send test results to
 */
@Slf4j
public class Main {
    /**
     * The name of the REPO_URL environment variable.
     */
    private static final @NonNull String ENV_REPO_URL = "SPEED_REPO_URL";

    /**
     * The name of the REPO_BRANCH environment variable.
     */
    private static final @NonNull String ENV_REPO_BRANCH = "SPEED_REPO_BRANCH";

    /**
     * The name of the REPO_TESTS environment variable.
     */
    private static final @NonNull String ENV_REPO_TESTS = "SPEED_REPO_TESTS";

    /**
     * The name of the KAFKA_ADDRESS environment variable.
     */
    public static final @NonNull String ENV_KAFKA_ADDRESS = "SPEED_KAFKA_ADDRESS";

    /**
     * The main method for the worker.
     * @param args not used
     */
    public static void main(String[] args) {
        String url = System.getenv(ENV_REPO_URL);
        String branch = System.getenv(ENV_REPO_BRANCH);
        String tests = System.getenv(ENV_REPO_TESTS);
        String kafkaAddress = System.getenv(ENV_KAFKA_ADDRESS);
        if (url == null || branch == null || tests == null || kafkaAddress == null) {
            LOGGER.error("Environment variables missing");
            System.exit(1);
        }
        List<String> listOfTests = Arrays.asList(tests.split(","));
        LOGGER.info("listOfTest: " + listOfTests);
        try (TestOutputStream output = TestOutputStream.createTestOutputStream(kafkaAddress)) {
            Repository repository = RepositoryFactory.fromGitRepo(url, branch);
            Config config = repository.getConfig();
            LOGGER.info("Building");
            repository.build(config.getBuildCommands());
            LOGGER.info("Testing");
            repository.test(listOfTests, output);
            LOGGER.info("Done");
            output.done();
            LOGGER.info(repository.toString());
        } catch (Exception e) {
            LOGGER.error("Worker failed", e);
            System.exit(1);
        }
    }
}
