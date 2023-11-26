package edu.cornell.testenv.testrunner;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJUnit5TestRunner {

    @Test
    public void testJUnitRunner() {
        JUnit5TestRunner runner = new JUnit5TestRunner();
        boolean result = runner.runTest(
                new ArrayList<>(List.of("edu.cornell.testenv.testclasses.TestClassJUnit"))
        );

        System.out.println("Tests passed: " + result);
    }

}
