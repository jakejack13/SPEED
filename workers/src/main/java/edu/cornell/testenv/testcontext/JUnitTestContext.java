package edu.cornell.testenv.testcontext;

import edu.cornell.repository.Repository;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements Test Environment Context Class
 * Uses the TestEnvContext to create the necessary context
 * needed to run JUnit Tests
 */
public class JUnitTestContext implements TestEnvContext<String> {

    private List<String> classNames;

    /**
     * Create The Test Environment Context to Run JUnit Tests
     * @param classNames - List of String paths to JUnit classes to run
     * @param repo the repository on disk
     */
    public JUnitTestContext(List<String> classNames) {
        this.classNames = classNames;
    }

    @Override
    public List<String> getTestClasses() {
        return classNames;
    }

}
