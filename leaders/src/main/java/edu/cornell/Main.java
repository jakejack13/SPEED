package edu.cornell;

import edu.cornell.testconsumer.TestConsumer;
import edu.cornell.worker.Worker;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * The main leader application
 * Environment variables:
 * SPEED_REPO_URL: the url of the repository to clone
 * SPEED_REPO_BRANCH: the branch of the repository to clone
 * SPEED_KAFKA_ADDRESS: the address of the message bus to send test results to
 */
@Slf4j
public class Main {

    /**
     * Debug mode flag, used for turning on debug mode
     */
    public static final boolean DEBUG_MODE = System.getenv("speed.debug") != null &&
            "true".equalsIgnoreCase(System.getenv("speed.debug"));

    /**
     * The name of the REPO_URL environment variable
     */
    public static final @NonNull String ENV_REPO_URL = "SPEED_REPO_URL";

    /**
     * The name of the REPO_BRANCH environment variable
     */
    public static final @NonNull String ENV_REPO_BRANCH = "SPEED_REPO_BRANCH";

    /**
     * The name of the REPO_TESTS environment variable.
     * NOTE: Only used for worker communication, not a leader env variable
     */
    public static final @NonNull String ENV_REPO_TESTS = "SPEED_REPO_TESTS";

    /**
     * The name of the KAFKA_ADDRESS environment variable
     */
    public static final @NonNull String ENV_KAFKA_ADDRESS = "SPEED_KAFKA_ADDRESS";

    public static void main(String[] args) {
        String kafkaAddress = System.getenv(ENV_KAFKA_ADDRESS);
        String url = System.getenv(ENV_REPO_URL);
        String branch = System.getenv(ENV_REPO_BRANCH);
        if (url == null || branch == null || kafkaAddress == null) {
            LOGGER.error("Environment variables missing");
            System.exit(1);
        }
        // TODO: Find tests, create workers, assign tests to workers

        Set<String> workerIds;
        if (DEBUG_MODE) {
             workerIds = Set.of("localhost");
        } else {
            workerIds = Set.of(); // FIXME: Replace
        }
        Set<String> tests = Set.of("org.example.CalcTest"); // FIXME: Replace
        TestConsumer testConsumer = TestConsumer.createTestConsumer(workerIds);
        try (CloseableSet<Worker> workers = new CloseableSet<>();
                WorkerRunner workerRunner = new WorkerRunner(workers);
                KafkaConsumerRunner kafkaRunner =
                    new KafkaConsumerRunner(kafkaAddress, workerIds, testConsumer)
                ) {
            workers.add(Worker.createWorker(url, branch, tests, kafkaAddress));
            Thread workerThread = new Thread(workerRunner);
            Thread kafkaThread = new Thread(kafkaRunner);
            workerThread.start();
            kafkaThread.start();
            workerThread.join();
            kafkaThread.join();
        } catch (Exception e) {
            LOGGER.error("Leader failed", e);
            System.exit(1);
        }
    }
}
