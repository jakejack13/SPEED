package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestJUnit5TestRunner {

    @Test
    public void testJUnitRunner() {

        JUnitTestContext context = new JUnitTestContext( new ArrayList<>(List.of("edu.cornell.testenv.testclasses.TestClassJUnit")));

        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(context);

        assertFalse(result);
    }

}