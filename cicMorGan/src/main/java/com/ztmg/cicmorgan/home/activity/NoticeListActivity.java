package com.ztmg.cicmorgan.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 首页公告列表
 *
 * @author pc
 */
public class NoticeListActivity extends BaseActivity {
    private WebView wv_notice;
    //	private String url="/account_announcement.html";
    private String url;
    private WebSettings ws;
    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mindex++;
            if (mindex >= 5) {
                newProgress += 10;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessage(1);

            } else {
                newProgress += 5;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(NoticeListActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_notice_list);
        Intent intent = getIntent();
        url = intent.getStringExtra("Url");
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(NoticeListActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
        setTitle("中投摩根");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(NoticeListActivity.this, "302001_ptgg_back_click");
                finish();
            }
        });
        wv_notice = (WebView) findViewById(R.id.wv_notice);
        ws = wv_notice.getSettings();
        ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        ws.setLoadWithOverviewMode(true);
        ws.setJavaScriptEnabled(true);
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        wv_notice.setWebChromeClient(new WebChromeClient() {
            //刚加载完页面时，进度发生变化时，调用此方法
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //				CustomProgress.show(RollViewActivity.this);
            }
        });
        wv_notice.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //				CustomProgress.show(NoticeListActivity.this);
                newProgress = 0;
                mindex = 0;
                slowlyProgressBar.setProgress(0);
                slowlyProgressBar.onProgressStart();
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //				CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
            }

        });
    }

    @Override
    protected void initData() {
        wv_notice.loadUrl(Urls.WEB_SERVER_PATH_H5 + url);
    }

}
