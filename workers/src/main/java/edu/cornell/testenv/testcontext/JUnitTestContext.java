package edu.cornell.testenv.testcontext;

import java.util.ArrayList;
import java.util.List;

public class JUnitTestContext implements TestEnvContext<String> {

    List<String> classNames;

    /**
     * Implements Test Environment Context Class
     * Provide the class names and directories required to run JUnit Tests
     * @param classNames
     */
    public JUnitTestContext(List<String> classNames) {
        this.classNames = classNames;
    }

    /**
     *
     * @return Test Class locations as a list of strings
     */
    public List<String> getTestClasses() {
        return classNames;
    }

}
