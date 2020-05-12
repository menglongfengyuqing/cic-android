package com.ztmg.cicmorgan.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ztmg.cicmorgan.net.OkUtil;

/**
 * 根据网络请求重新写 base基类  慢慢替换
 * dong dong
 * 2018年7月18日
 * <p>
 * BaseFragmentCommon  基类
 */

public abstract class BaseFragmentCommon extends Fragment implements OkUtil.HttpCallback {
    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    public Activity mActivity;
    // public Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        //mContext = getContext();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void success(String result, String json) {
        responseData(result, json);
    }

    /**
     * 请求返回数据
     */
    protected abstract void responseData(String stringId, String json);

}
