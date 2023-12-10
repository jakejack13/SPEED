package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import edu.cornell.testenv.testcontext.TestEnvContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TestJUnit5TestRunner {

    @Test
    void testJUnitRunner() {

        TestEnvContext<String> context = new JUnitTestContext(
                new ArrayList<>(List.of("edu.cornell.testenv.testclasses.TestClassJUnit")));

        TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context);

        assertFalse(result);
    }

}
