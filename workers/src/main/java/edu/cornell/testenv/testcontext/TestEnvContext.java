package edu.cornell.testenv.testcontext;

import java.util.List;

/**
 * Provide The Necessary Environment Context To Run Tests
 * List can be provided as a Generic for now
 * Ex: JUnit needs the location of test classes for the Launcher to run
 */
public interface TestEnvContext<T> {

    /**
     * Each element has a test class in a format that allows it to be run by the desired library.
     * @return The location of the test classes
     */
    List<T> getTestClasses();

}
