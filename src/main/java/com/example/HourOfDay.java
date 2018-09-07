package com.example;
import java.util.Calender;
import java.util.Date;

public class HourOfDay {
  private Calender calender;
  public HourOfDay() {
       this.calender = GregorianCalendar.getInstance();
  }
  public int getHour() {
       calender.setTime(new Date());
       return calendar.get(Calendar.HOUR_OF_DAY);
  }
}

