package com.ztmg.cicmorgan.calendar.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Process;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/1/4.
 */

public class AndroidUtils
{

    /**
     * 检查权限
     * @param ctx
     * @param permission
     * @param func
     * @return
     */
    public static  boolean checkCallingPermission(Context ctx, String permission, String func)
    {
        // Quick check: if the calling permission is me, it's all okay.
        if (Binder.getCallingPid() == Process.myPid()) {
            return true;
        }

        if (ctx.checkCallingPermission(permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        String msg = "Permission Denial: " + func + " from pid="
                + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + permission;
        return false;
    }
    public  static String stream2String(InputStream is) throws IOException
    {
        if (is != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int temp = -1;
            while ((temp = is.read(buffer)) != -1) {
                baos.write(buffer, 0, temp);
            }
            is.close();
            baos.close();
            return baos.toString();
        }
        return null;
    }

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            return 0;
        }

        return versioncode;
    }

    /**
     * 后去versionname
     * @param context
     * @return
     * @author Mark
     */
    public static String getAppVersionName(Context context) {
        String versionName = "0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "0";
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

        return versionName;
    }


    /**
     * 验证以1开头11位
     */
    public static boolean isMobileNO(String mobiles) { // 是否是手机号格式
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
