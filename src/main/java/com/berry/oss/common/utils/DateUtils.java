
package com.berry.oss.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期、时间类
 *
 * @ author sys
 */
public class DateUtils {

    public static String formatDateByPattern(Date date,String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }
    /***
     * convert Date to cron ,eg.  "0 07 10 15 1 ? 2016"
     * @param date  : 时间点
     * @return
     */
    public static String getCron(Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    /**
     * 日期转换
     *
     * @param time
     * @param fmt  : yyyy-MM-dd HH:mm:ss
     * @return
     * @ author sys
     */
    public static String formatTime(Timestamp time, String fmt) {
        if (time == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
        return myFormat.format(time);
    }

    /**
     * 获取当前时间戳(毫秒)
     *
     * @return
     */
    public static Long getCurrentTimeMills() {
        return System.currentTimeMillis();
    }


    /**
     * 获取系统当前时间（秒）
     *
     * @return
     * @ author sys
     */
    public static Timestamp getTime() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }


    /**
     * 获取当前日期(时间 00:00:00)
     *
     * @return
     * @ author sys
     */
    public static Timestamp getDateFirst() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar calendar = Calendar.getInstance();
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }

    /**
     * 获取当前日期(时间 23:59:59)
     *
     * @return
     * @ author sys
     */
    public static Timestamp getDateLast() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar calendar = Calendar.getInstance();
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }

    /**
     * 获取昨日开始时间 00:00:00
     *
     * @return
     */
    public static Timestamp getYesterdayBegin() {
        Long today = getDateFirst().getTime();
        Long yesterday = today - (24 * 60 * 60 * 1000 - 1);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = myFormat.format(yesterday);
        return Timestamp.valueOf(result);
    }

    /**
     * 获取昨日最后时间- 23:59:59
     *
     * @return
     */
    public static Timestamp getYesterdayEnd() {
        Long today = getDateLast().getTime();
        Long yesterday = today - (24 * 60 * 60 * 1000 - 1);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = myFormat.format(yesterday);
        return Timestamp.valueOf(result);
    }


    /**
     * 获取当前日期
     *
     * @return
     * @ author sys
     */
    public static Date getDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * yyyy-MM-dd HH:mm:ss 转换成 Timestamp
     *
     * @param timeString
     * @return
     * @ author sys
     */
    public static Timestamp getTime(String timeString) {
        return Timestamp.valueOf(timeString);
    }

    /**
     * 自定义格式的字符串转换成日期
     *
     * @param timeString
     * @param fmt
     * @return
     * @throws Exception
     * @ author sys
     */
    public static Timestamp getTime(String timeString, String fmt) throws Exception {
        SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
        Date date = myFormat.parse(timeString);
        myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return getTime(myFormat.format(date));
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param fmt
     * @return
     * @ author sys
     */
    public static String formatDate(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
        return myFormat.format(date);
    }


    /**
     * 格式化日期
     *
     * @param timeString
     * @param timeStringfmt
     * @param fmt
     * @return
     * @throws Exception
     */
    public static String formatDate(String timeString, String timeStringfmt, String fmt) throws Exception {
        SimpleDateFormat myFormat = new SimpleDateFormat(timeStringfmt);
        Date date = myFormat.parse(timeString);
        myFormat = new SimpleDateFormat(fmt);
        return myFormat.format(date);
    }

    /**
     * 返回日期或者时间，如果传入的是日期，返回日期的 00:00:00 时间
     *
     * @param timeString
     * @return
     * @throws Exception
     * @ author sys
     */
    public static Timestamp getDateFirst(String timeString) throws Exception {

        if (timeString == null || "".equals(timeString)) {
            return null;
        }
        if (timeString.length() > 10) {
            return getTime(timeString, "yyyy-MM-dd HH:mm:ss");
        } else {
            return getTime(timeString, "yyyy-MM-dd");
        }
    }

    /**
     * 返回日期或者时间，如果传入的是日期，返回日期的 23:59:59 时间
     *
     * @param timeString
     * @return
     * @throws Exception
     * @ author sys
     */
    public static Timestamp getDateLast(String timeString) throws Exception {
        if (timeString == null || "".equals(timeString)) {
            return null;
        }
        if (timeString.length() > 10) {
            return getTime(timeString, "yyyy-MM-dd HH:mm:ss");
        } else {
            return getTime(timeString + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 获取本周周一时间，返回格式 yyyy-MM-dd 00:00:00
     *
     * @return
     * @ author sys
     */
    public static Timestamp getMonday() {
        Calendar calendar = Calendar.getInstance();
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        calendar.add(Calendar.DATE, -dayofweek + 1);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }

    /**
     * 获取本周周日时间，返回格式 yyyy-MM-dd 23:59:59
     *
     * @return
     * @ author sys
     */
    public static Timestamp getSunday() {
        Calendar calendar = Calendar.getInstance();
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        calendar.add(Calendar.DATE, -dayofweek + 7);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }

    /**
     * 增加天数
     *
     * @param time
     * @param day
     * @return
     * @ author sys
     */
    public static Timestamp addDay(Timestamp time, Long day) {
        Timestamp time2 = new Timestamp(time.getTime() + day * 1000 * 60 * 60 * 24);
        return time2;
    }

    /**
     * 比较 2 个日期格式的字符串
     *
     * @param str1 格式 ：yyyyMMdd
     * @param str2 格式 ：yyyyMMdd
     * @return
     * @ author sys
     */
    public static Integer compareDate(String str1, String str2) throws Exception {
        return Integer.parseInt(str1) - Integer.parseInt(str2);
    }


    /**
     * 2 个时间的相差天数
     *
     * @param time1
     * @param time2
     * @return
     * @ author sys
     */
    public static Integer getDay(Timestamp time1, Timestamp time2) {
        Long dayTime = (time1.getTime() - time2.getTime()) / (1000 * 60 * 60 * 24);
        return dayTime.intValue();
    }

    /**
     * 获取系统当前时间（分）
     *
     * @return
     * @ author sys
     */
    public static String getMinute() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return myFormat.format(new Date());
    }


    /**
     * 获取系统当前时间
     *
     * @return
     * @ formatStyle  时间格式
     * @ author sys
     */
    public static String getDateTime() {
        return DateUtils.getDateTime("yyyyMMddHHmmss");
    }

    public static String getDateTimeDay() {
        return DateUtils.getDateTime("yyyyMMdd");
    }

    /**
     * 获取系统当前时间
     *
     * @return
     * @ formatStyle  时间格式
     * @ author sys
     */
    public static String getDateTime(String formatStyle) {
        if (formatStyle == null || "".equals(formatStyle)) {
            formatStyle = "yyyyMMddHHmmss";
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(formatStyle);
        return myFormat.format(new Date());
    }


    /**
     * 格式化时间成yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @ formatStyle  时间格式
     * @ author sys
     */
    public static String formatDateTime(String time) throws ParseException {
        return DateUtils.formatDateTime(time, "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 格式化时间成yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @ formatStyle  时间格式
     * @ author sys
     */
    public static String formatDateTime(String time, String timeStr) throws ParseException {
        if (timeStr == null || "".equals(timeStr)) {
            timeStr = "yyyy-MM-dd HH:mm:ss";
        }
        Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
        SimpleDateFormat myFormat = new SimpleDateFormat(timeStr);
        return myFormat.format(date);
    }


    /**
     * 转换成时间 字符串格式必须为 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd
     *
     * @return
     * @throws ParseException
     * @ author sys
     */
    public static Date parseToDate(String val) throws ParseException {
        Date date = null;
        if (val != null && val.trim().length() != 0 && !val.trim().toLowerCase().equals("null")) {
            val = val.trim();
            if (val.length() > 10) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf.parse(val);
            }
            if (val.length() <= 10) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.parse(val);
            }
        }
        return date;
    }

    /**
     * 获取上月的第一天 yyyy-MM-dd 00:00:00 和最后一天 yyyy-MM-dd 23:59:59
     *
     * @return
     * @ author sys
     */
    @SuppressWarnings("static-access")
    public static Map<String, String> getPreMonth() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first_prevM = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first_prevM).append(" 00:00:00");
        //上月第一天
        day_first_prevM = str.toString();

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        String day_end_prevM = df.format(calendar.getTime());
        StringBuffer endStr = new StringBuffer().append(day_end_prevM).append(" 23:59:59");
        //上月最后一天
        day_end_prevM = endStr.toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("prevMonthFD", day_first_prevM);
        map.put("prevMonthPD", day_end_prevM);
        return map;
    }


    /**
     * 获取本月的第一天 yyyy-MM-dd 00:00:00
     *
     * @return
     * @ author sys
     */
    public static String getNowMonth() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.MONTH, 0);
        Date theDate = calendar.getTime();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first_prevM = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first_prevM).append(" 00:00:00");
        //本月第一天
        day_first_prevM = str.toString();
        return day_first_prevM;
    }

    /**
     * 获取上周周一时间，返回格式 yyyy-MM-dd 00:00:00
     *
     * @return
     * @ author sys
     */
    @SuppressWarnings("static-access")
    public static Timestamp getPreMonday() {
        Calendar calendar = Calendar.getInstance();
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(dayofweek);
        if (dayofweek == 1) {
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_MONTH, -1);

        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }

    /**
     * 获取上周周日时间，返回格式 yyyy-MM-dd 23:59:59
     *
     * @return
     * @ author sys
     */
    @SuppressWarnings("static-access")
    public static Timestamp getPreSunday() {
        Calendar calendar = Calendar.getInstance();
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayofweek != 1) {
            calendar.add(Calendar.WEEK_OF_MONTH, +1);
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.WEEK_OF_MONTH, -1);

        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String mystrdate = myFormat.format(calendar.getTime());
        return Timestamp.valueOf(mystrdate);
    }

    /**
     * 字符串日期加n天
     *
     * @param date       日期
     * @param days       加的天数
     * @param dateFormat 字符串日期格式
     * @return
     */
    public static String addDay(String date, int days, String dateFormat) {
        if (dateFormat == null || "".equals(dateFormat)) {
            dateFormat = "yyyyMMdd";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Calendar cd = Calendar.getInstance();
            cd.setTime(formatter.parse(date));
            cd.add(Calendar.DATE, days);
            return formatter.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算两个字符串的时间相差的时间
     *
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static Long getDayNum(String beforeDate, String afterDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        long result = 0;
        try {
            long to = df.parse(afterDate).getTime();
            long from = df.parse(beforeDate).getTime();
            result = (to - from) / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return result;
    }

    /**
     * 计算日期两个差的月份
     *
     * @param beforeDate 格式 yyyyMM
     * @param afterDate  格式 yyyyMM
     * @return
     */
    public static int getMonthNum(String beforeDate, String afterDate) {

        int yearNum = Integer.parseInt(afterDate.substring(0, 4))
                - Integer.parseInt(beforeDate.substring(0, 4));

        int resultMonthNum = 0;

        if (yearNum > 0) {
            resultMonthNum = Integer.parseInt(afterDate.substring(4)) +
                    (12 - Integer.parseInt(beforeDate.substring(4))) + 12 * (yearNum - 1);
        } else if (yearNum == 0) {
            resultMonthNum = Integer.parseInt(afterDate.substring(4))
                    - Integer.parseInt(beforeDate.substring(4));
        }

        return resultMonthNum;

    }


    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

}
