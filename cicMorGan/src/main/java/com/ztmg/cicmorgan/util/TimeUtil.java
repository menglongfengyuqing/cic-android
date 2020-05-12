package com.ztmg.cicmorgan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ztmg.cicmorgan.R;

import android.text.TextUtils;
import android.text.format.Time;

public class TimeUtil {

    /**
     * 与当前时间比较早晚
     *
     * @param time 需要比较的时间
     * @return 输入的时间比现在时间晚则返回true
     */
    public static boolean compareInverstmentListNowTime(String time) {
        boolean isDayu = false;

        //获取手机当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        long nowStr = getInvestmentListTime(str);
        if (!TextUtils.isEmpty(time) && !time.equals("null")) {
            long getLoanTime = getInvestmentListTime(time);
            long diff = nowStr - getLoanTime;
            if (diff > 0) {
                //输入的时间比现在时间早
                isDayu = false;
            } else {
                isDayu = true;
            }
        }

        return isDayu;
    }


    /**
     * 与当前时间比较早晚
     *
     * @param time 需要比较的时间
     * @return 输入的时间比现在时间晚则返回true
     */
    public static boolean compareNowTime(String time) {
        boolean isDayu = false;

        //获取手机当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        long nowStr = getTime(str);
        if (!TextUtils.isEmpty(time) && !time.equals("null")) {
            long getLoanTime = getTime(time);
            long diff = nowStr - getLoanTime;
            if (diff > 0) {
                //输入的时间比现在时间早
                isDayu = false;
            } else {
                isDayu = true;
            }
        }

        return isDayu;
    }

    public static boolean compareDetailTime(String time) {
        boolean isDayu = false;

        //获取手机当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        long nowStr = getInvestmentListTime("2018-01-02 00:00:00");//2018/01/04 10:37:37
        if (!TextUtils.isEmpty(time) && !time.equals("null")) {
            long getLoanTime = getInvestmentListTime(time);
            long diff = nowStr - getLoanTime;
            if (diff > 0) {
                //输入的时间比现在时间早
                isDayu = false;
            } else {
                isDayu = true;
            }
        }

        return isDayu;
    }

    //将时间格式转换成时间戳
    public static long getTime(String user_time) {
        String re_time = null;
        long l = 0;
        //2017/06/22 00:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
            //			 String str = String.valueOf(l);
            //			 re_time = str;
        } catch (ParseException e) {
        }
        return l;
    }


    //将时间格式转换成时间戳
    public static long getInvestmentListTime(String user_time) {
        String re_time = null;
        long l = 0;
        //2017/06/22 00:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
            //			 String str = String.valueOf(l);
            //			 re_time = str;
        } catch (ParseException e) {
        }
        return l;
    }
}
