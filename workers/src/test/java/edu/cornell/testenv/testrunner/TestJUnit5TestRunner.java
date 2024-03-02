package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import edu.cornell.testoutputstream.TestOutputStream;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the JUnit5 test runner.
 */
public class TestJUnit5TestRunner {

    /**
     * A no-operation output stream to send tests to.
     */
    private static class NoopTestOutputStream implements TestOutputStream {

        @Override
        public void close() throws IOException {
            // Noop
        }

        @Override
        public void sendTestResult(String testName, TestResult result, int elapsedTime) {

        }

        @Override
        public void done() {

        }
    }

    /**
     * Tests internal JUnit runner.
     */
    @Test
    public void testJUnitRunnerInternal() {

        String currentWorkingDirectory = System.getProperty("user.dir");

        JUnitTestContext context = new JUnitTestContext(new ArrayList<>(
                List.of(
                        "edu.cornell.testenv.testclasses.JUnitClassRunnerTest"
                )
        ));

        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context, new NoopTestOutputStream(), 
            new File(currentWorkingDirectory));
        assertFalse(result);
    }

    /**
     * Tests external JUnit runner.
     */
    // @Test
    public void testJUnitRunnerExternal() {
        String pathToSpeedTest = "PATH_TO_SPEED_TESTER";

        JUnitTestContext context = new JUnitTestContext(new ArrayList<>(
                List.of(
                        "org.example.CalcTest"
                )
        ));

        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context, new NoopTestOutputStream(), 
            new File(pathToSpeedTest));
        assertFalse(result);
    }

}
