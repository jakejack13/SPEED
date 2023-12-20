package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import edu.cornell.testoutputstream.TestOutputStream;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

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
    public void testJUnitRunnerInternal() {

        String currentWorkingDirectory = System.getProperty("user.dir");

        JUnitTestContext context = new JUnitTestContext( new ArrayList<>(
                List.of(
                        "edu.cornell.testenv.testclasses.JUnitClassRunnerTest"
                )
        ));

        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context, new NoopTestOutputStream(), new File(currentWorkingDirectory));
        System.out.println(currentWorkingDirectory);
        assertFalse(result);
    }

    @Test
    public void testJUnitRunnerExternal() {
        String path_to_speed_test = "/Users/owenralbovsky/Projects/SPEED-TESTER";

        JUnitTestContext context = new JUnitTestContext( new ArrayList<>(
                List.of(
                        "org.example.CalcTest"
                )
        ));

        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context, new NoopTestOutputStream(), new File(path_to_speed_test));
        System.out.println(path_to_speed_test);
        assertFalse(result);
    }

}
