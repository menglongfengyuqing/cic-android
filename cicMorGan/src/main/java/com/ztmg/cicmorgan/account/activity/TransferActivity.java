package com.ztmg.cicmorgan.account.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivityCommon;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 转账充值 by
 * dong dong on 2018/10/11.
 */

public class TransferActivity extends BaseActivityCommon implements View.OnClickListener {


    @BindView(R.id.wv_recharge_safety)
    WebView wv_recharge_safety;
    private WebSettings ws;

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(TransferActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);
        setTitle("转账充值");
        setBack(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(TransferActivity.this, "311001_zzcz_back_click");
                finish();
            }
        });
        setRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(TransferActivity.this, "311002_zzcz_lxkf_click");
                //联系客服
                ContactServiceDialog dialog = new ContactServiceDialog(TransferActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });

        ws = wv_recharge_safety.getSettings();
        ws.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        ws.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        ws.setSupportZoom(true);//是否可以缩放，默认true
        ws.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        ws.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        ws.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        ws.setAppCacheEnabled(true);//是否使用缓存
        ws.setDomStorageEnabled(true);//开启本地DOM存储
        ws.setLoadsImagesAutomatically(true); // 加载图片
        ws.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放

        wv_recharge_safety.setWebChromeClient(new WebChromeClient() {
            //刚加载完页面时，进度发生变化时，调用此方法
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        wv_recharge_safety.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(url.contains("type=goBack")){
                    finish();
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(TransferActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

    }

    @Override
    protected void initData() {
        wv_recharge_safety.loadUrl(Urls.ACCOUNTRECHARGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }


    @Override
    protected void responseData(String stringId, String json) {
    }
    @Override
    public void onClick(View v) {
    }

    //联系客服弹框
    public class ContactServiceDialog extends Dialog {
        Context context;

        public ContactServiceDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ContactServiceDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_contact_service);

            TextView tv_on_line = (TextView) findViewById(R.id.tv_on_line);//在线客服
            tv_on_line.setTextColor(context.getResources().getColor(R.color.text_34393c));
            TextView tv_investment = (TextView) findViewById(R.id.tv_investment);//投资业务
            TextView tv_loan = (TextView) findViewById(R.id.tv_loan);//借款业务
            tv_loan.setTextColor(context.getResources().getColor(R.color.text_34393c));
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_on_line.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TransferActivity.this, OnlineContactWeActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            tv_investment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(Intent.ACTION_CALL);
                    mIntent.setData(Uri.parse("tel:4006669068"));
                    startActivity(mIntent);
                    dismiss();
                }
            });
            tv_loan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006669068"));
                    startActivity(intent);
                    dismiss();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //取消
                    dismiss();
                }
            });
        }

    }
}
