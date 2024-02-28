package edu.cornell;

import edu.cornell.partitioner.ClassPartitioner;
import edu.cornell.partitioner.methods.NumSplitPartitionMethod;
import edu.cornell.repository.Repository;
import edu.cornell.repository.RepositoryBuildException;
import edu.cornell.repository.RepositoryCloneException;
import edu.cornell.repository.RepositoryFactory;
import edu.cornell.status.DeploymentStatus;
import edu.cornell.status.StatusUpdater;
import edu.cornell.testconsumer.TestConsumer;
import edu.cornell.worker.Worker;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The main leader application
 * Environment variables:
 * SPEED_REPO_URL: the url of the repository to clone
 * SPEED_REPO_BRANCH: the branch of the repository to clone
 * SPEED_KAFKA_ADDRESS: the address of the message bus to send test results to
 * SPEED_NUM_WORKERS: the number of workers to execute for this deployment
 * DEPLOYMENT_ID: the id of the deployment that this leader belongs to
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

    /**
     * The name of the NUM_WORKERS environment variable
     */
    public static final @NonNull String ENV_NUM_WORKERS = "SPEED_NUM_WORKERS";

    /**
     * The name of the DEPLOYMENT_ID environment variable
     */
    public static final @NonNull String ENV_DEPLOYMENT_ID = "DEPLOYMENT_ID";

    /**
     * Endpoint leader uses to update its status
     */
    public static @NonNull String UPDATE_ENDPOINT = "http://host.docker.internal:5000/update/";

    public static void main(String[] args) {
        Integer deploymentID = Integer.valueOf(System.getenv(ENV_DEPLOYMENT_ID).strip());
        UPDATE_ENDPOINT = UPDATE_ENDPOINT + deploymentID;
        StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.STARTED);
        String kafkaAddress = System.getenv(ENV_KAFKA_ADDRESS);
        String url = System.getenv(ENV_REPO_URL);
        String branch = System.getenv(ENV_REPO_BRANCH);
        LOGGER.info("DEPLOYMENT ID: " + deploymentID);

        int numWorkers = 0;
        try {
            numWorkers = Integer.parseInt(System.getenv(ENV_NUM_WORKERS));
        } catch (NumberFormatException e) {
            StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.FAILED);
            LOGGER.info("Incorrect integer format", e);
            System.exit(1);
        }

        if (url == null || branch == null || kafkaAddress == null) {
            StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.FAILED);
            LOGGER.error("Environment variables missing");
            System.exit(1);
        }

        Set<String> tests = Set.of();
        try {
            Repository repository = RepositoryFactory.fromGitRepo(url, branch);
            StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.BUILDING);
            LOGGER.info("Building");
            repository.build(repository.getConfig().getBuildCommands());
            tests = repository.getTests();
        } catch (RepositoryCloneException e) {
            LOGGER.error("Unable to clone the repository", e);
            System.exit(1);
        } catch (RepositoryBuildException e) {
            LOGGER.error("Unable to build the repository", e);
            System.exit(1);
        }

        StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.IN_PROGRESS);
        try (CloseableSet<Worker> workers = createWorkerSet(url, branch, tests, numWorkers,
                kafkaAddress);
                WorkerRunner workerRunner = new WorkerRunner(workers);
                KafkaConsumerRunner kafkaRunner =
                    new KafkaConsumerRunner(kafkaAddress, getWorkerIds(workers),
                            TestConsumer.createTestConsumer(getWorkerIds(workers)), deploymentID)
                ) {
            Thread workerThread = new Thread(workerRunner);
            Thread kafkaThread = new Thread(kafkaRunner);
            workerThread.start();
            kafkaThread.start();
            workerThread.join();
            kafkaThread.join();
        } catch (Exception e) {
            StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.FAILED);
            LOGGER.error("Leader failed", e);
            System.exit(1);
        }

        StatusUpdater.updateDeploymentStatus(UPDATE_ENDPOINT, DeploymentStatus.DONE);
    }

    /**
     * Creates the set of workers partitioned across the different tests
     * @param url the url of the repository to test
     * @param branch the branch of the repository to test
     * @param tests the set of tests to execute
     * @param numWorkers the number of workers to create
     * @param kafkaAddress the Kafka message bus address
     * @return the set of workers
     */
    private static @NonNull CloseableSet<Worker> createWorkerSet(String url, String branch,
            Set<String> tests, int numWorkers, String kafkaAddress) {
        List<String> testsList = new ArrayList<>(tests);
        ClassPartitioner classPartitioner = new ClassPartitioner(new NumSplitPartitionMethod(), testsList, Math.min(numWorkers, testsList.size()));
        List<Set<String>> partitionedTestsList = classPartitioner.getClasses();
        CloseableSet<Worker> workers = new CloseableSet<>();
        for (Set<String> testSet : partitionedTestsList) {
            workers.add(Worker.createWorker(url,
                    branch,
                    testSet,
                    kafkaAddress));
        }
        return workers;
    }

    /**
     * Returns the set of worker ids from the given set of workers
     * @param workers the set of workers
     * @return the set of worker ids
     */
    private static @NonNull Set<String> getWorkerIds(CloseableSet<Worker> workers) {
        return workers.stream()
                .map(worker -> worker.getId().substring(0, Math.min(worker.getId().length(), 12)))
                .collect(Collectors.toSet());
    }
}
