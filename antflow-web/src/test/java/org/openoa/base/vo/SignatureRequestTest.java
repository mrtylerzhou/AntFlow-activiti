package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class SignatureRequestTest extends BaseTest {

    @Nested
    @DisplayName("no-arg constructor")
    class NoArgConstructorTest {
        @Test
        @DisplayName("should create with null fields")
        void nullFields() {
            SignatureRequest req = new SignatureRequest();
            assertNull(req.getSystemType());
            assertNull(req.getAppVersion());
            assertNull(req.getHardware());
            assertNull(req.getSystemVersion());
        }
    }

    @Nested
    @DisplayName("all-arg constructor")
    class AllArgConstructorTest {
        @Test
        @DisplayName("should set all fields")
        void allFields() {
            SignatureRequest req = new SignatureRequest("iOS", "2.0", "iPhone15", "17.0");
            assertEquals("iOS", req.getSystemType());
            assertEquals("2.0", req.getAppVersion());
            assertEquals("iPhone15", req.getHardware());
            assertEquals("17.0", req.getSystemVersion());
        }
    }

    @Nested
    @DisplayName("builder")
    class BuilderTest {
        @Test
        @DisplayName("should build with all fields")
        void buildWithAllFields() {
            SignatureRequest req = SignatureRequest.builder()
                    .systemType("Android")
                    .appVersion("3.0")
                    .hardware("Pixel8")
                    .systemVersion("14")
                    .build();
            assertEquals("Android", req.getSystemType());
            assertEquals("3.0", req.getAppVersion());
            assertEquals("Pixel8", req.getHardware());
            assertEquals("14", req.getSystemVersion());
        }
    }

    @Nested
    @DisplayName("setters")
    class SettersTest {
        @Test
        @DisplayName("should set systemType to PC value '1'")
        void pcSystemType() {
            SignatureRequest req = new SignatureRequest();
            req.setSystemType("1");
            assertEquals("1", req.getSystemType());
        }
    }
}
