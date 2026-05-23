package org.openoa.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class IndexControllerTest extends BaseTest {

    @Nested
    @DisplayName("IndexController basic tests")
    class BasicTest {
        @Test
        @DisplayName("should verify IndexController class exists and is accessible")
        void shouldVerifyClassExists() {
            assertDoesNotThrow(() -> Class.forName("org.openoa.controller.IndexController"));
        }
    }
}
