package com.ztmg.cicmorgan.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.CreateGesturePasswordActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 注册发送手机验证码
 *
 * @author pc
 */
public class SendCodeActivity extends BaseActivity implements OnClickListener {

    private String phone;
    private EditText et_phone_code;
    private TextView tv_send_phone_code;
    private TextView tv_phone;
    private TimeTask timeTask;
    private String ip;
    private String password, recommendMobilePhone;
    private String overtime;
    private Dialog registerResult;
    private String key;
    private String pictureCode;
    @BindView(R.id.bt_register)
    Button bt_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_send_code);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(SendCodeActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone").trim();
        password = intent.getStringExtra("password").toString().trim();
        recommendMobilePhone = intent.getStringExtra("recommendMobilePhone").toString().trim();
        key = intent.getStringExtra("key");
        pictureCode = intent.getStringExtra("pictureCode");
        overtime = intent.getStringExtra("overtime");
        sendVerity(phone, "1", "3", key, pictureCode);
        initView();
        initData();
        timeTask = new TimeTask(60 * 1000);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(SendCodeActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


    }

    @Override
    protected void initView() {
        setTitle("注册");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(SendCodeActivity.this, "215001_zhuce_back_click");
                finish();
            }
        });
        et_phone_code = (EditText) findViewById(R.id.et_phone_code);
        tv_send_phone_code = (TextView) findViewById(R.id.tv_send_phone_code);
        tv_send_phone_code.setOnClickListener(this);
        et_phone_code.addTextChangedListener(new MyEditText());
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        //        if (!TextUtils.isEmpty(phone) && phone != null) {
        //            String maskNumber = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
        //            tv_phone.setText(maskNumber);
        //        }
        bt_register.setEnabled(false);
        bt_register.setBackgroundResource(R.drawable.bt_gray_36);

        // findViewById(R.id.tv_tip).setOnClickListener(this);
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


    public class MyEditText implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (et_phone_code.getText().toString().length() > 0) {
                bt_register.setEnabled(true);
                bt_register.setBackgroundResource(R.drawable.bt_red);
            } else {
                bt_register.setEnabled(false);
                bt_register.setBackgroundResource(R.drawable.bt_gray_36);
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    @OnClick({R.id.bt_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send_phone_code:
                sendVerity(phone, "1", "3", key, pictureCode);
                break;
            case R.id.bt_register:
                onEvent(SendCodeActivity.this, "215002_zhuce_zczh_click");
                String phoneCode = et_phone_code.getText().toString().trim();
                if (TextUtils.isEmpty(phoneCode)) {
                    //ToastUtils.show(SendCodeActivity.this, "请输入验证码");
                    new CustomToastUtils(this, "请输入正确的短信验证码").show();
                    return;
                }
                verifySmsCode(phone, phoneCode, "3");
                break;
            // case R.id.tv_tip:
            //Intent riskTipFirstIntent = new Intent(SendCodeActivity.this, AgreementActivity.class);
            //riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
            //riskTipFirstIntent.putExtra("title", "风险提示书");
            //startActivity(riskTipFirstIntent);
            //break;
            default:
                break;
        }
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
            tv_send_phone_code.setText("重新发送");
            tv_send_phone_code.setClickable(true);
            tv_send_phone_code.setTextColor(getResources().getColor(R.color.text_d40f42));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_send_phone_code.setText(millisUntilFinished / 1000 + "秒后重发");
            tv_send_phone_code.setClickable(false);
            tv_send_phone_code.setTextColor(getResources().getColor(R.color.text_989898));
        }
    }


    /**
     * 发送验证码
     */
    public void sendVerity(String mobilePhone, String type, String from, String key, String picturCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(SendCodeActivity.this));
        String url = Urls.NEWSENDSMSCODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("type", type);
        params.put("from", from);
        params.put("key", key);
        params.put("picturCode", picturCode);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//发送成功
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                        timeTask.start();
                    } else if (jsonObject.getString("state").equals("2")) {//发送失败
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                    } else {
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SendCodeActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(SendCodeActivity.this, "请检查网络", 0).show();
            }
        });
    }

    /**
     * 校验验证码是否正确
     */
    private void verifySmsCode(String mobilePhone, String smsCode, String from) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(SendCodeActivity.this));
        String url = Urls.VERIFYSMSCODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("smsCode", smsCode);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//校验成功
                        register("3", phone, password, ip, recommendMobilePhone);//注册
                    } else if (jsonObject.getString("state").equals("2")) {//校验失败
                        CustomProgress.CustomDismis();
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                    } else {
                        CustomProgress.CustomDismis();
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(SendCodeActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(SendCodeActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //注册
    private void register(String from, String name, String pwd, String ip, String recommendMobilePhone) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(SendCodeActivity.this));
        String url = Urls.NEWREGIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("name", name);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        params.put("ip", ip);
        params.put("recommendMobilePhone", recommendMobilePhone);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//注册成功
                        String token = jsonObject.getString("token");
                        getUserInfo(token, "3");
                    } else if (jsonObject.getString("state").equals("2")) {//注册失败
                        CustomProgress.CustomDismis();
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                    } else {
                        CustomProgress.CustomDismis();
                        //Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(SendCodeActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(SendCodeActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(SendCodeActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取手机ip
    private void getPhoneIp() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        ip = intToIp(ipAddress);
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(SendCodeActivity.this));
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
                        info.setPhone(phone);
                        info.setBankPas(dataObj.getString("businessPwd"));//交易密码
                        info.setEmail(dataObj.getString("email"));//邮箱
                        info.setIsBindBank(dataObj.getString("cgbBindBankCard"));//是否绑定银行卡 1未绑定 2已绑定
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
                        LoginUserProvider.saveUserInfo(SendCodeActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(SendCodeActivity.this);
                        util.put("isLogin", "isLogin");
                        util.put("mobile", phone);

                        //注册成功之后弹框
                        registerSuccessDialog();

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(SendCodeActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(SendCodeActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(SendCodeActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(registerActivity.this);
                        //LoginUserProvider.cleanDetailData(registerActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(SendCodeActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(SendCodeActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SendCodeActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(SendCodeActivity.this, "请检查网络", 0).show();
            }
        });
    }


    private void registerSuccessDialog() {
        registerResult = new Dialog(SendCodeActivity.this, R.style.MyDialog);
        registerResult.setContentView(R.layout.dl_register_result);

        TextView tv_register_next = (TextView) registerResult.findViewById(R.id.tv_register_next);
        tv_register_next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                registerResult.dismiss();
                Intent intent = new Intent(SendCodeActivity.this, CreateGesturePasswordActivity.class);
                intent.putExtra("enterType", "isRegister");//判断进入手势时是哪个类
                intent.putExtra("overtime", overtime);
                startActivity(intent);
                LoginActivity.getInstance().finish();
                finish();
            }
        });
        registerResult.show();
    }

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

}
