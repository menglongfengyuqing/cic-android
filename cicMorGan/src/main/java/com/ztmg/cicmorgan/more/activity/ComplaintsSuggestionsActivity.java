package com.ztmg.cicmorgan.more.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.CreateGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SSLSocketFactory;

/**
 * 投诉与建议
 * @author pc
 *
 */
public class ComplaintsSuggestionsActivity extends BaseActivity implements OnClickListener{
	private EditText et_phone,et_text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_complaints_suggestions);
		initView();
		initData();
	}
	@Override
	protected void initView() {
		findViewById(R.id.bt_submit).setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_text = (EditText) findViewById(R.id.et_text);
		et_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String strContent = s.toString();
				int num=Integer.valueOf(strContent.length());
				//				int newNum = 200 - num;
				//				tvNum.setText(newNum + "字");
				if (strContent.length() > 200) {
					Toast.makeText(ComplaintsSuggestionsActivity.this,
							"最多200字", 0).show();
					// 获取输入的字符
					String newStrContent = et_text.getText().toString();
					// 截取50个字符包括
					String cutStringContent = newStrContent.substring(0, 200);
					// 重新设置
					et_text.setText(cutStringContent);
					// 设置光标的位置，不然光标都跑到第一个位置了
					et_text.setSelection(cutStringContent.length());
				}

			}
		});

		setTitle("投诉与建议");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.tv_tip).setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}

	//提交数据
	private void submitData(String from,String name,String remarks){
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ComplaintsSuggestionsActivity.this));  
		String url = Urls.SAVESUGGESTION;
		RequestParams params = new RequestParams();
		params.put("from", from);
		params.put("name", name);
		params.put("remarks", remarks);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("state").equals("0")) {
						Toast.makeText(ComplaintsSuggestionsActivity.this, jsonObject.getString("message"), 0).show();
					}else{
						Toast.makeText(ComplaintsSuggestionsActivity.this, jsonObject.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(ComplaintsSuggestionsActivity.this, "解析异常", 0).show();
				}
			}

			@Override
			public void onFailure(int statusCode,Header[] headers, byte[] responseBody,
					Throwable error) {
				Toast.makeText(ComplaintsSuggestionsActivity.this, "请检查网络", 0).show();
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_submit:
			String phone = et_phone.getText().toString();
			String content = et_text.getText().toString();
			if(!TextUtils.isEmpty(phone)){
				if(PwdUtil.isPhone(phone)){
					if(!TextUtils.isEmpty(content)){
						submitData("3", phone, content);
					}else{
						Toast.makeText(ComplaintsSuggestionsActivity.this, "请输入投诉或者建议内容", 0).show();
					}

				}else{
					Toast.makeText(ComplaintsSuggestionsActivity.this, "请输入正确的手机号", 0).show();
				}
			}else{
				Toast.makeText(ComplaintsSuggestionsActivity.this, "请输入手机号", 0).show();
			}
			break;
		case R.id.tv_tip:
			Intent riskTipFirstIntent = new Intent(ComplaintsSuggestionsActivity.this,AgreementActivity.class);
			riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
			riskTipFirstIntent.putExtra("title", "风险提示书");
			startActivity(riskTipFirstIntent);
			break;
		default:
			break;
		}
	}
	
	//点击空白处隐藏软键盘
		public boolean onTouchEvent(MotionEvent event) {
			if(null != this.getCurrentFocus()){/*** 点击空白位置 隐藏软键盘 */
				InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);             return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);  
			}        
			return super .onTouchEvent(event);     
		}

}
