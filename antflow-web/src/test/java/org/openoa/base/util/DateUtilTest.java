package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest extends BaseTest {

    @Nested
    @DisplayName("getDayStart")
    class GetDayStartTest {
        @Test
        @DisplayName("should set time to start of day")
        void shouldSetTimeToStartOfDay() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.MARCH, 15, 10, 30, 45);
            cal.set(Calendar.MILLISECOND, 500);
            Date input = cal.getTime();

            Date result = DateUtil.getDayStart(input);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(0, resultCal.get(Calendar.HOUR_OF_DAY));
            assertEquals(0, resultCal.get(Calendar.MINUTE));
            assertEquals(0, resultCal.get(Calendar.SECOND));
            assertEquals(0, resultCal.get(Calendar.MILLISECOND));
            assertEquals(2024, resultCal.get(Calendar.YEAR));
            assertEquals(Calendar.MARCH, resultCal.get(Calendar.MONTH));
            assertEquals(15, resultCal.get(Calendar.DAY_OF_MONTH));
        }
    }

    @Nested
    @DisplayName("getDayEnd")
    class GetDayEndTest {
        @Test
        @DisplayName("should set time to end of day")
        void shouldSetTimeToEndOfDay() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.MARCH, 15, 10, 30, 45);
            Date input = cal.getTime();

            Date result = DateUtil.getDayEnd(input);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(23, resultCal.get(Calendar.HOUR_OF_DAY));
            assertEquals(59, resultCal.get(Calendar.MINUTE));
            assertEquals(59, resultCal.get(Calendar.SECOND));
            assertEquals(999, resultCal.get(Calendar.MILLISECOND));
        }
    }

    @Nested
    @DisplayName("getYearStart")
    class GetYearStartTest {
        @Test
        @DisplayName("should return start of year")
        void shouldReturnStartOfYear() {
            Date result = DateUtil.getYearStart(2024);

            Calendar cal = Calendar.getInstance();
            cal.setTime(result);
            assertEquals(2024, cal.get(Calendar.YEAR));
            assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
            assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
            assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        }
    }

    @Nested
    @DisplayName("getYearEnd")
    class GetYearEndTest {
        @Test
        @DisplayName("should return end of year")
        void shouldReturnEndOfYear() {
            Date result = DateUtil.getYearEnd(2024);

            Calendar cal = Calendar.getInstance();
            cal.setTime(result);
            assertEquals(2024, cal.get(Calendar.YEAR));
            assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
            assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
            assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
        }
    }

    @Nested
    @DisplayName("getMonthStart")
    class GetMonthStartTest {
        @Test
        @DisplayName("should return start of month")
        void shouldReturnStartOfMonth() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.FEBRUARY, 15);
            Date input = cal.getTime();

            Date result = DateUtil.getMonthStart(input);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(Calendar.FEBRUARY, resultCal.get(Calendar.MONTH));
            assertEquals(1, resultCal.get(Calendar.DAY_OF_MONTH));
            assertEquals(0, resultCal.get(Calendar.HOUR_OF_DAY));
        }
    }

    @Nested
    @DisplayName("getMonthEnd")
    class GetMonthEndTest {
        @Test
        @DisplayName("should return end of February in leap year")
        void shouldReturnEndOfFebruaryLeapYear() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.FEBRUARY, 15);
            Date input = cal.getTime();

            Date result = DateUtil.getMonthEnd(input);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(29, resultCal.get(Calendar.DAY_OF_MONTH));
        }

        @Test
        @DisplayName("should return end of February in non-leap year")
        void shouldReturnEndOfFebruaryNonLeapYear() {
            Calendar cal = Calendar.getInstance();
            cal.set(2023, Calendar.FEBRUARY, 15);
            Date input = cal.getTime();

            Date result = DateUtil.getMonthEnd(input);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(28, resultCal.get(Calendar.DAY_OF_MONTH));
        }
    }

    @Nested
    @DisplayName("getLastMonth")
    class GetLastMonthTest {
        @Test
        @DisplayName("should return previous month in yyyy-MM format")
        void shouldReturnPreviousMonth() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.MARCH, 15);
            Date input = cal.getTime();

            String result = DateUtil.getLastMonth(input);

            assertEquals("2024-02", result);
        }

        @Test
        @DisplayName("should handle January rolling back to previous year")
        void shouldHandleJanuaryRollback() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.JANUARY, 15);
            Date input = cal.getTime();

            String result = DateUtil.getLastMonth(input);

            assertEquals("2023-12", result);
        }
    }

    @Nested
    @DisplayName("getDayOfWeek")
    class GetDayOfWeekTest {
        @Test
        @DisplayName("should return day of week as integer (0=Sunday)")
        void shouldReturnDayOfWeekAsInteger() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.JANUARY, 1);
            Date input = cal.getTime();

            int result = DateUtil.getDayOfWeek(input);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("should return string representation of day")
        void shouldReturnStringRepresentation() {
            assertEquals("SUN", DateUtil.getDayOfWeek(0));
            assertEquals("MON", DateUtil.getDayOfWeek(1));
            assertEquals("TUE", DateUtil.getDayOfWeek(2));
            assertEquals("WED", DateUtil.getDayOfWeek(3));
            assertEquals("THU", DateUtil.getDayOfWeek(4));
            assertEquals("FRI", DateUtil.getDayOfWeek(5));
            assertEquals("SAT", DateUtil.getDayOfWeek(6));
        }

        @Test
        @DisplayName("should return empty string for invalid day")
        void shouldReturnEmptyForInvalidDay() {
            assertEquals("", DateUtil.getDayOfWeek(7));
            assertEquals("", DateUtil.getDayOfWeek(-1));
        }
    }

    @Nested
    @DisplayName("addDay")
    class AddDayTest {
        @Test
        @DisplayName("should add days to date")
        void shouldAddDays() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.MARCH, 15);
            Date input = cal.getTime();

            Date result = DateUtil.addDay(input, 5);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(20, resultCal.get(Calendar.DAY_OF_MONTH));
        }

        @Test
        @DisplayName("should subtract days with negative value")
        void shouldSubtractDays() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.MARCH, 15);
            Date input = cal.getTime();

            Date result = DateUtil.addDay(input, -5);

            Calendar resultCal = Calendar.getInstance();
            resultCal.setTime(result);
            assertEquals(10, resultCal.get(Calendar.DAY_OF_MONTH));
        }
    }

    @Nested
    @DisplayName("dateDiff")
    class DateDiffTest {
        @Test
        @DisplayName("should calculate difference in human readable format")
        void shouldCalculateDifference() {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(2024, Calendar.MARCH, 15, 10, 0, 0);
            Calendar cal2 = Calendar.getInstance();
            cal2.set(2024, Calendar.MARCH, 16, 12, 30, 45);

            String result = DateUtil.dateDiff(cal1.getTime(), cal2.getTime());

            assertTrue(result.contains("1天"));
            assertTrue(result.contains("2小时"));
            assertTrue(result.contains("30分"));
            assertTrue(result.contains("45秒"));
        }

        @Test
        @DisplayName("should calculate difference by mark")
        void shouldCalculateDifferenceByMark() {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(2024, Calendar.MARCH, 15, 0, 0, 0);
            Calendar cal2 = Calendar.getInstance();
            cal2.set(2024, Calendar.MARCH, 17, 0, 0, 0);

            assertEquals(Long.valueOf(2), DateUtil.dateDiff(cal1.getTime(), cal2.getTime(), 1));
            assertEquals(Long.valueOf(0), DateUtil.dateDiff(cal1.getTime(), cal2.getTime(), 2));
        }
    }

    @Nested
    @DisplayName("dateDiffDay")
    class DateDiffDayTest {
        @Test
        @DisplayName("should calculate day difference")
        void shouldCalculateDayDifference() {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(2024, Calendar.MARCH, 15, 0, 0, 0);
            Calendar cal2 = Calendar.getInstance();
            cal2.set(2024, Calendar.MARCH, 20, 0, 0, 0);

            Long result = DateUtil.dateDiffDay(cal1.getTime(), cal2.getTime());

            assertEquals(5L, result);
        }
    }

    @Nested
    @DisplayName("getSeason")
    class GetSeasonTest {
        @Test
        @DisplayName("Q1 months should return 1")
        void q1ShouldReturn1() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.JANUARY, 15);
            assertEquals(1, DateUtil.getSeason(cal.getTime()));

            cal.set(2024, Calendar.MARCH, 15);
            assertEquals(1, DateUtil.getSeason(cal.getTime()));
        }

        @Test
        @DisplayName("Q2 months should return 2")
        void q2ShouldReturn2() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.APRIL, 15);
            assertEquals(2, DateUtil.getSeason(cal.getTime()));

            cal.set(2024, Calendar.JUNE, 15);
            assertEquals(2, DateUtil.getSeason(cal.getTime()));
        }

        @Test
        @DisplayName("Q3 months should return 3")
        void q3ShouldReturn3() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.JULY, 15);
            assertEquals(3, DateUtil.getSeason(cal.getTime()));

            cal.set(2024, Calendar.SEPTEMBER, 15);
            assertEquals(3, DateUtil.getSeason(cal.getTime()));
        }

        @Test
        @DisplayName("Q4 months should return 4")
        void q4ShouldReturn4() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.OCTOBER, 15);
            assertEquals(4, DateUtil.getSeason(cal.getTime()));

            cal.set(2024, Calendar.DECEMBER, 15);
            assertEquals(4, DateUtil.getSeason(cal.getTime()));
        }
    }

    @Nested
    @DisplayName("dayForWeek")
    class DayForWeekTest {
        @Test
        @DisplayName("should return Chinese day of week")
        void shouldReturnChineseDayOfWeek() {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, Calendar.JANUARY, 1);
            assertEquals("周一", DateUtil.dayForWeek(cal.getTime()));
        }
    }

    @Nested
    @DisplayName("parseStandard")
    class ParseStandardTest {
        @Test
        @DisplayName("should parse standard datetime string")
        void shouldParseStandardDatetime() {
            Date result = DateUtil.parseStandard("2024-03-15 10:30:00");

            Calendar cal = Calendar.getInstance();
            cal.setTime(result);
            assertEquals(2024, cal.get(Calendar.YEAR));
            assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
            assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
            assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
            assertEquals(30, cal.get(Calendar.MINUTE));
        }
    }
}
