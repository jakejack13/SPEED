package edu.cornell.testconsumer;

import lombok.NonNull;
import java.io.PrintStream;
import java.util.Set;

/**
 * A test consumer that simply prints the results of the tests.
 */
class PrintTestConsumer extends TestConsumer {

    /**
     * The output stream.
     */
    private final PrintStream out = System.out;

    /**
     * Creates a new PrintTestConsumer for the specified workers.
     *
     * @param workerIds the set of all worker ids
     */
    public PrintTestConsumer(@NonNull Set<String> workerIds) {
        super(workerIds);
    }

    @Override
    protected void displayTestResult(String testName, TestResult testResult) {
        out.println(testName + ":" + testResult);
    }
}
