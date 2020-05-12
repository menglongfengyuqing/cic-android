package com.ztmg.cicmorgan.net;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.ztmg.cicmorgan.net.callback.StringCallBackWithError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by wanghao on 2017/6/23 0023.
 */

public class HttpUtils {
    /**
     * get方法获取数据
     *
     * @param url
     * @param callback
     */
    public static void getData(String url, StringCallBackWithError callback) {
        //Logger.d(url);
        GetRequest<String> get = OkGo.get(url);
        get.cacheKey("platform_cache")
                .cacheMode(CacheMode.DEFAULT)
                .execute(callback);
    }

    public static void postData(String url, Map<String, String> params, StringCallBackWithError callback) {
        PostRequest<String> post = OkGo.post(url);
        post.params(params)
                .cacheKey("platform_cache")
                .cacheMode(CacheMode.DEFAULT)
                .execute(callback);
    }

    public static void uploadFile(String url, Map<String, String> params, File file, StringCallBackWithError callback) {
        PostRequest<String> post = OkGo.post(url);
        List list = new ArrayList();
        list.add(file);
        post.addFileParams("returnBody", list);
        post.params(params).cacheKey("platform_cache").cacheMode(CacheMode.DEFAULT).upFile(file).execute(callback);
    }

    public static void downLoadFile(String url, FileCallback callback) {
        //Logger.d(url);
        GetRequest<File> get = OkGo.get(url);
        get.cacheKey("platform_cache").cacheMode(CacheMode.DEFAULT).execute(callback);
    }
}
