package com.ztmg.cicmorgan.investment.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 投资详情  投资协议
 *
 * @author pc
 */
@SuppressLint("NewApi")
public class AgreementActivity extends BaseActivity {

    private WebView wv_agreement;
    private String path;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activty_agreement);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(AgreementActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        title = intent.getStringExtra("title");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        wv_agreement = (WebView) findViewById(R.id.wv_agreement);
        WebSettings webset = wv_agreement.getSettings();
        // 设置默认的缩放
        webset.setDefaultZoom(ZoomDensity.FAR);
        // 设置网页的默认排列规则
        webset.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // 打开系统自带的默认的缩放按钮
        webset.setBuiltInZoomControls(true);
        webset.setJavaScriptEnabled(true);
        webset.setDomStorageEnabled(true);
        webset.setBlockNetworkImage(false); // 把图片加载放在最后来加载渲染
        webset.setLoadsImagesAutomatically(true);// 是否自动加载图像资
        webset.setCacheMode(WebSettings.LOAD_DEFAULT);
        webset.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webset.setLoadWithOverviewMode(true);
        webset.setJavaScriptEnabled(true);
        webset.setSupportZoom(true);

        wv_agreement.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        // 消除漏洞问题
        wv_agreement.removeJavascriptInterface("searchBoxJavaBredge_");
        if (TextUtils.isEmpty(title) || title.equals(null)) {
            setTitle("出借协议");
        } else {
            setTitle(title);
        }

        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        wv_agreement.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        wv_agreement.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //				CustomProgress.show(AgreementActivity.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //				CustomProgress.CustomDismis();
            }
        });
        if (path.contains("?")) {
            wv_agreement.loadUrl(path + "&app=1");
        } else {
            wv_agreement.loadUrl(path + "?app=1");
        }
    }
}
