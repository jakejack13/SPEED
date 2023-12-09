package edu.cornell.testconsumer;

import java.util.Map;
import lombok.NonNull;

/**
 * A test consumer that simply prints the results of the tests
 */
public class PrintTestConsumer extends TestConsumer {

    /**
     * Creates a new TestConsumer with the specified number of test class methods
     *
     * @param testClassMethods a mapping from test class name to number of test methods in the test
     *                         class
     */
    public PrintTestConsumer(@NonNull Map<String, Integer> testClassMethods) {
        super(testClassMethods);
    }

    @Override
    protected void displayTestResult(TestResultTuple result) {
        System.out.println(result);
    }
}
