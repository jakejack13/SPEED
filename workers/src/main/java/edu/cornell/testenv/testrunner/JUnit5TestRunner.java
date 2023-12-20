package edu.cornell.testenv.testrunner;

import edu.cornell.Main;
import edu.cornell.repository.Repository;
import edu.cornell.testenv.testcontext.JUnitContextClassLoader;
import edu.cornell.testenv.testcontext.TestEnvContext;
import edu.cornell.testoutputstream.TestOutputStream;
import edu.cornell.testoutputstream.TestOutputStream.TestResult;
import java.io.File;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();
                SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
                OutputTestExecutionListener outputTestExecutionListener = new OutputTestExecutionListener(outputStream);
                launcher.registerTestExecutionListeners(summaryListener, outputTestExecutionListener);

                JUnitContextClassLoader.loadClassesFromDirectory(baseRootPath);

                List<DiscoverySelector> selectors = new ArrayList<>();

                for (String classPath : context.getTestClasses()) {
                    selectors.add(DiscoverySelectors.selectClass(classPath));
                }

                LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectors)
                        .build();

                launcher.execute(request);

                TestExecutionSummary summary = summaryListener.getSummary();
                return summary.getTestsFailedCount() == 0 && summary.getTestsSkippedCount() == 0;
            }

        } catch (Exception e) {
            LOGGER.error("Test execution failed", e);
            return false;
        }
    }

}
