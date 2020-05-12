package com.ztmg.cicmorgan.more.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.AccountMessageActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 在线客服
 * @author pc
 *
 */
public class OnlineContactWeActivity extends BaseActivity{
	private WebView wv_contact_we;
	private final static int FILECHOOSER_RESULTCODE = 1;
//	http://a1.7x24cc.com/phone_webChat.html?accountId=N000000003539&chatId=43fea77f-27c5-4298-a73c-4291941961ce
	private final String host = "a1.7x24cc.com/phone_webChat.html?accountId=N000000003539&chatId=43fea77f-27c5-4298-a73c-4291941961ce";
	private final String urlAddress = "http://" + host;
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mUploadCallbackAboveL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_contact_we);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(OnlineContactWeActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		initView();
		initData();
	}

	@Override
	protected void initView() {
		setTitle("在线客服");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		wv_contact_we = (WebView) findViewById(R.id.wv_contact_we);
		WebSettings settings = wv_contact_we.getSettings();
		settings.setJavaScriptEnabled(true);
		wv_contact_we.loadUrl(urlAddress);
		wv_contact_we.setWebViewClient(new MyWebViewClient());

		wv_contact_we.setWebChromeClient(new WebChromeClient() {
			//关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。

			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg) {

				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				OnlineContactWeActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"),
						FILECHOOSER_RESULTCODE);

			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				OnlineContactWeActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Browser"),
						FILECHOOSER_RESULTCODE);
			}

			//For Android 4.1
			public void openFileChooser(ValueCallback uploadMsg, String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				OnlineContactWeActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), 
						OnlineContactWeActivity.FILECHOOSER_RESULTCODE);

			}
			// For Android 5.0+
			public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
				mUploadCallbackAboveL = filePathCallback;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				OnlineContactWeActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Browser"),
						FILECHOOSER_RESULTCODE);
				return true;
			}

		});

	}

	@Override
	protected void initData() {

	}
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (Uri.parse(url).getHost().equals(host)) {
				// This is my web site, so do not override; let my WebView load the page
				return false;
			}
			// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

			//		      progressBar.setVisibility(View.GONE);
		}
	}
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
		if (requestCode != FILECHOOSER_RESULTCODE
				|| mUploadCallbackAboveL == null) {
			return;
		}

		Uri[] results = null;
		if (resultCode == Activity.RESULT_OK) {
			if (data == null) {

			} else {
				String dataString = data.getDataString();
				ClipData clipData = data.getClipData();

				if (clipData != null) {
					results = new Uri[clipData.getItemCount()];
					for (int i = 0; i < clipData.getItemCount(); i++) {
						ClipData.Item item = clipData.getItemAt(i);
						results[i] = item.getUri();
					}
				}

				if (dataString != null)
					results = new Uri[]{Uri.parse(dataString)};
			}
		}
		mUploadCallbackAboveL.onReceiveValue(results);
		mUploadCallbackAboveL = null;
		return;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// 捕捉"回退"按键，让WebView能回退到上一页，而不是直接关闭Activity。
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_contact_we.canGoBack()) {
			wv_contact_we.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==FILECHOOSER_RESULTCODE)
		{
			if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (mUploadCallbackAboveL != null) {
				onActivityResultAboveL(requestCode, resultCode, data);
			}
			else if (mUploadMessage != null) {
				mUploadMessage.onReceiveValue(result);
				mUploadMessage = null;
			}
		}
	}

}

