package org.activiti.engine.impl.calendar;

import org.activiti.engine.runtime.ClockReader;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class implements business calendar based on internal clock
 */
public abstract class BusinessCalendarImpl implements BusinessCalendar {

  protected ClockReader clockReader;

  public BusinessCalendarImpl(ClockReader clockReader) {
    this.clockReader = clockReader;
  }

  @Override
  public Date resolveDuedate(String duedateDescription){
    return resolveDuedate(duedateDescription,-1);
  }

  public abstract Date resolveDuedate(String duedateDescription, int maxIterations);

  @Override
  public Boolean validateDuedate(String duedateDescription, int maxIterations, Date endDate, Date newTimer) {
    return endDate == null || endDate.after(newTimer) || endDate.equals(newTimer);
  }

  @Override
  public Date resolveEndDate(String endDateString) {
    ZonedDateTime zdt = ZonedDateTime.parse(endDateString, DateTimeFormatter.ISO_DATE_TIME
        .withZone(clockReader.getCurrentTimeZone().toZoneId()));
    GregorianCalendar cal = GregorianCalendar.from(zdt);
    return cal.getTime();
  }

}

