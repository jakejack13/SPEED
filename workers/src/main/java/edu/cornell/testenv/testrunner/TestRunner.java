package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;

public interface TestRunner {

    /**
     * Spec for Processing Given Tests
     * @param envInput - The environment needed to run the tests
     * @return boolean whether or not all tests passed TODO: Change to log
     */
    boolean runTest(TestEnvContext envInput);

}
