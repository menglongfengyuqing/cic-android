package com.ztmg.cicmorgan.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * 网络请求
 *
 * @author pc
 */
public class NetManger {
    private static AsyncHttpClient httpClient;
    private static AsyncHttpClient uploadHttpClient;

    public synchronized static void initAsyncHttpClientInstance() {
        if (httpClient == null) {
            httpClient = new AsyncHttpClient();
            httpClient.addHeader("Accept", "application/json;charset=UTF-8");
            httpClient.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        }
        if (uploadHttpClient == null) {
            uploadHttpClient = new AsyncHttpClient();
            uploadHttpClient.addHeader("Accept", "application/json;charset=UTF-8");
            uploadHttpClient.addHeader("enctype", "multipart/form-data");
        }
    }

    /**
     * 带参数的请求,并获取cookie
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static RequestHandle post(String url, RequestParams params,
                                     ResponseHandlerInterface responseHandler,
                                     PersistentCookieStore cookieStore) {
        httpClient.setCookieStore(cookieStore);
        return httpClient.post(url, params, responseHandler);
    }

    /**
     * 上传附件
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static RequestHandle postUpload(String url, RequestParams params,
                                           ResponseHandlerInterface responseHandler) {
        // if (CookieUitils.getCookies() != null) {//每次请求都要带上cookie
        // BasicCookieStore bcs = new BasicCookieStore();
        // bcs.addCookies(CookieUitils.getCookies().toArray(new
        // Cookie[CookieUitils.getCookies().size()]));
        // uploadHttpClient.setCookieStore(bcs);
        // }
        return uploadHttpClient.post(url, params, responseHandler);
    }

    /**
     * 带参数的请求
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static RequestHandle post(String url, RequestParams params,
                                     ResponseHandlerInterface responseHandler) {
        return httpClient.post(url, params, responseHandler);
    }

    /**
     * 不带参数的请求
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static RequestHandle post(String url,
                                     ResponseHandlerInterface responseHandler) {
        return httpClient.post(url, responseHandler);
    }

    /**
     * 获得一个client实例
     *
     * @return
     */
    public static AsyncHttpClient getAsyncHttpClient() {
        if (httpClient == null || uploadHttpClient == null) {
            initAsyncHttpClientInstance();
        }
        return httpClient;
    }

    /**
     * 获得一个UploadHttpClient实例
     *
     * @return
     */
    public static AsyncHttpClient getAsyncUploadHttpClient() {
        if (uploadHttpClient == null || uploadHttpClient == null) {
            initAsyncHttpClientInstance();
        }
        return uploadHttpClient;
    }
}
