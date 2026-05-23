package org.openoa.base.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class PageDtoTest extends BaseTest {

    @Nested
    @DisplayName("getStartIndex")
    class GetStartIndexTest {
        @Test
        @DisplayName("should return 0 for page 1 and pageSize 10")
        void shouldReturn0ForPage1() {
            PageDto dto = new PageDto();
            dto.setPage(1);
            dto.setPageSize(10);
            assertEquals(0, dto.getStartIndex());
        }

        @Test
        @DisplayName("should return 10 for page 2 and pageSize 10")
        void shouldReturn10ForPage2() {
            PageDto dto = new PageDto();
            dto.setPage(2);
            dto.setPageSize(10);
            assertEquals(10, dto.getStartIndex());
        }

        @Test
        @DisplayName("should return 50 for page 3 and pageSize 25")
        void shouldReturn50ForPage3Size25() {
            PageDto dto = new PageDto();
            dto.setPage(3);
            dto.setPageSize(25);
            assertEquals(50, dto.getStartIndex());
        }
    }

    @Nested
    @DisplayName("first")
    class FirstTest {
        @Test
        @DisplayName("should create PageDto with page=1 and pageSize=10")
        void shouldCreateFirstPage() {
            PageDto dto = PageDto.first();
            assertEquals(1, dto.getPage());
            assertEquals(10, dto.getPageSize());
        }
    }

    @Nested
    @DisplayName("buildCountedPage")
    class BuildCountedPageTest {
        @Test
        @DisplayName("should set totalCount and calculate pageCount")
        void shouldSetTotalAndPageCount() {
            PageDto original = new PageDto();
            original.setPage(1);
            original.setPageSize(10);
            PageDto result = PageDto.buildCountedPage(original, 55);
            assertEquals(55, result.getTotalCount());
            assertEquals(6, result.getPageCount());
        }

        @Test
        @DisplayName("should calculate pageCount=1 when total equals pageSize")
        void shouldCalculate1WhenEqual() {
            PageDto original = new PageDto();
            original.setPage(1);
            original.setPageSize(10);
            PageDto result = PageDto.buildCountedPage(original, 10);
            assertEquals(1, result.getPageCount());
        }

        @Test
        @DisplayName("should calculate pageCount=1 when total is 1")
        void shouldCalculate1ForSingleRecord() {
            PageDto original = new PageDto();
            original.setPage(1);
            original.setPageSize(10);
            PageDto result = PageDto.buildCountedPage(original, 1);
            assertEquals(1, result.getPageCount());
        }

        @Test
        @DisplayName("should preserve page and pageSize from original")
        void shouldPreservePageAndSize() {
            PageDto original = new PageDto();
            original.setPage(3);
            original.setPageSize(20);
            PageDto result = PageDto.buildCountedPage(original, 100);
            assertEquals(3, result.getPage());
            assertEquals(20, result.getPageSize());
            assertEquals(5, result.getPageCount());
        }
    }

    @Nested
    @DisplayName("constructor")
    class ConstructorTest {
        @Test
        @DisplayName("4-arg constructor should set all fields")
        void fourArgConstructor() {
            PageDto dto = new PageDto(2, 20, "name", 1);
            assertEquals(2, dto.getPage());
            assertEquals(20, dto.getPageSize());
            assertEquals("name", dto.getOrderColumn());
            assertEquals(1, dto.getOrderType());
        }
    }
}
