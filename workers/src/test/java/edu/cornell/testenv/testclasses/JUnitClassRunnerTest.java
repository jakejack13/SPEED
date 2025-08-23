package edu.cornell.testenv.testclasses;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
        assertTrue(true);
    }

    /**
     * Tests a failure.
     */
    @Test
    public void testFailure() {
        fail();
    }


}
