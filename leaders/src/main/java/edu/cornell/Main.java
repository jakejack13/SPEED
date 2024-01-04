package edu.cornell;

import edu.cornell.testconsumer.TestConsumer;
import edu.cornell.worker.Worker;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
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
        // TODO: Find tests, assign tests to workers

        Set<String> tests = Set.of("org.example.CalcTest"); // FIXME: Replace
        try (CloseableSet<Worker> workers = createWorkerSet(url, branch, tests, kafkaAddress);
                WorkerRunner workerRunner = new WorkerRunner(workers);
                KafkaConsumerRunner kafkaRunner =
                    new KafkaConsumerRunner(kafkaAddress, getWorkerIds(workers),
                            TestConsumer.createTestConsumer(getWorkerIds(workers)))
                ) {
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

    /**
     * Creates the set of workers partitioned across the different tests
     * @param url the url of the repository to test
     * @param branch the branch of the repository to test
     * @param tests the set of tests to execute
     * @param kafkaAddress the Kafka message bus address
     * @return the set of workers
     */
    private static @NonNull CloseableSet<Worker> createWorkerSet(String url, String branch,
            Set<String> tests, String kafkaAddress) {
        CloseableSet<Worker> workers = new CloseableSet<>();
        workers.add(Worker.createWorker(url, branch, tests, kafkaAddress));
        return workers;
    }

    /**
     * Returns the set of worker ids from the given set of workers
     * @param workers the set of workers
     * @return the set of worker ids
     */
    private static @NonNull Set<String> getWorkerIds(CloseableSet<Worker> workers) {
        return workers.stream().map(Worker::getId).collect(Collectors.toSet());
    }
}
