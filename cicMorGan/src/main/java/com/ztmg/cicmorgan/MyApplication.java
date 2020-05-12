package com.ztmg.cicmorgan;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;

import com.lzy.okgo.OkGo;
import com.mob.MobSDK;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.util.DynamicTimeFormat;
import com.ztmg.cicmorgan.util.FileUtils;
import com.ztmg.cicmorgan.util.LockPatternUtils;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.SDCardUtils;

import java.io.File;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    //	private static ImageLoaderUtils instance = null;
    //	private static ImageLoader loader = null;
    //	private static DisplayImageOptions defaultOption;
    // 应用程序的根目录
    public static String baseDirPath;
    private LockPatternUtils mLockPatternUtils;

    public static MyApplication getInstance() {
        return mInstance;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        OkGo.getInstance().init(this);
        mLockPatternUtils = new LockPatternUtils(this);
        // 初始化根目录
        initBaseDir();
        //分享
        MobSDK.init(this, "13c6a19227752", "e56ec53e4b4bbcf0ace095b51706287a");
        // 初始化ImageLoader
        setImageLoaderConfig();
        JPushInterface.setDebugMode(true);//极光推送
        JPushInterface.init(this);
        //极光推送设置顶层状态栏图标
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(mInstance, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.app_icon;
        builder.statusBarDrawable = R.drawable.app_icon;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
        //友盟统计
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
        LogUtil.Config.setDebug(false);//打印日志

    }

    public LockPatternUtils getLockPatternUtils() {
        return mLockPatternUtils;
    }


    /**
     * 设置ImageLoader参数
     */
    private void setImageLoaderConfig() {

        // 设置缓存目录
        File cacheDir = FileUtils.CreateDir(baseDirPath + File.separator
                + "cache");
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory().threadPoolSize(3)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 内存缓存设置
                .memoryCache(new LRULimitedMemoryCache(maxSize))
                // SDcard缓存
                .diskCache(new UnlimitedDiskCache(cacheDir))
                // 设置缓存目录
                .diskCacheFileCount(500)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 日志设置
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);

    }

    /**
     * 获取Application
     */
    public static MyApplication getApp() {
        return mInstance;
    }

    /**
     * 初始化根目录
     */
    public void initBaseDir() {

        if (SDCardUtils.isMounted()) {
            baseDirPath = SDCardUtils.getPath() + "/cicmorgan";
            FileUtils.CreateDir(baseDirPath);
        } else {
            baseDirPath = Environment.getDataDirectory().getAbsolutePath()
                    + "/sunshine";
        }
        // 创建目录
        FileUtils.CreateDir(baseDirPath);
    }

    //    static {
    //        //启用矢量图兼容
    //        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    //        //设置全局默认配置（优先级最低，会被其他设置覆盖）
    //        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
    //            @Override
    //            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
    //                //全局设置（优先级最低）
    //                layout.setEnableLoadMore(false);
    //                layout.setEnableAutoLoadMore(true);
    //                layout.setEnableOverScrollDrag(false);
    //                layout.setEnableOverScrollBounce(true);
    //                layout.setEnableLoadMoreWhenContentNotFull(true);
    //                layout.setEnableScrollContentWhenRefreshed(true);
    //            }
    //        });
    //        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
    //            @NonNull
    //            @Override
    //            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
    //                //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
    //                layout.setPrimaryColorsId(R.color.common_colorPrimary, android.R.color.white);
    //
    //                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
    //            }
    //        });
    //    }
}
