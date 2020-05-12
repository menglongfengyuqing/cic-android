package com.ztmg.cicmorgan.net;


import android.content.Context;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.ztmg.cicmorgan.net.callback.JsonCallback;
import com.ztmg.cicmorgan.net.callback.StringCallBackWithError;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by dong on 2018年7月11日
 * 网络框架二次封装
 */

public class OkUtil {


    /**
     * get
     *
     * @param url
     * @param tag
     * @param map
     * @param callback
     * @param <T>
     */
    public static <T> void get(String url, Object tag, Map<String, String> map, JsonCallback<T> callback) {
        // TODO: 2017/10/13  加密 时间戳等 请求日志打印
        Log.d("OkGoUtil", "method get");
        OkGo.<T>get(url)
                .tag(tag)
                .params(map)
                .cacheMode(CacheMode.DEFAULT)
                .execute(callback);
    }


    /**
     * post
     *
     * @param result       返回id
     * @param url          接口
     * @param map          参数
     * @param context      上下文
     * @param httpCallback 返回的httpCallback，传当前this
     * @param <T>          通用泛型，（暂时没有）
     */
    public static <T> void post(final String result, String url, Map<String, String> map, Context context, final OkUtil.HttpCallback httpCallback) {
        OkGo.<String>post(url)
                .params(map)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallBackWithError(context) {
                    @Override
                    public void onSuccess(String json, Call call, Response response) {
                        //super.onSuccess(json);
                        httpCallback.success(result, json);
                    }
                });
    }

    /**
     * fele 上传文件
     *
     * @param url
     * @param tag
     * @param map
     * @param file
     * @param callback
     * @param <T>
     */
    public static <T> void fileDownload(String url, Object tag, Map<String, String> map, File file, JsonCallback<T> callback) {
        Log.d("OkGoUtil", "method post");
        OkGo.<T>post(url)
                .tag(tag)
                .params(map)
                .upFile(file)
                .execute(callback);
    }

    /**
     * fele 下载文件
     *
     * @param url
     * @param tag
     * @param map
     * @param callback
     * @param <T>
     */
    public static <T> void downLoadFile(String url, Object tag, Map<String, String> map, JsonCallback<T> callback) {
        Log.d("OkGoUtil", "method post");
        OkGo.<T>post(url)
                .tag(tag)
                .params(map)
                .execute(callback);
    }


    public interface HttpCallback {
        void success(String result, String json);

        // void error(Throwable ex, String whereRequest);

        /*再次联网*/
        //void connNetAgain(String whereRequest);
    }

}
