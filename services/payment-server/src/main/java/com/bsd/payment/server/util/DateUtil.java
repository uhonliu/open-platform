package com.bsd.payment.server.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author liujianhong
 */
public class DateUtil {
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDDHHMMSSSSS = "yyyyMMddhhmmssSSS";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddhhmmss";

    private final SimpleDateFormat format;

    public DateUtil(SimpleDateFormat format) {
        this.format = format;
    }

    public SimpleDateFormat getFormat() {
        return this.format;
    }

    //紧凑型日期格式，也就是纯数字类型yyyyMMdd
    public static final DateUtil COMPAT = new DateUtil(new SimpleDateFormat("yyyyMMdd"));

    //常用日期格式，yyyy-MM-dd
    public static final DateUtil COMMON = new DateUtil(new SimpleDateFormat("yyyy-MM-dd"));
    public static final DateUtil COMMON_FULL = new DateUtil(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    //使用斜线分隔的，西方多采用，yyyy/MM/dd
    public static final DateUtil SLASH = new DateUtil(new SimpleDateFormat("yyyy/MM/dd"));

    //中文日期格式常用，yyyy年MM月dd日
    public static final DateUtil CHINESE = new DateUtil(new SimpleDateFormat("yyyy年MM月dd日"));
    public static final DateUtil CHINESE_FULL = new DateUtil(new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒"));

    /**
     * 日期获取字符串
     */
    public String getDateText(Date date) {
        return getFormat().format(date);
    }

    /**
     * 字符串获取日期
     *
     * @throws ParseException
     */
    public Date getTextDate(String text) throws ParseException {
        return getFormat().parse(text);
    }

    /**
     * 日期获取字符串
     */
    public static String getDateText(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 字符串获取日期
     *
     * @throws ParseException
     */
    public static Date getTextDate(String dateText, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(dateText);
    }

    /**
     * 根据日期，返回其星期数，周一为1，周日为7
     *
     * @param date
     * @return
     */
    public static int getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        int ret;
        if (w == Calendar.SUNDAY) {
            ret = 7;
        } else {
            ret = w - 1;
        }
        return ret;
    }


    public static String getCurrentDate() {
        String formatPattern_Short = "yyyyMMddhhmmss";
        SimpleDateFormat format = new SimpleDateFormat(formatPattern_Short);
        return format.format(new Date());
    }

    public static String getSeqString() {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMddHHmmss"); // "yyyyMMdd G
        return fm.format(new Date());
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 获取当前时间，格式为 yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentTimeStr(String format) {
        format = StringUtils.isBlank(format) ? FORMAT_YYYY_MM_DD_HH_MM_SS : format;
        Date now = new Date();
        return date2Str(now, format);
    }

    public static String date2Str(Date date) {
        return date2Str(date, FORMAT_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 时间转换成 Date 类型
     *
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date, String format) {
        if ((format == null) || "".equals(format)) {
            format = FORMAT_YYYY_MM_DD_HH_MM_SS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date != null) {
            return sdf.format(date);
        }
        return "";
    }

    /**
     * 获取批量付款预约时间
     *
     * @return
     */
    public static String getRevTime() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        String dateString = new SimpleDateFormat(DateUtil.FORMAT_YYYYMMDDHHMMSS).format(cal.getTime());
        System.out.println(dateString);
        return dateString;
    }

    /**
     * 时间比较
     *
     * @param date1
     * @param date2
     * @return DATE1>DATE2返回1，DATE1<DATE2返回-1,等于返回0
     */
    public static int compareDate(String date1, String date2, String format) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 把给定的时间减掉给定的分钟数
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date minusDateByMinute(Date date, int minute) {
        Date newDate = new Date(date.getTime() - (minute * 60 * 1000));
        return newDate;
    }

    //判断是否超过24小时
    public static boolean expire(String date1, String date2) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Date start = dateFormat.parse(date1);
        java.util.Date end = dateFormat.parse(date2);
        long cha = end.getTime() - start.getTime();
        double result = cha * 1.0 / (1000 * 60 * 60);
        if (result <= 24) {
            //可用
            return true;
        } else {
            //已过期
            return false;
        }
    }
}

