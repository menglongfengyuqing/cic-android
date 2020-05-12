package com.ztmg.cicmorgan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 密码工具类
 *
 * @author Administrator
 */
public class PwdUtil {
    /**
     * 密码正则，数字或字母，区分大小写，6位
     *
     * @param pwd
     * @return
     */
    public static boolean isPWD(String pwd) {
        String regex = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9]{6,16}$";

        return pwd.matches(regex);
    }

    //	public static boolean ISPWD(String pwd) {
    //		int count = 0;
    //		String regex1 = ".*\\d+.*";
    //		String regex2 = ".*[A-Za-z]+.*";
    //		String regex3 = ".*[@#$%^&*<>]+.*";
    //		if (pwd.matches(regex1)) {
    //			count++;
    //		}
    //		if (pwd.matches(regex2)) {
    //			count++;
    //		}
    //		if (pwd.matches(regex3)) {
    //			count++;
    //		}
    //
    //		return count>1 && pwd.length()>=6;
    //	}

    public static boolean isCard(String card) {// 判断银行卡格式：（15-19位）
        Pattern p = Pattern
                .compile("^([0-9]{15}|[0-9]{16}|[0-9]{17}|[0-9]{18}|[0-9]{19})$");
        Matcher m = p.matcher(card);
        // System.out.println(m.matches()+"---");
        return m.matches();
    }

    public static boolean isPhone(String phone) {//判断手机号格式
        Pattern p = Pattern
                .compile("[1][3578]\\d{9}");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

}
