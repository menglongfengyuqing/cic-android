package com.ztmg.cicmorgan.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;

import java.net.URL;


/**
 * Created by dongdong on 2015/6/15.
 */
public abstract class BasePager {
    public String TAG = getClass().getName(), Tag = TAG, tag = TAG;
    public Context context;
    protected AsyncHttpClient mClient;
    public LayoutInflater inflater;
    private View view;

    public BasePager() {
        view = initView();
    }

    public BasePager(Context context) {
        this.context = context;
        mClient = new AsyncHttpClient();
        inflater = LayoutInflater.from(context);
        view = initView();
    }

    /**
     * 获取根结点
     *
     * @return
     */
    public View getRootView() {
        return view;
    }

    /**
     * 初始化view
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化数据
     */
    public abstract void initData(String string);
}
