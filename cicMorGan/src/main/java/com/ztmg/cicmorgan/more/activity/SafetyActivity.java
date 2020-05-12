package com.ztmg.cicmorgan.more.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.SupplyChainActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

/**
 * 安全保障
 * @author pc
 *
 */
public class SafetyActivity extends BaseActivity{
	private WebView wv_safety;
	private String url=Urls.MORESAFETYCONTROL;
	private WebSettings ws;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_safety);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(SafetyActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		initView();
		initData();
	}

	@Override
	protected void initView() {
		setTitle("安全保障");
		setBack(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		wv_safety = (WebView) findViewById(R.id.wv_safety);
		ws = wv_safety.getSettings();
		ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		ws.setLoadWithOverviewMode(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		wv_safety.setWebChromeClient(new WebChromeClient(){
			//刚加载完页面时，进度发生变化时，调用此方法
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
//				CustomProgress.show(RollViewActivity.this);
			}
		});
		wv_safety.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				CustomProgress.show(SafetyActivity.this);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				CustomProgress.CustomDismis();
			}
		
		});
	}

	@Override
	protected void initData() {
		wv_safety.loadUrl(url+"?app=1");
	}

}
