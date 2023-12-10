package edu.cornell.testenv.testclasses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClassJUnit {

    @Test
    public void testSuccess() {
        assertTrue(1 + 1 == 2);
    }

    @Test
    public void testFailure() {
        assertTrue(1 + 1 == 3);
    }


}
