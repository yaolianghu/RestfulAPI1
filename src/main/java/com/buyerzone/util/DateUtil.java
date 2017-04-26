package com.buyerzone.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by isantiago on 6/1/16.
 */
public class DateUtil {
    public static final String DATE_TIME = "yyyy-MM-dd' 'HH:mm:ss";
    public static final String DATE = "yyyy-MM-dd";
    public static final String TIME = "HH:mm:ss";

    public static int getCurrentDayOfWeek() {
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(GregorianCalendar.DAY_OF_WEEK);
    }

    private static String getNewDateTime(Date date, int field, int amount, String format){
        GregorianCalendar currentDate = (GregorianCalendar)Calendar.getInstance();
        currentDate.setTime(date);
        currentDate.add(field, amount);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(currentDate.getTime());
    }
    public static String getFormattedDate(Date date, String format){
        GregorianCalendar currentDate = (GregorianCalendar)Calendar.getInstance();
        currentDate.setTime(date);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(currentDate.getTime());
    }

    public static String getStartOfMonth(Date date, String format){
        GregorianCalendar newDate = (GregorianCalendar)Calendar.getInstance();
        newDate.setTime(date);
        setStartOfMonth(newDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(newDate.getTime());
    }
    public static String getStartOfDay(Date date, String format){
        GregorianCalendar newDate = (GregorianCalendar)Calendar.getInstance();
        newDate.setTime(date);
        setStartOfDay(newDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(newDate.getTime());
    }
    public static String getEndOfDay(Date date, String format){
        GregorianCalendar newDate = (GregorianCalendar)Calendar.getInstance();
        newDate.setTime(date);
        setEndOfDay(newDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(newDate.getTime());
    }
    public static String getStartOfWeek(Date date, String format){
        GregorianCalendar newDate = (GregorianCalendar)Calendar.getInstance();
        newDate.setTime(date);
        setStartOfWeek(newDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(newDate.getTime());
    }
    private static void setStartOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    private static void setEndOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
    private static void setStartOfMonth(Calendar calendar){
        setStartOfDay(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
    }
    private static void setStartOfWeek(Calendar calendar){
        setStartOfDay(calendar);
        calendar.set(Calendar.DAY_OF_WEEK, 0);
    }

}
