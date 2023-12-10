package edu.cornell.testenv.testclasses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestClassJUnit {

    @Test
    void testSuccess() {
        assertTrue(1 + 1 == 2);
    }

    @Test
    void testFailure() {
        assertTrue(1 + 1 == 3);
    }


}
