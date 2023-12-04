package edu.cornell.workers;

import lombok.NonNull;

/**
 * An interface representing a worker executing a set of unit tests on a separate machine
 */
public interface Worker {

    /**
     * Returns the unique id of the worker
     * @return the unique id of the worker
     */
    @NonNull String getId();

    /**
     * Starts the execution of the worker
     */
    void start();

    /**
     * Returns whether the worker has finished its execution
     * @return true if the worker has finished its work, false otherwise
     */
    boolean isDone();

    /**
     * Cleans up the resources of this worker. Note that this will end
     * execution of this worker if it is still running
     */
    void cleanup();
}
