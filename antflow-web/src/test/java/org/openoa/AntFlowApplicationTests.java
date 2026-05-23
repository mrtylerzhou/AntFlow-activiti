package org.openoa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AntFlow Application Tests")
class AntFlowApplicationTests {
    @Test
    @DisplayName("sanity check - test infrastructure should work")
    void sanityCheck() {
        assertTrue(true, "Test infrastructure is working");
    }
}
