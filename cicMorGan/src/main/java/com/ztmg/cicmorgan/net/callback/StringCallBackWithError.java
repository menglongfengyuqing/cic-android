package com.ztmg.cicmorgan.net.callback;

import android.content.Context;
import android.content.Intent;

import com.lzy.okgo.callback.StringCallback;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.util.CommonUtil;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.ToastUtils;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * dong dong
 * 2017/7/24 0024.
 * 网络成功返回
 * 异常抛出(判断网络)
 */

public class StringCallBackWithError extends StringCallback {

    private Context context;

    public StringCallBackWithError() {
        super();
    }

    public StringCallBackWithError(Context context) {
        //super();
        this.context = context;
    }

    @Override
    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
        try {
            JSONObject object = new JSONObject(response.body());
            int errorCode = Integer.parseInt(object.opt("state").toString());
            if (errorCode != 0) {
                if (errorCode == 4) {
                    String mGesture = LoginUserProvider.getUser(context).getGesturePwd();
                    if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                        //设置手势密码
                        Intent intent = new Intent(context, UnlockGesturePasswordActivity.class);
                        intent.putExtra("overtime", "0");
                        context.startActivity(intent);
                    } else {
                        //未设置手势密码
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("overtime", "0");
                        context.startActivity(intent);
                    }
                    //LoginUserProvider.cleanData(getActivity());
                    //LoginUserProvider.cleanDetailData(getActivity());
                    DoCacheUtil util = DoCacheUtil.get(context);
                    util.put("isLogin", "");
                } else {
                    String errorMsg = (String) object.opt("message");
                    Exception throwable = new Exception(errorMsg);
                    response.setException(throwable);
                    onError(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onSuccess(response.body(), null, null);
    }

    public void onSuccess(String s, Call call, Response response) {

    }


    @Override
    public void onError(com.lzy.okgo.model.Response<String> response) {
        super.onError(response);
        int networkAvailable = CommonUtil.isNetworkAvailable(context);
        if (networkAvailable == 0) {
            ToastUtils.show(context, "网络链接失败");
        } else {
            int code = response.code();
            if (400 < code && code < 500) {
                ToastUtils.show(context, "接口异常了哦");
            }
            if (500 < code && code < 600) {
                ToastUtils.show(context, "服务器异常");
            }
            if (code == 200) {
                ToastUtils.show(context, response.getException().getMessage());
            }
        }

    }
}


