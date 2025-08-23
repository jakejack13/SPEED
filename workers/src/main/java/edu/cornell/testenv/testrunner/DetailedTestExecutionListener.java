package edu.cornell.testenv.testrunner;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

/**
 * Test Execution Listener to Provide Lots of information during JUnit Test Class Execution.
 * Mainly for debugging, purpose is to determine what is going on throughout JUnit Launcher 
 * lifecycle.
 */
@Slf4j
public class DetailedTestExecutionListener implements TestExecutionListener {

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        LOGGER.info("Execution started for: {}{}",
                testIdentifier.isTest() ? "Test: " : "Container: ",
                testIdentifier.getDisplayName());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, 
            TestExecutionResult testExecutionResult) {
        LOGGER.info("Execution finished for: {}{}, Result: {}",
                testIdentifier.isTest() ? "Test: " : "Container: ", testIdentifier.getDisplayName(),
                testExecutionResult.getStatus());
        testExecutionResult.getThrowable().ifPresent(t -> LOGGER.info("Test Exception: ", t));
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        LOGGER.info("Execution skipped for: {}, Reason: {}", testIdentifier.getDisplayName(),
                reason);
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        LOGGER.info("Dynamic test registered: {}", testIdentifier.getDisplayName());
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        LOGGER.info("Reporting entry published for: {}, Entry: {}", testIdentifier.getDisplayName(),
                entry);
    }
}
