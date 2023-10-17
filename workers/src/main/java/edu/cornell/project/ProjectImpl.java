package edu.cornell.project;

import java.io.File;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * A class representing a Java project to build and test
 * @author Jacob Kerr
 */
@EqualsAndHashCode
final class ProjectImpl implements Project {
    /** The root directory of the project once it's cloned */
    private final @NotNull File rootDir;

    /**
     * Creates a new Project with the given root directory
     * @param rootDir -
     */
    ProjectImpl(@NotNull File rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public @NotNull String toString() {
        return "Project[" + rootDir + "]";
    }
}
