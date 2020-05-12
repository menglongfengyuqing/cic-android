package com.ztmg.cicmorgan.account.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CodeView;
import com.ztmg.cicmorgan.view.CustomProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录密码
 *
 * @author pc
 */
public class LoginPasActivity extends BaseActivity implements OnClickListener {
    private EditText et_old_pas, et_new_pas, et_before_phone_code;
    private ImageView iv_showCode;
    private String ip;
    private String phone;
    private Dialog mdialog;
    private String key;
    private String pictureCode;
    private Button iv_eyes_no;
    private boolean isShowPwd;//是否显示密码
    private TextView tv_before_send_code;
    private TimeTask timeTask;
    private String oldPas;//原密码
    @BindView(R.id.rl_eyes)
    RelativeLayout rl_eyes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(LoginPasActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_login_pas);
        ButterKnife.bind(this);
        initView();
        initData();
        timeTask = new TimeTask(60 * 1000);
        phone = LoginUserProvider.getUser(LoginPasActivity.this).getPhone().toString();
        // getPictureCode("3", iv_showCode);
    }

    @Override
    protected void initView() {
        et_old_pas = (EditText) findViewById(R.id.et_old_pas);
        et_new_pas = (EditText) findViewById(R.id.et_new_pas);
        et_before_phone_code = (EditText) findViewById(R.id.et_before_phone_code);
        tv_before_send_code = (TextView) findViewById(R.id.tv_before_send_code);
        tv_before_send_code.setOnClickListener(this);//发送原来手机号验证码

        //iv_showCode = (ImageView) findViewById(R.id.iv_showCode);
        //iv_showCode.setOnClickListener(this);
        iv_eyes_no = (Button) findViewById(R.id.iv_eyes_no);
        //findViewById(R.id.rl_eyes).setOnClickListener(this);
        findViewById(R.id.bt_modify_pwd).setOnClickListener(this);
        setTitle("修改密码");
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

    @OnClick({R.id.rl_eyes})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.iv_showCode:
            //getPictureCode("3", iv_showCode);
            //break;
            case R.id.tv_before_send_code:
                // String beforePhone = tv_before_phone_num.getText().toString();
                String phone = LoginUserProvider.getUser(LoginPasActivity.this).getPhone();
                sendVerity(phone, "8", ip, "3", LoginUserProvider.getUser(LoginPasActivity.this).getToken());
                break;
            case R.id.bt_modify_pwd:
                oldPas = et_old_pas.getText().toString().trim();
                String newPas = et_new_pas.getText().toString().trim();
                String code = et_before_phone_code.getText().toString().trim();
                if (TextUtils.isEmpty(oldPas)) {
                    //Toast.makeText(LoginPasActivity.this, "旧密码不能为空", 0).show();
                    ToastUtils.show(LoginPasActivity.this, "旧密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(newPas)) {
                    //Toast.makeText(LoginPasActivity.this, "新密码不能为空", 0).show();
                    ToastUtils.show(LoginPasActivity.this, "新密码不能为空");
                    return;
                }
                if (!PwdUtil.isPWD(newPas)) {
                    //Toast.makeText(LoginPasActivity.this, "密码格式为6-16位数字加字母", 0).show();
                    ToastUtils.show(LoginPasActivity.this, "密码格式为6-16位数字加字母");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    //Toast.makeText(LoginPasActivity.this, "验证码不能为空", 0).show();
                    ToastUtils.show(LoginPasActivity.this, "短信验证码不能为空");
                    return;
                }
                //                if (!pictureCode.equals(phone_code)) {
                //                    ToastUtils.show(LoginPasActivity.this, "请输入正确的验证码");
                //                    return;
                //                }
                if (newPas.equals(oldPas) || newPas == oldPas) {
                    // Toast.makeText(LoginPasActivity.this, "新密码不能与旧密码相同", 0).show();
                    ToastUtils.show(LoginPasActivity.this, "新密码不能与旧密码相同");
                    return;
                }
                //判断验证码是否正确
                verifySmsCode("", code, "3");
                break;
            case R.id.rl_eyes://显示密码
                if (isShowPwd) {
                    et_new_pas.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPwd = false;
                    iv_eyes_no.setBackgroundResource(R.drawable.pic_pas);
                    et_new_pas.setSelection(et_new_pas.getText().toString().length());
                } else {
                    et_new_pas.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPwd = true;
                    iv_eyes_no.setBackgroundResource(R.drawable.pic_no_pas);
                    et_new_pas.setSelection(et_new_pas.getText().toString().length());
                }
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(LoginPasActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 获取图形验证码
     */
    public void getPictureCode(String from, final ImageView iv_showCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginPasActivity.this));
        String url = Urls.GETPICTURECODE;
        RequestParams params = new RequestParams();
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//发送成功
                        pictureCode = jsonObject.getString("pictureCode");
                        key = jsonObject.getString("key");
                        iv_showCode.setImageBitmap(CodeView.getInstance().createBitmap(pictureCode));
                    } else {
                        Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginPasActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //原密码是否正确
    private void checkOldPwd(String token, String from, String pwd) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginPasActivity.this));
        String url = Urls.NEWCHECKOLDPWD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        String newPwd = et_new_pas.getText().toString().trim();
                        modifyLoginPas(LoginUserProvider.getUser(LoginPasActivity.this).getToken(), "3", newPwd);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(LoginPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(LoginPasActivity.this);
                        //LoginUserProvider.cleanDetailData(LoginPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(LoginPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginPasActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    //修改登录密码
    private void modifyLoginPas(String token, String from, String pwd) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.NEWUPDATEUSERPWD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//修改密码成功
                        Toast.makeText(LoginPasActivity.this, "修改密码成功", 0).show();
                        dialog();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(LoginPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(LoginPasActivity.this);
                        //LoginUserProvider.cleanDetailData(LoginPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginPasActivity.this, "请检查网络", 0).show();
            }
        });
    }

    // 修改成功的对话框
    private void dialog() {

        mdialog = new Dialog(LoginPasActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dialog_setting);

        TextView tv_dialog_text = (TextView) mdialog.findViewById(R.id.tv_dialog_text);
        tv_dialog_text.setText("恭喜您   密码重置成功！");
        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
        iv_dialog_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mdialog.dismiss();
                //					finish();
                LoginUserProvider.cleanData(LoginPasActivity.this);
                LoginUserProvider.cleanDetailData(LoginPasActivity.this);
                DoCacheUtil util = DoCacheUtil.get(LoginPasActivity.this);
                util.put("isLogin", "");
                Intent exitIntent = new Intent(LoginPasActivity.this,
                        LoginActivity.class);
                exitIntent.putExtra("overtime", "8");//1非超时
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
            }
        });
        mdialog.show();
    }

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
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
        params.put("ip", ip);
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
                        //Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                        ToastUtils.show(LoginPasActivity.this, jsonObject.getString("message"));
                        timeTask.start();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(LoginPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginPasActivity.this);
                        util.put("isLogin", "");

                    } else if (jsonObject.getString("state").equals("2")) {//发送失败
                        //Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                        ToastUtils.show(LoginPasActivity.this, jsonObject.getString("message"));
                    } else {
                        //Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                        ToastUtils.show(LoginPasActivity.this, jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(LoginPasActivity.this, "解析异常", 0).show();
                    ToastUtils.show(LoginPasActivity.this, "解析异常");
                    CustomProgress.CustomDismis();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(LoginPasActivity.this, "请检查网络", 0).show();
                ToastUtils.show(LoginPasActivity.this, "请检查网络");
                CustomProgress.CustomDismis();
            }
        });
    }

    //校验验证码是否正确
    private void verifySmsCode(String mobilePhone, String smsCode, String from) {
        CustomProgress.show(LoginPasActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.VERIFYSMSCODE;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", LoginUserProvider.getUser(LoginPasActivity.this).getPhone());
        params.put("smsCode", smsCode);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//校验成功
                        //Intent intent = new Intent(LoginPasActivity.this, BindPhoneNextActivity.class);
                        //startActivity(intent);
                        //判断旧密码是否正确
                        checkOldPwd(LoginUserProvider.getUser(LoginPasActivity.this).getToken(), "3", oldPas);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(LoginPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(LoginPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(LoginPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginPasActivity.this, "请检查网络", 0).show();
            }
        });
    }
}
