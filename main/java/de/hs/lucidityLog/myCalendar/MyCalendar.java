package de.hs.lucidityLog.myCalendar;

import java.util.Calendar;

public class MyCalendar {
    private static Calendar calendar;

    public static Calendar getCalendar() {
        if(calendar == null) return Calendar.getInstance();
        return calendar;
    }

    public static void setCalendar(Calendar newCalendar) {
        calendar = newCalendar;
    }
}
