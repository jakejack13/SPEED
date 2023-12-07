package edu.cornell;

import edu.cornell.repository.Config;
import edu.cornell.repository.Repository;
import edu.cornell.repository.RepositoryFactory;
import edu.cornell.testresultproducer.TestOutputStream;
import edu.cornell.testresultproducer.TestOutputStreamFactory;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * The main worker application
 * Environment variables:
 * SPEED_REPO_URL: the url of the repository to clone
 * SPEED_REPO_BRANCH: the branch of the repository to clone
 * SPEED_REPO_TESTS: the comma-separated list of tests to run
 * SPEED_KAFKA_ADDRESS: the address of the message bus to send tests to
 */
@Slf4j
public class Main {

    /**
     * Debug mode flag, used for turning on debug mode
     */
    public static final boolean DEBUG_MODE = System.getProperty("speed.debug") != null &&
            "true".equalsIgnoreCase(System.getProperty("speed.debug"));

    public static void main(String[] args) {
        String url = System.getenv("SPEED_REPO_URL");
        String branch = System.getenv("SPEED_REPO_BRANCH");
        String tests = System.getenv("SPEED_REPO_TESTS");
        String kafkaAddress = System.getenv("SPEED_KAFKA_ADDRESS");
        if (url == null || branch == null || tests == null || kafkaAddress == null) {
            LOGGER.error("Environment variables missing");
            System.exit(1);
        }
        List<String> listOfTests = Arrays.asList(tests.split(","));
        try (TestOutputStream output = TestOutputStreamFactory.createTestOutputStream(kafkaAddress)) {
            Repository repository = RepositoryFactory.fromGitRepo(url,branch);
            Config config = repository.getConfig();
            repository.build(config.getBuildCommands());
            repository.test(listOfTests, output);
            LOGGER.info(repository.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(1);
        }
    }
}
