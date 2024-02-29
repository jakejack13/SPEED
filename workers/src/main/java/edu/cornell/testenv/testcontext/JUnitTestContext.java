package edu.cornell.testenv.testcontext;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

/**
 * Implements Test Environment Context Class
 * Uses the TestEnvContext to create the necessary context
 * needed to run JUnit Tests
 */
public class JUnitTestContext implements TestEnvContext {

    private final @NonNull List<String> classNames;

    /**
     * Create The Test Environment Context to Run JUnit Tests
     * @param classNames - List of String paths to JUnit classes to run.
     * @Precondition: classNames are in the [package.class] format
     */
    public JUnitTestContext(@NonNull List<String> classNames) {
        this.classNames = new ArrayList<>(classNames);
    }

    @Override
    public List<String> getTestClasses() {
        return new ArrayList<>(classNames);
    }

}
