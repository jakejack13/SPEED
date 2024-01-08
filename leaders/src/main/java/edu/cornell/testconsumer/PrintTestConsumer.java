package edu.cornell.testconsumer;

import lombok.NonNull;

import java.util.Set;

/**
 * A test consumer that simply prints the results of the tests
 */
class PrintTestConsumer extends TestConsumer {

    /**
     * Creates a new PrintTestConsumer for the specified workers
     *
     * @param workerIds the set of all worker ids
     */
    public PrintTestConsumer(@NonNull Set<String> workerIds) {
        super(workerIds);
    }

    @Override
    protected void displayTestResult(String testName, TestResult testResult) {
        System.out.println(testName + ":" + testResult);
    }
}
