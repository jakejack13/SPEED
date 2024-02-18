package edu.cornell.status;

import lombok.ToString;

public enum DeploymentStatus {
    STARTED,
    BUILDING,
    IN_PROGRESS,
    DONE,
    FAILED;

    @Override
    public String toString() {
        return name(); // Returns just the enum constant name
    }
}
