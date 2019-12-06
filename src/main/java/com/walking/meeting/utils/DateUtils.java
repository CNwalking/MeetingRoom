package com.walking.meeting.utils;

import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    public static final String FORMAT_YYYY_MM_DD_UNIT = "yyyyMMdd";
    public static final String FORMAT_THREE = "yyyy/MM/dd";
    public static final String FORMAT_FOUR = "yyyy年MM月dd日";
    public static final String FORMAT_YYYY_MM = "yyyy-MM";
    public static final String TIME = "HH:mm:ss";
    public static final String SHOWTIME = "HH:mm";

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

    public static double getMeetingRequiredTime(String startTime, String endTime){
        Date startTimeParse = DateUtils.parse(startTime, FORMAT_YYYY_MM_DD_HH_MM);
        Date endTimeParse = DateUtils.parse(endTime, FORMAT_YYYY_MM_DD_HH_MM);
        Long startTimeStamp = startTimeParse.getTime();
        Long endTimeStamp = endTimeParse.getTime();
        double hours = (double) (endTimeStamp-startTimeStamp)/3600/1000;
        BigDecimal b = new BigDecimal(hours);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double getMeetingRequiredTime(Date startTime,Date endTime){
        Long startTimeStamp = startTime.getTime();
        Long endTimeStamp = endTime.getTime();
        double hours = (double) Math.round((endTimeStamp-startTimeStamp)/3600/1000 * 100) / 100;
        return hours;
    }

    public static String parseDateToEveryDayTime(Date time) {
        return DateFormatUtils.format(time,"HHmm");
    }

    // HHmm的compare方法
    public static int timeCompare(Date dateTime, Date anotherDate) {
        int thisTime = Integer.parseInt(parseDateToEveryDayTime(dateTime));
        int anotherTime = Integer.parseInt(parseDateToEveryDayTime(anotherDate));
        return (thisTime<anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
    }

//    public static void main(String[] args) {
//        System.out.println(DateUtils.parse("2019-11-6 16:00:00", FORMAT_YYYY_MM_DD_HH_MM_SS));
//        String t1 = "2019-11-06 09:00";
//        String t2 = "2019-11-06 09:00";
//        String t3 = "2019-11-06 21:00";
//        String t4 = "2019-11-06 22:00";
//        if (timeCompare(DateUtils.parse(t3, FORMAT_YYYY_MM_DD_HH_MM),DateUtils.parse(t4, FORMAT_YYYY_MM_DD_HH_MM))<0) {
//            throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_EARLY);
//        }
//        System.out.println(Integer.parseInt(parseDateToEveryDayTime(DateUtils.parse(t2, FORMAT_YYYY_MM_DD_HH_MM))));
//        System.out.println(timeCompare(DateUtils.parse(t3,FORMAT_YYYY_MM_DD_HH_MM),
//                DateUtils.parse(t1,FORMAT_YYYY_MM_DD_HH_MM)));
//        System.out.println(getMeetingRequiredTime(t1,t2));
//        System.out.println(parseDateToEveryDayTime(new Date()));
//    }
}
