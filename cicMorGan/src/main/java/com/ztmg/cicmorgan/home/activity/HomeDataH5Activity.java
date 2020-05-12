package com.ztmg.cicmorgan.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.more.activity.AboutWeActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

public class HomeDataH5Activity extends BaseActivity{
	private WebView wv_home_data;
	private String url;
	private String name;
	private WebSettings ws;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_home_data);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		name = intent.getStringExtra("name");
		if(!name.equals("信息披露")&&!name.equals("风险教育")){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
				setTranslucentStatus(true); 
				SystemBarTintManager tintManager = new SystemBarTintManager(HomeDataH5Activity.this); 
				tintManager.setStatusBarTintEnabled(true); 
				tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
			} 
		}
		initView();
		initData();
	}

	@Override
	protected void initView() {
		View il_home_title = (View) findViewById(R.id.il_home_title);
		View v_home_bg = findViewById(R.id.v_home_bg);
		setTitle(name);
		if(name.equals("信息披露")||name.equals("风险教育")){
			il_home_title.setVisibility(View.GONE);
			v_home_bg.setVisibility(View.GONE);
		}
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		wv_home_data = (WebView) findViewById(R.id.wv_home_data);
		ws = wv_home_data.getSettings();
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
		ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		ws.setLoadWithOverviewMode(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		wv_home_data.setWebChromeClient(new WebChromeClient(){
			//刚加载完页面时，进度发生变化时，调用此方法
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		wv_home_data.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				boolean a = url.contains("type=close&name=disclosure");
				if(url.contains("type=close&name=disclosure")|| url.contains("type=close&name=education")){
					finish();
				}
				return true;
			}
			

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
//				CustomProgress.show(HomeDataH5Activity.this);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
//				CustomProgress.CustomDismis();
			}

		});
		wv_home_data.clearCache(true);
	}

	@Override
	protected void initData() {
		wv_home_data.loadUrl(url+"?app=1");
	}

}
