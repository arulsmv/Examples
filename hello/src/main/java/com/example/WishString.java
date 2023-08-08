package com.example;

public class WishString {
    public static String getWish() {
       int hourOfDay = new HourOfDay().getHour(); 
       if (hourOfDay > 4 && hourOfDay < 12) 
          return "GoodMorning";
       else if (hourOfDay >= 12 && hourOfDay < 16)
          return "GoodAfterNoon";
       else if (hourOfDay >= 16 && hourOfDay < 22)
          return "GoodEvening";
       return "You better Sleep";
    }
}
