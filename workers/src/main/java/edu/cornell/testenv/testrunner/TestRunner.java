package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;

/**
 * Runs the given Environment Context and Tracks Test Results
 */
public interface TestRunner {

    boolean runTest(TestEnvContext<String> context);

}
