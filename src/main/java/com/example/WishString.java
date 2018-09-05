package com.example;
import java.util.Calender;
import java.util.Date;

public class WishString {
    public static String getWish() {
       Calender calender = GregorianCalendar.getInstance();
       calender.setTime(new Date());
       int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
       if (hourOfDay > 4 && hourOfDay < 12) 
          return "GoodMorning";
       else if (hourOfDay >= 12 && hourOfDay < 16)
          return "GoodAfterNoon";
       else if (hourOfDay >= 16 && hourOfDay < 22)
          return "GoodEvening";
       else if (hourOfDay >= 22 ||  hourOfDay <=4 )
          return "You better Sleep";
    }
}
