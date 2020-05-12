package com.ztmg.cicmorgan.util;

import android.content.Context;

import com.ztmg.cicmorgan.entity.UserAccountInfo;
import com.ztmg.cicmorgan.entity.UserInfo;

/**
 * 当前登陆者，用户信息提供类
 *
 * @author pc
 */
public class LoginUserProvider {
    private static UserInfo loginUser; // 当前登陆者
    public static boolean currentStatus; // 当前登录状态，true代表已登录，false代表未登录
    private static UserAccountInfo userAccountInfo;

    /**
     * 获取当前登陆者的信息
     *
     * @param ctx 上下文
     * @return 如果有登录者的缓存就返回UserEntity实体类，如果没有就返回null
     */
    public static UserInfo getUser(Context ctx) {
        if (loginUser == null) {
            loginUser = (UserInfo) DoCacheUtil.get(ctx).getAsObject(Constant.CurrentUserBean);
            if (loginUser != null) {
                currentStatus = true;
            } else {
                currentStatus = false;
            }
        } else {
            currentStatus = true;
        }
        return loginUser;
    }

    /**
     * 获取当前登录者的详细信息
     *
     * @param ctx
     * @return
     */
    public static UserAccountInfo getUserAccountInfo(Context ctx) {
        if (userAccountInfo == null) {
            userAccountInfo = (UserAccountInfo) DoCacheUtil.get(ctx).getAsObject(
                    Constant.CurrentUserBean);
            if (userAccountInfo != null) {
                currentStatus = true;
            } else {
                currentStatus = false;
            }
        } else {
            currentStatus = true;
        }
        return userAccountInfo;
    }

    /**
     * 设置内存中的登录用户信息
     *
     * @param loginUser
     */
    public static void setUser(UserInfo loginUser) {
        LoginUserProvider.loginUser = loginUser;
    }

    /**
     * 设置内存中登录的详细信息
     */
    public static void setUserDetail(UserAccountInfo userAccountInfo) {
        LoginUserProvider.userAccountInfo = userAccountInfo;
    }

    /**
     * 登陆者信息修改完后，保存到本地
     *
     * @param ctx 上下文
     * @return 保存成功返回true，其它返回false
     */
    public static synchronized boolean saveUserInfo(Context ctx) {
        if (loginUser != null) {
            DoCacheUtil.get(ctx).put(Constant.CurrentUserBean, loginUser);
            currentStatus = true;
            return true;
        } else {
            currentStatus = false;
            return true;
        }
    }

    /**
     * 清除登陆者的信息，本地、内存中的信息将全部清除，并且变成未登录状态
     *
     * @param ctx 上下文
     */
    public static synchronized void cleanData(Context ctx) {
        DoCacheUtil.get(ctx).remove(Constant.CurrentUserBean);
        loginUser = null;
        currentStatus = false;
    }

    //清除详细数据
    public static synchronized void cleanDetailData(Context ctx) {
        DoCacheUtil.get(ctx).remove(Constant.CurrentDetailUserBean);
        userAccountInfo = null;
    }

    /**
     * 保存详细信息
     *
     * @param ctx
     * @return
     */
    public static synchronized boolean saveUserDetailInfo(Context ctx) {
        if (userAccountInfo != null) {
            DoCacheUtil.get(ctx).put(Constant.CurrentDetailUserBean,
                    userAccountInfo);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取详细信息
     *
     * @param ctx
     * @return
     */
    public static synchronized UserAccountInfo getDetailEntity(Context ctx) {
        if (!currentStatus) {
            return null;
        }
        UserAccountInfo ret = (UserAccountInfo) DoCacheUtil.get(ctx)
                .getAsObject(Constant.CurrentDetailUserBean);
        return ret;

    }
}
