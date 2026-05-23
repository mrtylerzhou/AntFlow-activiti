package org.openoa.engine.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashingAlgTest extends BaseTest {

    @Nested
    @DisplayName("getHash")
    class GetHashTest {
        @Test
        @DisplayName("should return non-negative hash value")
        void shouldReturnNonNegativeHash() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("server", 3);

            int hash = alg.getHash("testKey");

            assertTrue(hash >= 0);
        }

        @Test
        @DisplayName("should return consistent hash for same input")
        void shouldReturnConsistentHash() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("server", 3);

            int hash1 = alg.getHash("testKey");
            int hash2 = alg.getHash("testKey");

            assertEquals(hash1, hash2);
        }

        @Test
        @DisplayName("should return different hash for different inputs")
        void shouldReturnDifferentHashForDifferentInputs() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("server", 3);

            int hash1 = alg.getHash("key1");
            int hash2 = alg.getHash("key2");

            assertNotEquals(hash1, hash2);
        }
    }

    @Nested
    @DisplayName("getServer")
    class GetServerTest {
        @Test
        @DisplayName("should route to a valid server")
        void shouldRouteToValidServer() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("server", 3);

            String server = alg.getServer("someKey");

            assertNotNull(server);
            assertTrue(server.startsWith("server_"));
        }

        @Test
        @DisplayName("should consistently route same key to same server")
        void shouldConsistentlyRouteSameKey() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("node", 5);

            String server1 = alg.getServer("consistentKey");
            String server2 = alg.getServer("consistentKey");

            assertEquals(server1, server2);
        }

        @Test
        @DisplayName("should distribute keys across servers")
        void shouldDistributeKeysAcrossServers() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("node", 3);
            java.util.Set<String> servers = new java.util.HashSet<>();

            for (int i = 0; i < 100; i++) {
                servers.add(alg.getServer("key_" + i));
            }

            assertTrue(servers.size() > 1, "Keys should be distributed across multiple servers");
        }

        @Test
        @DisplayName("should handle single server")
        void shouldHandleSingleServer() {
            ConsistentHashingAlg alg = new ConsistentHashingAlg("single", 1);

            String server = alg.getServer("anyKey");

            assertEquals("single_0", server);
        }
    }
}
