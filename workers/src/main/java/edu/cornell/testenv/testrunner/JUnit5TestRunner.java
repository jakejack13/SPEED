package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;
import edu.cornell.testoutputstream.TestOutputStream;
import edu.cornell.testoutputstream.TestOutputStream.TestResult;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;

/**
 * Runs the given Environment Context and Tracks JUnit Test Results
 */
@Slf4j
public class JUnit5TestRunner implements TestRunner {

    @ToString
    private static class OutputTestExecutionListener implements TestExecutionListener {

        private final TestOutputStream outputStream;

        OutputTestExecutionListener(TestOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier,
                TestExecutionResult testExecutionResult) {
            if (!testIdentifier.isTest()) {
                return;
            }
            TestResult result = switch (testExecutionResult.getStatus()) {
                case ABORTED -> TestResult.EXCEPTION;
                case FAILED -> TestResult.FAILURE;
                case SUCCESSFUL -> TestResult.SUCCESS;
            };
            outputStream.sendTestResult(testIdentifier.getDisplayName(), result);
        }
    }

    /**
     * Main runner that takes in the context and returns the result of JUnit Tests
     * @param context - The environment needed to run the tests
     * @return boolean of whether all JUnit tests passed
     */
    @Override
    public boolean runTest(TestEnvContext<String> context, TestOutputStream outputStream) {
        try {

            List<String> classPaths = context.getTestClasses();

            // Setup Launcher to find and builder test map for JUnit
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(classPaths.stream().map(
                            DiscoverySelectors::selectClass
                    ).toList()).build();

            // Test Result Listeners
            SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
            OutputTestExecutionListener outputListener =
                    new OutputTestExecutionListener(outputStream);

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();
                launcher.registerTestExecutionListeners(summaryListener, outputListener);
                TestPlan testPlan = launcher.discover(request);
                launcher.execute(testPlan);
            }

            TestExecutionSummary summary = summaryListener.getSummary();
            return summary.getFailures().isEmpty();
        } catch (Throwable t) {
            LOGGER.error(t.getLocalizedMessage());
            return false; // Return false in case of any exceptions.
        }
    }

}
