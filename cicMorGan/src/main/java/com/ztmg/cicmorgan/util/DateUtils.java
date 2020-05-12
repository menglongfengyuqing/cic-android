package com.ztmg.cicmorgan.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static long timestamp;
    private static SimpleDateFormat sf_month, sf_day;
    private static String[] str_English = {"Jan", "Feb", "Mar", "Apr", "May",
            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static String[] str_Chinese = {"一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"};

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前，2小时前，3天前，4个月前，5年前
     *
     * @param timeStamp
     * @return
     */
    public static String convertTimeToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30) {
            return getTime(String.valueOf(timeStamp));
        } else {
            return "刚刚";
        }
    }

    /* 将字符串转为时间戳 */
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return date.getTime();
    }

    /* 将字符串转为时间戳 */
    public static long getStringToDatee(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取当前年：2015
     *
     * @param time
     * @return
     */
    public static String getYear(String time) {
        String month = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        long date = Long.valueOf(time).longValue();
        month = dateFormat.format(new Date(date * 1000L));
        return month;
    }

    /**
     * 获取当前月：02
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getMonth(String time) {
        String month = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        long date = Long.valueOf(time).longValue();
        month = dateFormat.format(new Date(date * 1000L));
        return month;
    }

    /**
     * 获取当前日：22
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDay(String time) {
        String day = "";
        // timestamp = Long.valueOf(time).longValue();
        // Date d = new Date(timestamp);
        // sf_day = new SimpleDateFormat("dd");
        // day = sf_day.format(d);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        long date = Long.valueOf(time).longValue();
        day = dateFormat.format(new Date(date * 1000L));
        return day;
    }

    /**
     * 获取年月日时分秒：2015-02-02 06:11:56
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 获取年月日时分秒：2015年02月
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYearMonth(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 获取年月日时分：2015-02-02 06:11
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTimeYMD(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 获取年月日时分：2015-02-02 06:11
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTimeTomm(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 获取月日时分：02月02日 06:11
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTimeTomd(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 时分：06:11
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTimeHM(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 获取月日时分：02月02日
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTimemd(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    /**
     * 获取年月日：2015-02-02
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getOnlyDate(String time) {
        String nowTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long date = Long.valueOf(time).longValue();
        nowTime = dateFormat.format(new Date(date * 1000L));
        return nowTime;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        String data = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        data = formatter.format(curDate);
        return data;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeCha(String date, String time) {
        long t = 0;
        String d = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        try {
            d1 = df.parse(date);
            d2 = df.parse(time);
            long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
                    * (1000 * 60 * 60))
                    / (1000 * 60);
            if (minutes > 0 && hours == 0 && days == 0) {
                t = minutes;
                d = t + "分钟之前";
            } else if (hours > 0 && days == 0) {
                t = hours;
                d = t + "小时之前";
            } else if (days > 0) {
                t = days;
                d = t + "天之前";
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }

    public static String getEnglish(String month) {
        String english = "";
        if (month.equals("01")) {
            english = str_English[0];
        } else if (month.equals("02")) {
            english = str_English[1];
        } else if (month.equals("03")) {
            english = str_English[2];
        } else if (month.equals("04")) {
            english = str_English[3];
        } else if (month.equals("05")) {
            english = str_English[4];
        } else if (month.equals("06")) {
            english = str_English[5];
        } else if (month.equals("07")) {
            english = str_English[6];
        } else if (month.equals("08")) {
            english = str_English[7];
        } else if (month.equals("09")) {
            english = str_English[8];
        } else if (month.equals("10")) {
            english = str_English[9];
        } else if (month.equals("11")) {
            english = str_English[10];
        } else if (month.equals("12")) {
            english = str_English[11];
        }
        return english;
    }

    public static String getChinese(String month) {
        String english = "";
        if (month.equals("01")) {
            english = str_Chinese[0];
        } else if (month.equals("02")) {
            english = str_Chinese[1];
        } else if (month.equals("03")) {
            english = str_Chinese[2];
        } else if (month.equals("04")) {
            english = str_Chinese[3];
        } else if (month.equals("05")) {
            english = str_Chinese[4];
        } else if (month.equals("06")) {
            english = str_Chinese[5];
        } else if (month.equals("07")) {
            english = str_Chinese[6];
        } else if (month.equals("08")) {
            english = str_Chinese[7];
        } else if (month.equals("09")) {
            english = str_Chinese[8];
        } else if (month.equals("10")) {
            english = str_Chinese[9];
        } else if (month.equals("11")) {
            english = str_Chinese[10];
        } else if (month.equals("12")) {
            english = str_Chinese[11];
        }
        return english;
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDate2String3(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * 将字符串日期数据“YYYY_MM_DD_HH_MM_SS1”转换为Date对象
     *
     * @param sDate 日期数据的字符串表示形式“YYYY_MM_DD_HH_MM_SS1”
     * @return
     */
    public static Date getString2Date3(String sDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
