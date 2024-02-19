package edu.cornell.status;

/**
 * Represents the status of a deployment.
 */
public enum DeploymentStatus {
    /**
     * The leader has been added to the database but the leader has not yet distributed work.
     */
    STARTED,

    /**
     * The leader is building files.
     */
    BUILDING,

    /**
     * The leader has started the worker lifecycle testing process.
     */
    IN_PROGRESS,

    /**
     * The leader has completed.
     */
    DONE,

    /**
     * The leader has failed due to an error.
     */
    FAILED;

    /**
     * Returns just the enum constant name when converted to a string.
     *
     * @return The name of the enum constant.
     */
    @Override
    public String toString() {
        return name();
    }
}
