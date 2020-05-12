package com.ztmg.cicmorgan.account.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 修改手机号下一步
 *
 * @author pc
 */
public class BindPhoneNextActivity extends BaseActivity implements OnClickListener {
    private TextView tv_new_send_code;
    private EditText et_new_phone, et_new_phone_code;
    private TimeTask timeTask;
    private String ip;
    private String newPhone;
    private String newCode;
    private Dialog mdialog;
    private Dialog contactServiceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_bind_phone_next);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BindPhoneNextActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
        timeTask = new TimeTask(60 * 1000);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(BindPhoneNextActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
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
        setTitle("绑定新手机");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_new_phone = (EditText) findViewById(R.id.et_new_phone);
        et_new_phone_code = (EditText) findViewById(R.id.et_new_phone_code);
        tv_new_send_code = (TextView) findViewById(R.id.tv_new_send_code);
        tv_new_send_code.setOnClickListener(this);//发送新的手机号验证码
        findViewById(R.id.bt_bind_phone_next).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        newPhone = et_new_phone.getText().toString();
        newCode = et_new_phone_code.getText().toString();
        Pattern p = Pattern.compile("[1][123456789]\\d{9}");
        Matcher m = p.matcher(newPhone);
        switch (v.getId()) {
            case R.id.tv_new_send_code:
                if (TextUtils.isEmpty(newPhone)) {
                    ToastUtils.show(BindPhoneNextActivity.this, "请输入新手机号");
                    return;
                }
                if (!m.matches()) {//手机格式正确
                    Toast.makeText(BindPhoneNextActivity.this, "请检查手机号是否正确", 0).show();
                    return;
                }
                phoneNumIsRegister(newPhone, "3");
                break;
            case R.id.bt_bind_phone_next:
                onEvent(BindPhoneNextActivity.this, "304003_phone_sure_btn_click");
                if (TextUtils.isEmpty(newPhone)) {
                    ToastUtils.show(BindPhoneNextActivity.this, "请输入新手机号");
                    return;
                }

                if (!m.matches()) {//手机格式正确
                    Toast.makeText(BindPhoneNextActivity.this, "请检查手机号是否正确", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(newCode)) {
                    ToastUtils.show(BindPhoneNextActivity.this, "请输入您的短信验证码");
                    return;
                }
                String token = LoginUserProvider.getUser(this).getToken();
                verifySmsCode(newPhone, newCode, "3", token);
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

            tv_new_send_code.setText("重新发送");
            tv_new_send_code.setTextColor(getResources().getColor(R.color.text_a11c3f));
            tv_new_send_code.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_new_send_code.setText(millisUntilFinished / 1000 + "s后重新发送");
            tv_new_send_code.setTextColor(getResources().getColor(R.color.text_989898));
            tv_new_send_code.setClickable(false);
        }
    }

    /**
     * 发送验证码
     */
    public void sendVerity(String mobilePhone, String type, String ip, String from, String token) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneNextActivity.this));
        String url = Urls.SEND_CODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("type", type);
        params.put("ip", ip);
        params.put("from", from);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//发送成功
                        Toast.makeText(BindPhoneNextActivity.this, jsonObject.getString("message"), 0).show();
                        timeTask.start();
                    } else if (jsonObject.getString("state").equals("2")) {//发送失败
                        Toast.makeText(BindPhoneNextActivity.this, jsonObject.getString("message"), 0).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindPhoneNextActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindPhoneNextActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindPhoneNextActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneNextActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BindPhoneNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindPhoneNextActivity.this, "解析异常", 0).show();
                    CustomProgress.CustomDismis();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BindPhoneNextActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    //校验验证码是否正确
    private void verifySmsCode(String mobilePhone, String smsCode, String from, String token) {
        CustomProgress.show(BindPhoneNextActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneNextActivity.this));
        String url = Urls.VERIFYSMSCODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("smsCode", smsCode);
        params.put("from", from);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//校验成功
                        updateUserPhone(LoginUserProvider.getUser(BindPhoneNextActivity.this).getToken(), "3", newPhone);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindPhoneNextActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindPhoneNextActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindPhoneNextActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneNextActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(BindPhoneNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(BindPhoneNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BindPhoneNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //验证原手机号是否注册过
    private void phoneNumIsRegister(String mobilePhone, String from) {
        CustomProgress.show(BindPhoneNextActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneNextActivity.this));
        String url = Urls.CHECKMOBILEPHONEISREGISTERED;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//已注册
                        CustomProgress.CustomDismis();
                        Toast.makeText(BindPhoneNextActivity.this, "该手机号已注册", 0).show();
                    } else if (jsonObject.getString("state").equals("5")) {//未注册
                        sendVerity(newPhone, "8", ip, "3", LoginUserProvider.getUser(BindPhoneNextActivity.this).getToken());
                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(BindPhoneNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(BindPhoneNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BindPhoneNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //修改手机号提交
    private void updateUserPhone(String token, String from, String newphone) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindPhoneNextActivity.this));
        String url = Urls.UPDATEUSERPHONE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("newphone", newphone);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//修改手机号成功
                        dialog();

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindPhoneNextActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindPhoneNextActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindPhoneNextActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(BindPhoneNextActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BindPhoneNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindPhoneNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BindPhoneNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

    // 修改成功的对话框
    private void dialog() {

        mdialog = new Dialog(BindPhoneNextActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dialog_setting);

        TextView tv_dialog_text = (TextView) mdialog.findViewById(R.id.tv_dialog_text);
        tv_dialog_text.setText("恭喜您   更换手机号码成功！");
        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
        iv_dialog_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mdialog.dismiss();
                BindPhoneActivity.mContext.finish();
                finish();
                //更换完手机号，退出登录
                DoCacheUtil util = DoCacheUtil.get(BindPhoneNextActivity.this);
                util.put("isLogin", "");
                Intent exitIntent = new Intent(BindPhoneNextActivity.this, MainActivity.class);
                //exitIntent.putExtra("overtime", "7");//1非超时
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);

            }
        });
        mdialog.show();
    }

    // 联系客服对话框
    private void ContactServiceDialog() {

        contactServiceDialog = new Dialog(BindPhoneNextActivity.this, R.style.MyDialog);
        contactServiceDialog.setContentView(R.layout.dialog_contact_service_phone);

        TextView tv_cancel = (TextView) contactServiceDialog.findViewById(R.id.tv_cancel);
        TextView tv_call = (TextView) contactServiceDialog.findViewById(R.id.tv_call);
        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                contactServiceDialog.dismiss();
            }
        });
        tv_call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Intent.ACTION_CALL);
                mIntent.setData(Uri.parse("tel:4006669068"));
                startActivity(mIntent);
                contactServiceDialog.dismiss();
            }
        });
        contactServiceDialog.show();
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
