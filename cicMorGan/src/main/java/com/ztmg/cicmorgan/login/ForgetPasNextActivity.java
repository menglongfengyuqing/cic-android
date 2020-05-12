package com.ztmg.cicmorgan.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.ClearEditText;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 忘记密码next
 *
 * @author pc
 */
public class ForgetPasNextActivity extends BaseActivity implements OnClickListener {
    private TextView tv_send_phone_code;
    private ClearEditText et_phone_code, et_password;
    private Button iv_eyes_no;
    private String phone, pictureCode, key;
    private TimeTask timeTask;
    private boolean isShowPwd;//是否显示密码
    private String code;
    private String password;
    @BindView(R.id.bt_ok)
    Button bt_ok;
    @BindView(R.id.rl_eyes)
    RelativeLayout rl_eyes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(ForgetPasNextActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_forgetpas_next);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        pictureCode = intent.getStringExtra("pictureCode");
        key = intent.getStringExtra("key");
        initView();
        initData();
        sendVerity(phone, "2", "3", key, pictureCode);
        timeTask = new TimeTask(60 * 1000);

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(ForgetPasNextActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


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

    @Override
    protected void initView() {
        setTitle("设置新密码");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(ForgetPasNextActivity.this, "310001_szxmm_back_click");
                finish();
            }
        });
        tv_send_phone_code = (TextView) findViewById(R.id.tv_send_phone_code);
        tv_send_phone_code.setOnClickListener(this);
        et_phone_code = (ClearEditText) findViewById(R.id.et_phone_code);
        et_phone_code.addTextChangedListener(new MyEditText());
        et_password = (ClearEditText) findViewById(R.id.et_password);
        et_password.addTextChangedListener(new MyEditText());
        iv_eyes_no = (Button) findViewById(R.id.iv_eyes_no);
        et_password.setLongClickable(false); //禁止长按
        et_password.setTextIsSelectable(false); // 禁止被用户选择
        //findViewById(R.id.bt_finish).setOnClickListener(this);
        //  findViewById(R.id.tv_tip).setOnClickListener(this);
        bt_ok.setEnabled(false);


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
            if (et_phone_code.getText().toString().length() > 0 && et_password.getText().toString().length() > 0) {
                bt_ok.setEnabled(true);
                bt_ok.setBackgroundResource(R.drawable.bt_red);
            } else {
                bt_ok.setEnabled(false);
                bt_ok.setBackgroundResource(R.drawable.bt_gray_36);
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    @OnClick({R.id.bt_ok, R.id.rl_eyes})
    public void onClick(View v) {
        code = et_phone_code.getText().toString();
        password = et_password.getText().toString();
        switch (v.getId()) {
            case R.id.tv_send_phone_code://重新获取验证码
                sendVerity(phone, "2", "3", key, pictureCode);
                break;
            case R.id.rl_eyes://显示密码
                if (isShowPwd) {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPwd = false;
                    iv_eyes_no.setBackgroundResource(R.drawable.pic_pas);
                    et_password.setSelection(et_password.getText().toString().length());
                } else {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPwd = true;
                    iv_eyes_no.setBackgroundResource(R.drawable.pic_no_pas);
                    et_password.setSelection(et_password.getText().toString().length());
                }
                break;
            case R.id.bt_ok:
                onEvent(ForgetPasNextActivity.this, "310002_szxmm_wc_click");
                if (TextUtils.isEmpty(code)) {
                    new CustomToastUtils(this, "请输入您的短信验证码").show();
                    //ToastUtils.show(ForgetPasNextActivity.this, "请输入您的短信验证码");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    new CustomToastUtils(this, "请输入新密码").show();
                    //ToastUtils.show(ForgetPasNextActivity.this, "请输入新密码");
                    return;
                }
                if (!PwdUtil.isPWD(password)) {
                    new CustomToastUtils(this, "请输入6-16位包含数字和字母的新密码").show();
                    //Toast.makeText(ForgetPasNextActivity.this, "密码格式为6-16位数字加字母", 0).show();
                    return;
                }
                String token = null;
                if (LoginUserProvider.getUser(this) != null) {
                    token = LoginUserProvider.getUser(this).getToken();
                }
                verifySmsCode(phone, code, "3", token);
                break;
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
            tv_send_phone_code.setText("重新获取");
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
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ForgetPasNextActivity.this));
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
                        //Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                        timeTask.start();
                    } else if (jsonObject.getString("state").equals("2")) {//发送失败
                        //Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                    } else {
                        //Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(ForgetPasNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

    /**
     * 校验验证码是否正确
     */
    private void verifySmsCode(String mobilePhone, String smsCode, String from, String token) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ForgetPasNextActivity.this));
        String url = Urls.VERIFYSMSCODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("smsCode", smsCode);
        params.put("from", from);
        //params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//校验成功
                        forgetPassword("3", phone, password);
                    } else if (jsonObject.getString("state").equals("2")) {//校验失败
                        CustomProgress.CustomDismis();
                        //Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                    } else {
                        CustomProgress.CustomDismis();
                        //Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(ForgetPasNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(ForgetPasNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //找回密码
    private void forgetPassword(String from, String name, String pwd) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ForgetPasNextActivity.this));
        String url = Urls.NEWFORGETPASSWORD;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("name", name);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String token = object.getString("token");
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                        //Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        ForgetPasActivity.mContext.finish();
                        finish();
                    } else {
                        // Toast.makeText(ForgetPasNextActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasNextActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(ForgetPasNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

}
