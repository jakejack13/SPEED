package edu.cornell.testenv.testclasses;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple sample JUnit5 test class.
 */
public class JUnitClassRunnerTest {

    /**
     * Tests a success.
     */
    @Test
    public void testSuccess() {
        new JUnitTestContext(List.of(""));
        int i = 1;
        assertTrue(i + 1 == 2);
    }

    /**
     * Tests a failure.
     */
    @Test
    public void testFailure() {
        assertTrue(1 + 1 == 3);
    }


}
