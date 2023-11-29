package edu.cornell.repository;

import java.util.List;
import lombok.NonNull;

/**
 * An object representing the SPEED config file in the repository
 */
public class Config {

    /**
     * Returns the list of commands to build the repository
     * @return the list of commands to build the repository
     */
    public @NonNull List<String> getBuildCommands() {
        return List.of();
    }
}
