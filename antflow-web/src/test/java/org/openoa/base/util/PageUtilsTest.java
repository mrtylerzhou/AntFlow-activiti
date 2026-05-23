package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageUtilsTest extends BaseTest {

    @Nested
    @DisplayName("getPageCount")
    class GetPageCountTest {
        @Test
        @DisplayName("should return 1 when total equals size")
        void shouldReturn1WhenEqual() {
            assertEquals(1, PageUtils.getPageCount(10, 10));
        }

        @Test
        @DisplayName("should return 2 when total is 11 and size is 10")
        void shouldReturn2ForRemainder() {
            assertEquals(2, PageUtils.getPageCount(11, 10));
        }

        @Test
        @DisplayName("should return 0 when total is 0")
        void shouldReturn0ForZeroTotal() {
            assertEquals(0, PageUtils.getPageCount(0, 10));
        }

        @Test
        @DisplayName("should return 5 for 50 total and 10 size")
        void shouldReturn5ForExactDivision() {
            assertEquals(5, PageUtils.getPageCount(50, 10));
        }

        @Test
        @DisplayName("should return 3 for 21 total and 10 size")
        void shouldReturn3ForPartial() {
            assertEquals(3, PageUtils.getPageCount(21, 10));
        }
    }

    @Nested
    @DisplayName("getPageList")
    class GetPageListTest {
        @Test
        @DisplayName("should return first page")
        void shouldReturnFirstPage() {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> result = PageUtils.getPageList(1, 2, 3, list);
            assertEquals(Arrays.asList(1, 2), result);
        }

        @Test
        @DisplayName("should return middle page")
        void shouldReturnMiddlePage() {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> result = PageUtils.getPageList(2, 2, 3, list);
            assertEquals(Arrays.asList(3, 4), result);
        }

        @Test
        @DisplayName("should return last page with remaining elements")
        void shouldReturnLastPage() {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> result = PageUtils.getPageList(3, 2, 3, list);
            assertEquals(Arrays.asList(5), result);
        }

        @Test
        @DisplayName("should throw when page exceeds total")
        void shouldThrowWhenPageExceedsTotal() {
            List<Integer> list = Arrays.asList(1, 2, 3);
            assertThrows(AFBizException.class, () -> PageUtils.getPageList(5, 2, 3, list));
        }

        @Test
        @DisplayName("should return full page when not last page")
        void shouldReturnFullPage() {
            List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f");
            List<String> result = PageUtils.getPageList(1, 3, 2, list);
            assertEquals(Arrays.asList("a", "b", "c"), result);
        }
    }
}
