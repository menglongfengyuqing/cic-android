
package com.ztmg.cicmorgan.util;

import android.text.TextUtils;
import android.util.Log;


/**
 * 日志相关类:默认关闭Log
 *
 * @author:
 * @Company: <p>
 * log日志管理类
 */
public class LogUtil {

    public static String customTagPrefix = "ZTMG_LOG";

    private LogUtil() {

    }

    public static boolean isDebug() {
        return Config.debug;
    }

    /**
     * 定位输出到具体：%s.%s(Line:%d)
     * 具体哪个: 类(%s).方法(%s)（行号：%d）
     */
    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(Line:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.d(tag, content, tr);
    }

    public static void e(String content) {
        if (!isDebug())
            return;
        String tag = generateTag();

        Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.e(tag, content, tr);
    }

    public static void i(String content) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.i(tag, content, tr);
    }

    public static void v(String content) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.v(tag, content, tr);
    }

    public static void w(String content) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.w(tag, tr);
    }


    public static void wtf(String content) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.wtf(tag, content, tr);
    }

    public static void wtf(Throwable tr) {
        if (!isDebug())
            return;
        String tag = generateTag();
        Log.wtf(tag, tr);
    }

    public static class Config {
        private static boolean debug;

        public static void setDebug(boolean debug) {
            Config.debug = debug;
        }
    }

    /**
     * 分段打印出较长log文本
     *
     * @param logContent 打印文本
     * @param showLength 规定每段显示的长度（AndroidStudio控制台打印log的最大信息量大小为4k）
     * @param tag        打印log的标记
     */
    public static void showLargeLog(String logContent, int showLength, String tag) {
        if (logContent.length() > showLength) {
            String show = logContent.substring(0, showLength);
            Log.e(tag, show);
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if ((logContent.length() - showLength) > showLength) {
                String partLog = logContent.substring(showLength, logContent.length());
                showLargeLog(partLog, showLength, tag);
            } else {
                String printLog = logContent.substring(showLength, logContent.length());
                Log.e(tag, printLog);
            }

        } else {
            Log.e(tag, logContent);
        }
    }
}
