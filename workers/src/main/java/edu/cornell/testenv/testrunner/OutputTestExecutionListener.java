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

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
                                  TestExecutionResult testExecutionResult) {

        if(!testIdentifier.isTest()) { return; }

        TestOutputStream.TestResult result = switch (testExecutionResult.getStatus()) {
            case ABORTED -> TestOutputStream.TestResult.EXCEPTION;
            case FAILED -> TestOutputStream.TestResult.FAILURE;
            case SUCCESSFUL -> TestOutputStream.TestResult.SUCCESS;
        };

        outputStream.sendTestResult(testIdentifier.getDisplayName(), result);
    }
}