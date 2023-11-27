package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;

public class JUnit5TestRunner implements TestRunner {
    @Override
    public boolean runTest(TestEnvContext context) {
        try {

            List<String> classPaths = context.getTestClasses();

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(classPaths.stream().map(
                            path -> DiscoverySelectors.selectClass(path)
                    ).toList()).build();

            SummaryGeneratingListener listener = new SummaryGeneratingListener();

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();
                // Register a listener of your choice
                launcher.registerTestExecutionListeners(listener);
                // Discover tests and build a test plan
                TestPlan testPlan = launcher.discover(request);
                // Execute test plan
                launcher.execute(testPlan);
            }

            TestExecutionSummary summary = listener.getSummary();

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
            t.printStackTrace();
            return false; // Return false in case of any exceptions.
        }
    }

}