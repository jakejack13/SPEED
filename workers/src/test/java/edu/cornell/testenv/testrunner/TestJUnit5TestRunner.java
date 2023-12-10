package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import edu.cornell.testoutputstream.TestOutputStream;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestJUnit5TestRunner {

    private static class NoopTestOutputStream implements TestOutputStream {

        @Override
        public void close() throws IOException {
            // Noop
        }

        @Override
        public void sendTestResult(String testName, TestResult result) {

        }

        @Override
        public void done() {

        }
    }

    @Test
    public void testJUnitRunner() {

        JUnitTestContext context = new JUnitTestContext( new ArrayList<>(List.of("edu.cornell.testenv.testclasses.TestClassJUnit")));

        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context, new NoopTestOutputStream(), null);

        assertFalse(result);
    }

}
