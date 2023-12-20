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

    @ToString
    private static class OutputTestExecutionListener implements TestExecutionListener {

        private final TestOutputStream outputStream;

        OutputTestExecutionListener(TestOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void executionSkipped(TestIdentifier testIdentifier, String reason) {
            System.out.println(testIdentifier.getDisplayName() + " skipped for " + reason);
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier,
                                      TestExecutionResult testExecutionResult) {
            if(!testIdentifier.isTest()) { return; }

            TestResult result = switch (testExecutionResult.getStatus()) {
                case ABORTED -> TestResult.EXCEPTION;
                case FAILED -> TestResult.FAILURE;
                case SUCCESSFUL -> TestResult.SUCCESS;
            };
            outputStream.sendTestResult(testIdentifier.getDisplayName(), result);
        }
    }

    private static class CustomTestExecutionListener implements TestExecutionListener {
        @Override
        public void testPlanExecutionStarted(TestPlan testPlan) {
            LOGGER.info("Test plan execution started");
        }

        @Override
        public void testPlanExecutionFinished(TestPlan testPlan) {
            LOGGER.info("Test plan execution finished");
        }

        @Override
        public void dynamicTestRegistered(TestIdentifier testIdentifier) {
            LOGGER.info("Dynamic test registered: {}", testIdentifier);
        }

        @Override
        public void executionSkipped(TestIdentifier testIdentifier, String reason) {
            LOGGER.info("Execution skipped for test {}: {}", testIdentifier, reason);
        }

        @Override
        public void executionStarted(TestIdentifier testIdentifier) {
            LOGGER.info("Execution started for test: {}", testIdentifier);
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            LOGGER.info("Execution finished for test {}: {}", testIdentifier, testExecutionResult);
        }
    }



    /**
     * Main runner that takes in the context and returns the result of JUnit Tests
     * @param context - The environment needed to run the tests
     * @return boolean of whether all JUnit tests passed
     */
    @Override
    public boolean runTest(TestEnvContext<String> context, TestOutputStream outputStream, File rootDir) {
        try {
            String baseRootPath = rootDir.toString() + "/build/classes/java/".replaceAll("/", File.separator);
            String baseTestRootPath = baseRootPath + "test" + File.separator;

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();
                SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
                launcher.registerTestExecutionListeners(summaryListener, new CustomTestExecutionListener());

                JUnitContextClassLoader.loadClassesFromDirectory(baseRootPath);

                List<DiscoverySelector> selectors = new ArrayList<>();

                for (String classPath : context.getTestClasses()) {
                    List<String> splitString = Arrays.asList(classPath.split("\\."));
                    String classRootPath = String.join(File.separator, splitString) + ".class";
                    String pathToTestClass = baseTestRootPath + classRootPath;

                    selectors.add(DiscoverySelectors.selectClass(classPath));
                }

                LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectors)
                        .build();

                launcher.execute(request);

                TestExecutionSummary summary = summaryListener.getSummary();
                LOGGER.info("Number of Tests Run: {}", summary.getTestsFoundCount());
                LOGGER.info("Number of Tests Successful: {}", summary.getTestsSucceededCount());
                LOGGER.info("Number of Tests Failed: {}", summary.getTestsFailedCount());
                LOGGER.info("Number of Tests Skipped: {}", summary.getTestsSkippedCount());

                // Return true if all tests passed
                return summary.getTestsFailedCount() == 0 && summary.getTestsSkippedCount() == 0;
            }

        } catch (Exception e) {
            LOGGER.error("Test execution failed", e);
            return false;
        }
    }

    private void executeTest(String classPath, Class<?> clazz, Launcher launcher) {
//
    }

//    // Setup Launcher to find and builder test map for JUnit
//    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
//            .selectors(selectors)
//            .build();
//
//
//            if(Main.DEBUG_MODE) {
//        LOGGER.info("DEBUG: Printing Selectors");
//        List<DiscoverySelector> selectorsList = request.getSelectorsByType(DiscoverySelector.class);
//        for (DiscoverySelector selector : selectorsList) {
//            LOGGER.info("DEBUG: Selector: {}", selector);
//        }
//    }
//
//    // Test Result Listeners
//    SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
//    OutputTestExecutionListener outputListener =
//            new OutputTestExecutionListener(outputStream);
//
//            try (LauncherSession session = LauncherFactory.openSession()) {
//        Launcher launcher = session.getLauncher();
//        launcher.registerTestExecutionListeners(summaryListener, new CustomTestExecutionListener());
//        TestPlan testPlan = launcher.discover(request);
//
//        testPlan.getRoots().forEach(root -> {
//            LOGGER.info("Root: {}", root.getUniqueId());
//            LOGGER.info("Children: {}", testPlan.getChildren(root.getUniqueId()));
//            testPlan.getChildren(root.getUniqueId()).forEach(testIdentifier -> {
//                LOGGER.info("Test Identifier: {}", testIdentifier);
//            });
//        });
//
//        launcher.execute(testPlan);
//    }
//
//    TestExecutionSummary summary = summaryListener.getSummary();
//
//    // Print information about the run tests
//            LOGGER.info("Number of Tests Run: {}", summary.getTestsFoundCount());
//            LOGGER.info("Number of Tests Successful: {}", summary.getTestsSucceededCount());
//            LOGGER.info("Number of Tests Failed: {}", summary.getTestsFailedCount());
//            LOGGER.info("Number of Tests Skipped: {}", summary.getTestsSkippedCount());


}
