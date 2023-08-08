package com.example;
import java.util.Calendar;
import java.util.Date;

public class HourOfDay {
  private Calendar calendar;
  public HourOfDay() {
       this.calendar = Calendar.getInstance();
  }
  public int getHour() {
       calendar.setTime(new Date());
       return calendar.get(Calendar.HOUR_OF_DAY);
  }
}

