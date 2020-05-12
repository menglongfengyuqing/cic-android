package com.ztmg.cicmorgan.more.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.view.CustomProgress;

/**
 * 帮助中心
 * @author pc
 *
 */
public class HelpCenterActivity extends BaseActivity{
	private WebView wv_help_center;
	private String url="http://cicmorgan.com/more_help_center.html";
	private WebSettings ws;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_help_center);
		initView();
		initData();
	}

	@Override
	protected void initView() {
		setTitle("帮助中心");
		setBack(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		wv_help_center = (WebView) findViewById(R.id.wv_help_center);
		ws = wv_help_center.getSettings();
		ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		ws.setLoadWithOverviewMode(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		wv_help_center.setWebChromeClient(new WebChromeClient(){
			//刚加载完页面时，进度发生变化时，调用此方法
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
//				CustomProgress.show(RollViewActivity.this);
			}
		});
		wv_help_center.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				CustomProgress.show(HelpCenterActivity.this);
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
		wv_help_center.loadUrl(url+"?app=1");
	}

}
