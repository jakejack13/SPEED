package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestPlan;
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

    /**
     * Main runner that takes in the context and returns the result of JUnit Tests
     * @param context - The environment needed to run the tests
     * @return boolean of whether all JUnit tests passed
     */
    @Override
    public boolean runTest(TestEnvContext<String> context) {
        try {

            List<String> classPaths = context.getTestClasses();

            // Setup Launcher to find and builder test map for JUnit
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(classPaths.stream().map(
                            path -> DiscoverySelectors.selectClass(path)
                    ).toList()).build();

            // Test Result Listener
            SummaryGeneratingListener listener = new SummaryGeneratingListener();

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();

                launcher.registerTestExecutionListeners(listener);

                TestPlan testPlan = launcher.discover(request);

                launcher.execute(testPlan);
            }

            TestExecutionSummary summary = listener.getSummary();

            //TODO: Placeholder Logging Until Logging Object Properly Set
            System.out.println("Test Execution Summary:");
            System.out.println("Total Tests: " + summary.getTestsFoundCount());
            System.out.println("Successful Tests: " + summary.getTestsSucceededCount());
            System.out.println("Failed Tests: " + summary.getTestsFailedCount());

            // Print specific information about failures
            System.out.println("\nDetails of Failed Tests:");
            summary.getFailures().forEach(failure -> {
                System.out.println("Test: " + failure.getTestIdentifier().getDisplayName());
            });

            return summary.getFailures().isEmpty();
        } catch (Throwable t) {
            LOGGER.error(t.getLocalizedMessage());
            return false; // Return false in case of any exceptions.
        }
    }

}
