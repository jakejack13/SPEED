package edu.cornell.worker;

import lombok.NonNull;

import java.io.Closeable;
import java.util.Set;

/**
 * An interface representing a Worker that executes a subset of a test suite
 */
public interface Worker extends Runnable, Closeable {

    /**
     * Creates a new Worker object
     * @param repoUrl the url of the repository
     * @param repoBranch the branch of the repository to test
     * @param tests the list of tests to run
     * @param kafkaAddress the address of the Kafka message bus to send test results to
     */
    static @NonNull Worker createWorker(@NonNull String repoUrl, @NonNull String repoBranch,
            @NonNull Set<String> tests, @NonNull String kafkaAddress) {
        return new DockerWorker(repoUrl, repoBranch, tests, kafkaAddress);
    }

    /**
     * Returns the id of the worker
     * @return the id of the worker
     */
    @NonNull String getId();
}
