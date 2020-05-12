package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 绑定手机
 *
 * @author pc
 */
public class BindPhoneActivity extends BaseActivity implements OnClickListener {
    private TextView tv_before_phone_num;
    private TextView tv_before_send_code;
    private EditText et_before_phone_code;
    private String ip;
    private TimeTask timeTask;
    public static BindPhoneActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_bind_phone);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BindPhoneActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        mContext = this;
        initView();
        initData();
        timeTask = new TimeTask(60 * 1000);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(BindPhoneActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    public static BindPhoneActivity getInstance() {
        return mContext;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo(LoginUserProvider.getUser(BindPhoneActivity.this).getToken(), "3");
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }


    @Override
    protected void initView() {
        tv_before_phone_num = (TextView) findViewById(R.id.tv_before_phone_num);

        et_before_phone_code = (EditText) findViewById(R.id.et_before_phone_code);
        tv_before_send_code = (TextView) findViewById(R.id.tv_before_send_code);
        tv_before_send_code.setOnClickListener(this);//发送原来手机号验证码
        findViewById(R.id.bt_submit).setOnClickListener(this);
        setTitle("验证原手机");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(BindPhoneActivity.this, "304001_phone_back_click");
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_before_send_code:
                String beforePhone = tv_before_phone_num.getText().toString();
                sendVerity(beforePhone, "8", ip, "3", LoginUserProvider.getUser(BindPhoneActivity.this).getToken());
                break;
            case R.id.bt_submit:
                onEvent(BindPhoneActivity.this, "304002_phone_next_btn_click");
                String oldCode = et_before_phone_code.getText().toString();
                String phone = tv_before_phone_num.getText().toString();
                if (TextUtils.isEmpty(oldCode)) {
                    ToastUtils.show(BindPhoneActivity.this, "请输入短信验证码");
                    return;
                }
                verifySmsCode(phone, oldCode, "3");
                break;
            default:
                break;
        }
    }

    /**
     * 发送验证码
     */
    public void sendVerity(String mobilePhone, String type, String ip, String from, String token) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneActivity.this));
        String url = Urls.SEND_CODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("type", type);
        //params.put("ip", ip);
        params.put("from", from);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//发送成功
                        Toast.makeText(BindPhoneActivity.this, jsonObject.getString("message"), 0).show();
                        timeTask.start();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindPhoneActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindPhoneActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindPhoneActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //								LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //								LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneActivity.this);
                        util.put("isLogin", "");

                    } else if (jsonObject.getString("state").equals("2")) {//发送失败
                        Toast.makeText(BindPhoneActivity.this, jsonObject.getString("message"), 0).show();
                    } else {
                        Toast.makeText(BindPhoneActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindPhoneActivity.this, "解析异常", 0).show();
                    CustomProgress.CustomDismis();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BindPhoneActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    // 计时器
    private class TimeTask extends CountDownTimer {

        public TimeTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public TimeTask(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onFinish() {
            tv_before_send_code.setText("重新发送");
            tv_before_send_code.setTextColor(getResources().getColor(R.color.text_a11c3f));
            tv_before_send_code.setClickable(true);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_before_send_code.setText(millisUntilFinished / 1000 + "s后重新发送");
            tv_before_send_code.setTextColor(getResources().getColor(R.color.text_989898));
            tv_before_send_code.setClickable(false);
        }
    }

    //校验验证码是否正确
    private void verifySmsCode(String mobilePhone, String smsCode, String from) {
        CustomProgress.show(BindPhoneActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneActivity.this));
        String url = Urls.VERIFYSMSCODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("smsCode", smsCode);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//校验成功
                        Intent intent = new Intent(BindPhoneActivity.this, BindPhoneNextActivity.class);
                        startActivity(intent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindPhoneActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindPhoneActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindPhoneActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //								LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //								LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BindPhoneActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindPhoneActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BindPhoneActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        CustomProgress.show(BindPhoneActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneActivity.this));
        String url = Urls.GETUSERINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserInfo info = new UserInfo();
                        info.setToken(token);
                        info.setPhone(dataObj.getString("name"));
                        tv_before_phone_num.setText(dataObj.getString("name"));
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
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(BindPhoneActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneActivity.this);
                        util.put("isLogin", "isLogin");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindPhoneActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindPhoneActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindPhoneActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //								LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //								LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BindPhoneActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindPhoneActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BindPhoneActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }
}
