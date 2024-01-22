package edu.cornell.repository;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import edu.cornell.testenv.testrunner.JUnit5TestRunner;
import edu.cornell.testoutputstream.TestOutputStream;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.File;
import java.util.List;

/**
 * A class representing a Java project using JUnit 5 for testing
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class JUnit5RepositoryImpl extends Repository {

    JUnit5RepositoryImpl(@NonNull File rootDir) throws ConfigSyntaxException {
        super(rootDir);
    }

    /**
     * Run JUnit Test Runner
     * @param tests the list of names of test classes to run
     * @param output the output stream to log test results to
     */
    @Override
    public void test(@NonNull List<String> tests, TestOutputStream output) {
        JUnitTestContext context = new JUnitTestContext(tests);
        JUnit5TestRunner runner = new JUnit5TestRunner();
        List<String> configTestPaths = getConfig().getTestPaths();
        for(String testPath : configTestPaths) {
            runner.runTest(context, output, new File(getRootDir() + testPath.trim()));
        }
    }
}
