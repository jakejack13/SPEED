package edu.cornell.testenv.testcontext;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements Test Environment Context Class
 * Uses the TestEnvContext to create the necessary context
 * needed to run JUnit Tests
 */
public class JUnitTestContext implements TestEnvContext<String> {

    List<String> classNames;

    /**
     * Create The Test Environment Context to Run JUnit Tests
     * @param classNames - List of String paths to JUnit classes to run
     */
    public JUnitTestContext(List<String> classNames) {
        this.classNames = classNames;
    }

    /**
     * @return Test Class locations as a list of strings
     */
    public List<String> getTestClasses() {
        return classNames;
    }

}