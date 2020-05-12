package com.ztmg.cicmorgan.account.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.view.CustomProgress;

/**
 * 紧急联系人
 * @author pc
 *
 */
public class ContactPersonActivity extends BaseActivity{
	private EditText et_person_name,et_person_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_contact_person);
		initView();
		initData();
		getUserInfo(LoginUserProvider.getUser(ContactPersonActivity.this).getToken(),"3");
	}
	@Override
	protected void initView() {
		et_person_name = (EditText) findViewById(R.id.et_person_name);
		et_person_phone = (EditText) findViewById(R.id.et_person_phone);
		//		String name = LoginUserProvider.getUser(ContactPersonActivity.this).getEmergencyUser();
		//		String phone = LoginUserProvider.getUser(ContactPersonActivity.this).getEmergencyTel();

		findViewById(R.id.bt_person_submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = et_person_name.getText().toString();
				String phone = et_person_phone.getText().toString();
				if(TextUtils.isEmpty(name)||name==null){
					Toast.makeText(ContactPersonActivity.this, "姓名不能为空", 0).show();
					return;
				}
				if(TextUtils.isEmpty(phone)||phone==null){
					Toast.makeText(ContactPersonActivity.this, "联系方式不能为空", 0).show();
					return;
				}
				Pattern p = Pattern.compile("[1][3578]\\d{9}");
				Matcher m = p.matcher(phone);
				if(!m.matches()){
					Toast.makeText(ContactPersonActivity.this, "手机号格式不能为空", 0).show();
					return;
				}
				String token = LoginUserProvider.getUser(ContactPersonActivity.this).getToken().toString();
				submit(token,"3",name,phone);
			}
		});
		setTitle("紧急联系人");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(ContactPersonActivity.this,AgreementActivity.class);
				riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
				riskTipFirstIntent.putExtra("title", "风险提示书");
				startActivity(riskTipFirstIntent);
			}
		});
	}

	@Override
	protected void initData() {

	}
	//提交数据
	private void submit(String token,String from,String emergencyUser,String emergencyTel){
		CustomProgress.show(this);
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ContactPersonActivity.this));  
		String url = Urls.UPDATEEMERGENCY;
		RequestParams params = new RequestParams();
		params.put("token", token);
		params.put("from", from);
		params.put("emergencyUser", emergencyUser);
		params.put("emergencyTel", emergencyTel);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				CustomProgress.CustomDismis();
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("state").equals("0")) {
						Toast.makeText(ContactPersonActivity.this, jsonObject.getString("message"), 0).show();
						dialog();
					}else if(jsonObject.getString("state").equals("4")){//系统超时
						String mGesture = LoginUserProvider.getUser(ContactPersonActivity.this).getGesturePwd();
						if(mGesture.equals("1")&&!mGesture.equals("")&&mGesture!=null){// 判断是否设置手势密码
							//设置手势密码
							Intent intent = new Intent(ContactPersonActivity.this,UnlockGesturePasswordActivity.class);
							intent.putExtra("overtime", "0");
							startActivity(intent);
						}else{
							//未设置手势密码
							Intent intent = new Intent(ContactPersonActivity.this,LoginActivity.class);
							intent.putExtra("overtime", "0");
							startActivity(intent);
						}
						//						LoginUserProvider.cleanData(ContactPersonActivity.this);
						//						LoginUserProvider.cleanDetailData(ContactPersonActivity.this);
						DoCacheUtil util=DoCacheUtil.get(ContactPersonActivity.this);
						util.put("isLogin", "");

					}else{
						Toast.makeText(ContactPersonActivity.this, jsonObject.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(ContactPersonActivity.this, "解析异常", 0).show();
				}
			}

			@Override
			public void onFailure(int statusCode,Header[] headers, byte[] responseBody,
					Throwable error) {
				Toast.makeText(ContactPersonActivity.this, "请检查网络", 0).show();
				CustomProgress.CustomDismis();
			}
		});
	}
	// 修改成功的对话框
	private void dialog(){

		final Dialog mdialog = new Dialog(ContactPersonActivity.this, R.style.MyDialog);
		mdialog.setContentView(R.layout.dialog_setting);

		TextView tv_dialog_text = (TextView) mdialog.findViewById(R.id.tv_dialog_text);
		tv_dialog_text.setText("紧急联系人填写成功！");
		ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
		iv_dialog_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mdialog.dismiss();
				finish();
			}
		});
		mdialog.show();
	}
	//点击空白处隐藏软键盘
	public boolean onTouchEvent(MotionEvent event) {
		if(null != this.getCurrentFocus()){/*** 点击空白位置 隐藏软键盘 */
			InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);             return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);  
		}        
		return super .onTouchEvent(event);     
	}

	//获取用户信息
	private void getUserInfo(final String token,String from){
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ContactPersonActivity.this));  
		String url = Urls.GETUSERINFO;
		RequestParams params = new RequestParams();
		params.put("token", token);
		params.put("from", from);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("state").equals("0")) {
						JSONObject dataObj = jsonObject.getJSONObject("data");
						UserInfo info = new UserInfo();
						info.setToken(token);
						info.setPhone(dataObj.getString("name"));
						info.setBankPas(dataObj.getString("businessPwd"));//交易密码
						info.setEmail(dataObj.getString("email"));//邮箱
						String cgbBindBankCardState = dataObj.getString("cgbBindBankCardState");
						if(cgbBindBankCardState.equals("null")){
							info.setIsBindBank("1");//是否绑定银行卡 1未绑定 2已绑定
						}else{
							info.setIsBindBank(cgbBindBankCardState);
						}
						String certificateChecked = dataObj.getString("certificateChecked");
						if(certificateChecked.equals("null")){
							info.setCertificateChecked("1");
						}else{
							info.setCertificateChecked(dataObj.getString("certificateChecked"));//是否注册 1未开户  2已开户
						}
						info.setGesturePwd(dataObj.getString("gesturePwd"));//是否设置过手势密码，0设置1已设置
						info.setAddress(dataObj.getString("address"));//地址
						info.setEmergencyUser(dataObj.getString("emergencyUser"));//紧急联系人
						info.setEmergencyTel(dataObj.getString("emergencyTel"));//紧急联系电话
						if(!dataObj.getString("emergencyUser").equals("")||dataObj.getString("emergencyUser") != null){
							et_person_name.setText(dataObj.getString("emergencyUser"));
						}
						if(!dataObj.getString("emergencyTel").equals("")||dataObj.getString("emergencyTel") != null){
							et_person_phone.setText(dataObj.getString("emergencyTel"));
						}
						info.setRealName(dataObj.getString("realName"));//真实姓名
						info.setIdCard(dataObj.getString("IdCard"));//身份证号
						info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
						info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
						info.setIsTest(dataObj.getString("isTest"));//是否测试
						info.setUserType(dataObj.getString("userType"));//测试类型
						info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
						LoginUserProvider.setUser(info);
						LoginUserProvider.saveUserInfo(ContactPersonActivity.this);
						DoCacheUtil util=DoCacheUtil.get(ContactPersonActivity.this);
						util.put("isLogin", "isLogin");
					}else{
						Toast.makeText(ContactPersonActivity.this, jsonObject.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(ContactPersonActivity.this, "解析异常", 0).show();
				}
			}

			@Override
			public void onFailure(int statusCode,Header[] headers, byte[] responseBody,
					Throwable error) {
				Toast.makeText(ContactPersonActivity.this, "请检查网络", 0).show();
			}
		});
	}
}
