package edu.cornell.project;

import java.io.File;

/**
 * A class representing a Java project to build and test
 * @author Jacob Kerr
 */
final class ProjectImpl implements Project {
    /** The root directory of the project once it's cloned */
    private final File rootDir;

    /**
     * Creates a new Project with the given root directory
     * @param rootDir -
     */
    ProjectImpl(File rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public String toString() {
        return "Project[" + rootDir + "]";
    }
}
