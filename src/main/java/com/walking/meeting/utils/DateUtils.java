package com.walking.meeting.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

public abstract class DateUtils {
    public static final String FORMAT_DELETE_TIME = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_SEVEN = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT2 = "yyyy-MM-dd 00:00:00";
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_ONE = "yyyy/MM/dd HH:mm";
    public static final String FORMAT_FIVE = "yyyy年MM月dd日 HH:mm";
    public static final String FORMAT_YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_THREE = "yyyy/MM/dd";
    public static final String FORMAT_FOUR = "yyyy年MM月dd日";
    public static final String FORMAT_YYYY_MM = "yyyy-MM";
    public static final String TIME = "HH:mm:ss";

    private DateUtils() {
    }

    public static String getDeleteTime() {
        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
    }

    public static String formatDate(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    public static Date parse(String dateTime, String pattern) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(dateTime, new String[]{pattern});
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parse(String dateTime, String[] patterns) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(dateTime, patterns);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean sameDate(Date d1, Date d2) {
        if (d1 != null && d2 != null) {
            LocalDate localDate1 = ZonedDateTime.ofInstant(d1.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate2 = ZonedDateTime.ofInstant(d2.toInstant(), ZoneId.systemDefault()).toLocalDate();
            return localDate1.isEqual(localDate2);
        } else {
            return false;
        }
    }

    public static Long getTimestamp(long minutes) {
        return LocalDateTime.now().plusMinutes(minutes).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static double getRequiredTime(String startTime,String endTime){
        Date startTimeParse = DateUtils.parse(startTime, FORMAT_YYYY_MM_DD_HH_MM_SS);
        Date endTimeParse = DateUtils.parse(endTime, FORMAT_YYYY_MM_DD_HH_MM_SS);
        Long startTimeStamp = startTimeParse.getTime();
        Long endTimeStamp = endTimeParse.getTime();
        double hours = (double) Math.round((endTimeStamp-startTimeStamp)/3600/1000 * 100) / 100;
        System.out.println("时间差是："+hours+"（小时）");
        return hours;
    }

//    public static void main(String[] args) {
//        System.out.println(DateUtils.parse("2019-11-6 16:00:00", FORMAT_YYYY_MM_DD_HH_MM_SS));
//        String t1 = "2019-11-06 16:25:02";
//        String t2 = "2019-11-06 18:25:02";
//        System.out.println(getRequiredTime(t1,t2));
//    }
}
