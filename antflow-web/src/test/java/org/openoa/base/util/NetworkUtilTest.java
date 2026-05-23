package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class NetworkUtilTest extends BaseTest {

    @Nested
    @DisplayName("isHostConnectable")
    class IsHostConnectableTest {
        @Test
        @DisplayName("should return false for unreachable host")
        void shouldReturnFalseForUnreachable() {
            assertFalse(NetworkUtil.isHostConnectable("192.0.2.1", 9999));
        }

        @Test
        @DisplayName("should return false for null host")
        void shouldReturnFalseForNullHost() {
            assertFalse(NetworkUtil.isHostConnectable(null, 80));
        }

        @Test
        @DisplayName("should return false for invalid port")
        void shouldReturnFalseForInvalidPort() {
            assertFalse(NetworkUtil.isHostConnectable("localhost", -1));
        }
    }

    @Nested
    @DisplayName("getNetworkIPList")
    class GetNetworkIPListTest {
        @Test
        @DisplayName("should return non-null list")
        void shouldReturnNonNullList() {
            assertNotNull(NetworkUtil.getNetworkIPList());
        }

        @Test
        @DisplayName("should return list containing valid IP format")
        void shouldReturnValidIPs() {
            for (String ip : NetworkUtil.getNetworkIPList()) {
                assertTrue(ip.contains("."),
                        "IP should contain dots: " + ip);
            }
        }

        @Test
        @DisplayName("should not contain loopback address")
        void shouldNotContainLoopback() {
            for (String ip : NetworkUtil.getNetworkIPList()) {
                assertNotEquals("127.0.0.1", ip);
            }
        }
    }
}
