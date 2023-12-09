package edu.cornell.testenv.testcontext;

import java.util.List;

public interface TestEnvContext<T> {

    /**
     * Provide The Necessary Environment Context To Run Tests
     * Ex: JUnit needs the location of test classes for the Launcher to run
     */
    List<T> getTestClasses();

}
