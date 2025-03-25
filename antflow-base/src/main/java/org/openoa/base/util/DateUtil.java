package org.openoa.base.util;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public abstract class DateUtil {

    private static Date maxDate;

    private static Date minDate;

    private static final String YEAR_PATTERN = "yyyy";
    public static final FastDateFormat SDF_YEAR_PATTERN = FastDateFormat.getInstance(YEAR_PATTERN);

    private static final String MONTH_PATTERN = "yyyy-MM";
    public static final FastDateFormat SDF_MONTH_PATTERN = FastDateFormat.getInstance(MONTH_PATTERN);

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final FastDateFormat SDF_DATE_PATTERN = FastDateFormat.getInstance(DATE_PATTERN);

    private static final String DATETIME_PATTERN_NO_SECOND = "yyyy-MM-dd HH:mm";
    public static final FastDateFormat SDF_DATETIME_PATTERN_NO_SECOND = FastDateFormat
            .getInstance(DATETIME_PATTERN_NO_SECOND);

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final FastDateFormat SDF_DATETIME_PATTERN = FastDateFormat.getInstance(DATETIME_PATTERN);

    private static final String DATETIME_PATTERN_WITH_DOT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final FastDateFormat SDF_DATETIME_PATTERN_WITH_DOT_FULL = FastDateFormat
            .getInstance(DATETIME_PATTERN_WITH_DOT_FULL);

    private static final String DATETIME_T_PATTERN_NO_SECOND = "yyyy-MM-dd'T'HH:mm";
    public static final FastDateFormat SDF_T_DATETIME_PATTERN_NO_SECOND = FastDateFormat
            .getInstance(DATETIME_T_PATTERN_NO_SECOND);

    private static final String DATETIME_T_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final FastDateFormat SDF_T_DATETIME_PATTERN = FastDateFormat.getInstance(DATETIME_T_PATTERN);

    private static final String DATETIME_T_PATTERN_WITH_DOT_FULL = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final FastDateFormat SDF_T_DATETIME_PATTERN_WITH_DOT_FULL = FastDateFormat
            .getInstance(DATETIME_T_PATTERN_WITH_DOT_FULL);

    private static final String DATETIME_PATTERN_WITH_COMPRESS = "yyyyMMddHHmmss";
    public static final FastDateFormat SDF_DATETIME_PATTERN_COMPRESS = FastDateFormat
            .getInstance(DATETIME_PATTERN_WITH_COMPRESS);

    private static final String DATETIME_PATTERN_DATE_WITH_COMPRESS = "yyyyMMdd";
    public static final FastDateFormat SDF_DATETIME_PATTERN_DATE_COMPRESS = FastDateFormat
            .getInstance(DATETIME_PATTERN_DATE_WITH_COMPRESS);

    private static final String DATETIME_PATTERN_TIME_WITH_COMPRESS = "HHmm";
    public static final FastDateFormat SDF_DATETIME_PATTERN_TIME_COMPRESS = FastDateFormat
            .getInstance(DATETIME_PATTERN_TIME_WITH_COMPRESS);

    private static final String DATETIME_PATTERN_TIME_WITH_SECOND = "HH:mm:ss";
    public static final FastDateFormat SDF_DATETIME_PATTERN_TIME_SECOND = FastDateFormat
            .getInstance(DATETIME_PATTERN_TIME_WITH_SECOND);

    private static final String DATETIME_PATTERN_TIME_WITH_NOT_SECOND = "MM-dd";
    public static final FastDateFormat SDF_DATETIME_PATTERN_TIME_NOT_SECOND = FastDateFormat
            .getInstance(DATETIME_PATTERN_TIME_WITH_NOT_SECOND);

    private static final String DATETIME_PATTERN_TIME_WITH_MIN_NOT_SECOND = "HH:mm";
    public static final FastDateFormat SDF_DATETIME_PATTERN_TIME_MIN_NOT_SECOND = FastDateFormat
            .getInstance(DATETIME_PATTERN_TIME_WITH_MIN_NOT_SECOND);

    private static final String DATETIME_PATTERN_CHINA = "yyyy年MM月dd日";
    public static final FastDateFormat SDF_DATETIME_PATTERN_CHINA = FastDateFormat
            .getInstance(DATETIME_PATTERN_CHINA);

    private static final String MONTH_PATTERN_CHINA = "yyyy年MM月";
    public static final FastDateFormat SDF_MONTH_PATTERN_CHINA = FastDateFormat
            .getInstance(MONTH_PATTERN_CHINA);


    private static final String DATETIME_PATTERN_HOUR = "HH";
    public static final FastDateFormat SDF_DATETIME_PATTERN_HOUR = FastDateFormat
            .getInstance(DATETIME_PATTERN_HOUR);

    private static final String DATETIME_PATTERN_ISO8601 = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final FastDateFormat SDF_DATETIME_PATTERN_ISO8601 = FastDateFormat
            .getInstance(DATETIME_PATTERN_ISO8601);

    /**
     * get start date of a specified day
     *
     * @param oDate
     * @return
     */
    public static Date getDayStart(Date oDate) {
        DateTime jdt = new DateTime(oDate);
        jdt.withTime(0, 0, 0, 0);
        return jdt.toDate();
    }

    /**
     * a start date of a specified year
     *
     * @param year
     * @return
     */
    public static Date getYearStart(Integer year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_YEAR, 1);
        return getDayStart(c.getTime());
    }

    /**
     * get the end time of a specified day
     *
     * @param oDate
     * @return
     */
    public static Date getDayEnd(Date oDate) {
        DateTime jdt = new DateTime(oDate);
        jdt.withTime(23, 59, 59, 999);
        return jdt.toDate();
    }

    /**
     * get the end time of a specified year
     *
     * @param year
     * @return
     */
    public static Date getYearEnd(Integer year) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.roll(Calendar.DAY_OF_YEAR, -1);
        return getDayEnd(c.getTime());
    }

    /**
     * get the start time of a specified month
     *
     * @param oDate
     * @return
     */
    public static Date getMonthStart(Date oDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(oDate);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return getDayStart(c.getTime());
    }

    /**
     * get the end time of a specified month
     *
     * @param oDate
     * @return
     */
    public static Date getMonthEnd(Date oDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(oDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getDayEnd(c.getTime());
    }

    /**
     * get last month's year and month info in a format of yyyy-MM
     *
     * @param oDate
     * @return 返
     */
    public static String getLastMonth(Date oDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(oDate);
        c.add(Calendar.MONTH, -1);
        return SDF_MONTH_PATTERN.format(c.getTime());
    }

    /**
     * get day of week
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * get the year of a specified data
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * get the day of week in string format
     *
     * @param day
     * @return
     */
    public static String getDayOfWeek(int day) {

        switch (day) {
            case 0:
                return "SUN";
            case 1:
                return "MON";
            case 2:
                return "TUE";
            case 3:
                return "WED";
            case 4:
                return "THU";
            case 5:
                return "FRI";
            case 6:
                return "SAT";
            default:
                return "";
        }
    }

    /**
     * add second to a specified date
     * @param date
     * @param second
     * @return
     */
    public static Date addSecond(Date date, Integer second) {
        DateTime jDateTime = new DateTime(date);
        jDateTime.withSecondOfMinute(second);
        return jDateTime.toDate();
    }

    /**
     * add minute to a specified date
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutes(Date date, Integer minutes) {
        DateTime jDateTime = new DateTime(date);
        jDateTime.withMinuteOfHour(minutes);
        return jDateTime.toDate();
    }

    /**
     * add hour to a specified date
     * @param date
     * @param hour
     * @return
     */
    public static Date addHours(Date date, Integer hour) {
        DateTime jDateTime = new DateTime(date);
        jDateTime.withHourOfDay(hour);
        return jDateTime.toDate();
    }

    /**
     * add day to a specified date
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date, Integer day) {
        DateTime jDateTime = new DateTime(date);
        jDateTime.withDayOfYear(day);
        return jDateTime.toDate();
    }

    /**
     * add month to a specified date
     * @param date
     * @param month
     * @return
     */
    public static Date addMonth(Date date, Integer month) {
        DateTime jDateTime = new DateTime(date);
        jDateTime.withMonthOfYear(month);
        return jDateTime.toDate();
    }

    /**
     * add year to a specified date
     * @param date
     * @param year
     * @return
     */
    public static Date addYear(Date date, Integer year) {
        DateTime jDateTime = new DateTime(date);
        jDateTime.withYear(year);
        return jDateTime.toDate();
    }

    /**
     * get day difference between two dates in string format
     * @param date1
     * @param date2
     * @return
     */
    public static final String dateDiff(Date date1, Date date2) {
        Long diff = Math.abs(date1.getTime() - date2.getTime());
        Long day = diff / (24 * 60 * 60 * 1000);
        Long hour = (diff / (60 * 60 * 1000) - day * 24);
        Long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        Long sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuffer diffStr = new StringBuffer();
        Boolean show = false;
        if (day > 0) {
            diffStr.append(day);
            diffStr.append("天");
            show = true;
        }
        if (show || hour > 0) {
            diffStr.append(hour);
            diffStr.append("小时");
            show = true;
        }
        if (show || min > 0) {
            diffStr.append(min);
            diffStr.append("分");
        }
        diffStr.append(sec);
        diffStr.append("秒");
        return diffStr.toString();
    }


    /**
     * get the time difference between two dates,1 for day;2 for hour;3 for minute;4 for second
     *
     * @param date1
     * @param date2
     * @param mark
     * @return
     */
    public static final Long dateDiff(Date date1, Date date2, Integer mark) {
        Long diff = Math.abs(date1.getTime() - date2.getTime());
        Long day = diff / (24 * 60 * 60 * 1000);
        Long hour = (diff / (60 * 60 * 1000) - day * 24);
        Long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        Long sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

        switch (mark) {
            case 1:
                return day;
            case 2:
                return hour;
            case 3:
                return min;
            case 4:
                return sec;
            default:
                return null;
        }
    }


    public static final Long dateDiffDay(Date date1, Date date2) {
        Long diff = Math.abs(date1.getTime() - date2.getTime());
        Long day = diff / (24 * 60 * 60 * 1000);
        return day;
    }

    public static Date parseStandard(String dt){
        try {
            Date parsedDt = SDF_DATETIME_PATTERN.parse(dt);
            return parsedDt;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * get day of week in Chinese format
     *
     * @param date
     * @return
     */
    public static String dayForWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }


    /**
     * get season of a specified date
     *
     * @param date
     * @return
     */
    public static Integer getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

}
