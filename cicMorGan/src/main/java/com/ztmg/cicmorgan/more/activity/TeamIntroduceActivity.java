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
 * 团队介绍
 * @author pc
 *
 */
public class TeamIntroduceActivity extends BaseActivity{
	private WebView wv_team_introduce;
	private String url="http://cicmorgan.com/more_team_introduce.html";
	private WebSettings ws;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_team_introduce);
		initView();
		initData();
	}
	@Override
	protected void initView() {
		setTitle("团队介绍");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		wv_team_introduce = (WebView) findViewById(R.id.wv_team_introduce);
		ws = wv_team_introduce.getSettings();
		ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		ws.setLoadWithOverviewMode(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		wv_team_introduce.setWebChromeClient(new WebChromeClient(){
			//刚加载完页面时，进度发生变化时，调用此方法
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
//				CustomProgress.show(RollViewActivity.this);
			}
		});
		wv_team_introduce.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				CustomProgress.show(TeamIntroduceActivity.this);
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
		wv_team_introduce.loadUrl(url+"?app=1");
	}
}
