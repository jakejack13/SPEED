package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.JUnitContextClassLoader;
import edu.cornell.testenv.testcontext.TestEnvContext;
import edu.cornell.testoutputstream.TestOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.File;
import java.util.ArrayList;
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
    public boolean runTest(TestEnvContext<String> context, TestOutputStream outputStream, File rootDir) {
        try {
            //TODO: Assumption is class files live in /build/classes/java/ likely needing a resolve
            String baseRootPath = rootDir.toString() + "/build/classes/java/".replaceAll("/", File.separator);

            // Open a Launcher session
            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();

                //Create Listeners
                SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
                OutputTestExecutionListener outputTestExecutionListener = new OutputTestExecutionListener(outputStream);

                // Register Listeners
                launcher.registerTestExecutionListeners(summaryListener, outputTestExecutionListener);

                // Load all build files
                JUnitContextClassLoader.loadClassesFromDirectory(baseRootPath);
                List<DiscoverySelector> selectors = new ArrayList<>();
                for (String classPath : context.getTestClasses()) {
                    // Select each test class
                    selectors.add(DiscoverySelectors.selectClass(classPath));
                }

                LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectors)
                        .build();

                launcher.execute(request);

                TestExecutionSummary summary = summaryListener.getSummary();

                // Return if all test cases pass
                return summary.getTestsFailedCount() == 0 && summary.getTestsSkippedCount() == 0;
            }

        } catch (Exception e) {
            LOGGER.error("Test execution failed", e);
            return false;
        }
    }

}
