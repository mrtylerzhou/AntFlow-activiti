package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class RetryerTest extends BaseTest {

    private static void setAttempt(Retryer.Default retryer, int attempt) throws Exception {
        Field field = Retryer.Default.class.getDeclaredField("attempt");
        field.setAccessible(true);
        field.setInt(retryer, attempt);
    }

    @Nested
    @DisplayName("Default.continueOrPropagate")
    class ContinueOrPropagateTest {
        @Test
        @DisplayName("should not throw before max attempts")
        void shouldNotThrowBeforeMaxAttempts() {
            Retryer.Default retryer = new Retryer.Default(1, 3);
            assertDoesNotThrow(() -> retryer.continueOrPropagate(new RuntimeException("test")));
        }

        @Test
        @DisplayName("should throw after max attempts reached")
        void shouldThrowAfterMaxAttempts() {
            Retryer.Default retryer = new Retryer.Default(1, 2);
            retryer.continueOrPropagate(new RuntimeException("first"));
            assertThrows(RuntimeException.class,
                    () -> retryer.continueOrPropagate(new RuntimeException("second")));
        }

        @Test
        @DisplayName("should throw the original exception")
        void shouldThrowOriginalException() {
            Retryer.Default retryer = new Retryer.Default(1, 1);
            RuntimeException expected = new RuntimeException("original");
            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> retryer.continueOrPropagate(expected));
            assertEquals("original", thrown.getMessage());
        }

        @Test
        @DisplayName("should allow maxAttempts=1 to throw immediately")
        void shouldThrowImmediatelyForMaxAttempts1() {
            Retryer.Default retryer = new Retryer.Default(1, 1);
            assertThrows(RuntimeException.class,
                    () -> retryer.continueOrPropagate(new RuntimeException("immediate")));
        }
    }

    @Nested
    @DisplayName("Default.nextMaxInterval")
    class NextMaxIntervalTest {
        @Test
        @DisplayName("should return period for first attempt")
        void shouldReturnPeriodForFirstAttempt() throws Exception {
            Retryer.Default retryer = new Retryer.Default(1000, 10000, 100);
            setAttempt(retryer, 1);
            assertEquals(1000, retryer.nextMaxInterval());
        }

        @Test
        @DisplayName("should increase with exponential backoff")
        void shouldIncreaseWithBackoff() throws Exception {
            Retryer.Default retryer = new Retryer.Default(1000, 10000, 100);
            setAttempt(retryer, 1);
            long first = retryer.nextMaxInterval();
            setAttempt(retryer, 2);
            long second = retryer.nextMaxInterval();
            setAttempt(retryer, 3);
            long third = retryer.nextMaxInterval();
            assertTrue(second > first);
            assertTrue(third > second);
        }

        @Test
        @DisplayName("should not exceed maxPeriod")
        void shouldNotExceedMaxPeriod() throws Exception {
            Retryer.Default retryer = new Retryer.Default(1000, 5000, 100);
            setAttempt(retryer, 50);
            long interval = retryer.nextMaxInterval();
            assertTrue(interval <= 5000);
        }

        @Test
        @DisplayName("should cap at maxPeriod for large attempts")
        void shouldCapAtMaxPeriod() throws Exception {
            Retryer.Default retryer = new Retryer.Default(1000, 2000, 100);
            setAttempt(retryer, 100);
            long interval = retryer.nextMaxInterval();
            assertEquals(2000, interval);
        }

        @Test
        @DisplayName("should calculate 1.5x backoff correctly")
        void shouldCalculateBackoff() throws Exception {
            Retryer.Default retryer = new Retryer.Default(1000, 100000, 100);
            setAttempt(retryer, 1);
            assertEquals(1000, retryer.nextMaxInterval());
            setAttempt(retryer, 2);
            assertEquals((long) (1000 * Math.pow(1.5, 1)), retryer.nextMaxInterval());
            setAttempt(retryer, 3);
            assertEquals((long) (1000 * Math.pow(1.5, 2)), retryer.nextMaxInterval());
        }
    }

    @Nested
    @DisplayName("Default constructors")
    class DefaultConstructorTest {
        @Test
        @DisplayName("default constructor should use 1s period and 3 max attempts")
        void defaultConstructor() {
            Retryer.Default retryer = new Retryer.Default();
            assertDoesNotThrow(() -> retryer.continueOrPropagate(new RuntimeException("test")));
            assertDoesNotThrow(() -> retryer.continueOrPropagate(new RuntimeException("test")));
            assertThrows(RuntimeException.class,
                    () -> retryer.continueOrPropagate(new RuntimeException("test")));
        }

        @Test
        @DisplayName("two-arg constructor should set period and maxAttempts")
        void twoArgConstructor() {
            Retryer.Default retryer = new Retryer.Default(1, 5);
            for (int i = 0; i < 4; i++) {
                retryer.continueOrPropagate(new RuntimeException("test"));
            }
            assertThrows(RuntimeException.class,
                    () -> retryer.continueOrPropagate(new RuntimeException("final")));
        }
    }
}
