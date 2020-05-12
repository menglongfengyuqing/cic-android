package com.ztmg.cicmorgan.activity;

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

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.MessageActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

/**
 * 轮播图Webview
 *
 * @author pc
 */
public class RollViewActivity extends BaseActivity {

    private WebView wv_rollView;
    private String url = "";
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
        super.setContentView(R.layout.activity_rollview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(RollViewActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
        setTitle("中投摩根");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
    }

    @Override
    protected void initView() {
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();

        try {
            url = getIntent().getStringExtra("Url");
        } catch (Exception e) {
        }
        wv_rollView = (WebView) findViewById(R.id.wv_roll);
        ws = wv_rollView.getSettings();
        ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        ws.setLoadWithOverviewMode(true);
        ws.setJavaScriptEnabled(true);
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        wv_rollView.setWebChromeClient(new WebChromeClient() {
            //刚加载完页面时，进度发生变化时，调用此方法
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //				CustomProgress.show(RollViewActivity.this);
            }
        });
        wv_rollView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //				CustomProgress.show(RollViewActivity.this);
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
        wv_rollView.loadUrl(url);
    }

}
