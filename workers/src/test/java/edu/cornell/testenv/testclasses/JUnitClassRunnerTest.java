package edu.cornell.testenv.testclasses;

import edu.cornell.testenv.testcontext.JUnitTestContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JUnitClassRunnerTest {

    @Test
    public void testSuccess() {
        new JUnitTestContext(List.of(""));
        assertTrue(1 + 1 == 2);
    }

    @Test
    public void testFailure() {
        assertTrue(1 + 1 == 3);
    }


}
