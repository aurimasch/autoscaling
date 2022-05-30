package com.autoscaling.autoscaler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class DateUtils {

    public static final TimeZone HELSINKI_TIME_ZONE = TimeZone.getTimeZone("Europe/Helsinki");

    public static Timestamp now() {
        return new Timestamp(new java.util.Date().getTime());
    }

    public static Timestamp fromNow(int calendarField, int amount) {
        Calendar cal = Calendar.getInstance(HELSINKI_TIME_ZONE);
        cal.add(calendarField, amount);
        return new Timestamp(cal.getTime().getTime());
    }

    public static Timestamp fromDate(Timestamp date, int calendarField, int amount) {
        Calendar cal = Calendar.getInstance(HELSINKI_TIME_ZONE);
        cal.setTime(date);
        cal.add(calendarField, amount);
        return new Timestamp(cal.getTime().getTime());
    }

}
