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
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
/**
 * 关于我们
 * @author pc
 *
 */
public class AboutWeActivity extends BaseActivity{
	private WebView wv_about_we;
	private String url=Urls.MOREABOUTUS;
	private WebSettings ws;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_about_we);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(AboutWeActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		initView();
		initData();
	}

	@Override
	protected void initView() {
		setTitle("关于我们");
		setBack(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		wv_about_we = (WebView) findViewById(R.id.wv_about_we);
		ws = wv_about_we.getSettings();
		ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		ws.setLoadWithOverviewMode(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		wv_about_we.setWebChromeClient(new WebChromeClient(){
			//刚加载完页面时，进度发生变化时，调用此方法
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		wv_about_we.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
//				CustomProgress.show(AboutWeActivity.this);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
//				CustomProgress.CustomDismis();
			}
		
		});
	}

	@Override
	protected void initData() {
		wv_about_we.loadUrl(url+"?app=1");
	}

}
