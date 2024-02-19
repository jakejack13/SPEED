package edu.cornell.testenv.testrunner;

import edu.cornell.testoutputstream.TestOutputStream;
import lombok.ToString;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

@ToString
public class OutputTestExecutionListener implements TestExecutionListener {

    private final TestOutputStream outputStream;

    OutputTestExecutionListener(TestOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Store the time taken in nanoseconds for this test to run.
     */
    private long timeTaken = 0;

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        timeTaken = System.nanoTime();
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
                                  TestExecutionResult testExecutionResult) {

        if(!testIdentifier.isTest()) { return; }

        int elapsedTime = (int) (System.nanoTime() - timeTaken);

        TestOutputStream.TestResult result = switch (testExecutionResult.getStatus()) {
            case ABORTED -> TestOutputStream.TestResult.EXCEPTION;
            case FAILED -> TestOutputStream.TestResult.FAILURE;
            case SUCCESSFUL -> TestOutputStream.TestResult.SUCCESS;
        };

        outputStream.sendTestResult(extractClassName(testIdentifier.getUniqueId()) + "$" + testIdentifier.getDisplayName(), result, elapsedTime);
    }

    private String extractClassName(String uniqueId) {
        String[] parts = uniqueId.split("/");
        return parts[1].trim().replace("[class:", "").replace("]", ""); // Assuming the class name is the second part
    }
}