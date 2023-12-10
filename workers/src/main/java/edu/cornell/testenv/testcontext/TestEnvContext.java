package edu.cornell.testenv.testcontext;

import java.util.List;

/**
 * Provide The Necessary Environment Context To Run Tests
 * List can be provided as a Generic for now
 * Ex: JUnit needs the location of test classes for the Launcher to run
 */
public interface TestEnvContext<T> {

    List<T> getTestClasses();

}
