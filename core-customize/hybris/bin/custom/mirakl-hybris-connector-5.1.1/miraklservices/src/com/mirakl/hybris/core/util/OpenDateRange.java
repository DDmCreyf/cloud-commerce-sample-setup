package com.mirakl.hybris.core.util;

import java.util.Date;

import de.hybris.platform.util.DateRange;

public class OpenDateRange implements DateRange {

  protected final Date start;
  protected final Date end;

  private OpenDateRange(Date start, Date end) {
    this.start = start;
    this.end = end;
  }

  public static OpenDateRange dateRange(Date start, Date end) {
    return new OpenDateRange(start, end);
  }

  @Override
  public boolean encloses(Date paramDate) {
    return paramDate != null && (start == null || paramDate.after(start)) && (end == null || paramDate.before(end));
  }

}
