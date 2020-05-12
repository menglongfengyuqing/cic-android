package com.ztmg.cicmorgan.login;

import android.content.Intent;
import android.os.Build;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CodeView;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 找回密码
 *
 * @author pc
 */
public class ForgetPasActivity extends BaseActivity implements OnClickListener {
    private EditText et_code;
    private TextView tv_phone;
    private ImageView iv_showCode;
    private String phone;
    private String key;
    private String pictureCode;
    public static ForgetPasActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(ForgetPasActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_forget_pas);
        mContext = this;
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        initView();
        initData();
        getPictureCode("3", iv_showCode);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(ForgetPasActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    public static ForgetPasActivity getInstance() {
        return mContext;
    }

    @Override
    protected void initView() {
        setTitle("找回密码");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(ForgetPasActivity.this, "216001_zhmm_back_click");
                finish();
            }
        });
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(phone);
        et_code = (EditText) findViewById(R.id.et_code);
        iv_showCode = (ImageView) findViewById(R.id.iv_showCode);
        iv_showCode.setOnClickListener(this);
        findViewById(R.id.bt_forget).setOnClickListener(this);
        //findViewById(R.id.tv_tip).setOnClickListener(this);
    }

    @Override
    protected void initData() {

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
    public void onClick(View v) {
        phone = tv_phone.getText().toString();
        String code = et_code.getText().toString();
        switch (v.getId()) {
            case R.id.bt_forget:
                onEvent(ForgetPasActivity.this, "216002_zhmm_xyb_click");
                if (TextUtils.isEmpty(phone)) {
                    //ToastUtils.show(ForgetPasActivity.this, "请输入您绑定的手机号");
                    new CustomToastUtils(this, "手机号不能为空").show();
                    return;
                }
                Pattern p = Pattern.compile("[1][123456789]\\d{9}");
                Matcher m = p.matcher(phone);
                if (!m.matches()) {//手机格式正确
                    //Toast.makeText(ForgetPasActivity.this, "请检查手机号是否正确", 0).show();
                    new CustomToastUtils(this, "请输入正确的手机号").show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    //Toast.makeText(ForgetPasActivity.this, "请输入验证码", 0).show();
                    new CustomToastUtils(this, "验证码不能为空").show();
                    return;
                }
                if (!pictureCode.equals(code)) {
                    //ToastUtils.show(ForgetPasActivity.this, "请输入正确的验证码");
                    new CustomToastUtils(this, "请输入正确的验证码").show();
                    return;
                }
                ifRegister(phone, "3");
                break;
            case R.id.iv_showCode:
                //图形验证码
                getPictureCode("3", iv_showCode);
                break;
            //            case R.id.tv_tip:
            //                Intent riskTipFirstIntent = new Intent(ForgetPasActivity.this, AgreementActivity.class);
            //                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
            //                riskTipFirstIntent.putExtra("title", "风险提示书");
            //                startActivity(riskTipFirstIntent);
            //                break;
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
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ForgetPasActivity.this));
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
                        //Toast.makeText(ForgetPasActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(ForgetPasActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //判断是否注册过
    private void ifRegister(String mobilePhone, String from) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ForgetPasActivity.this));
        String url = Urls.CHECKMOBILEPHONEISREGISTERED;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//已注册
                        Intent intent = new Intent(ForgetPasActivity.this, ForgetPasNextActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("pictureCode", pictureCode);
                        intent.putExtra("key", key);
                        startActivity(intent);
                    } else if (jsonObject.getString("state").equals("5")) {//未注册
                        //ToastUtils.show(ForgetPasActivity.this, "您还未注册请先注册");
                        new CustomToastUtils(ForgetPasActivity.this, "用户名输入错误").show();
                    } else {
                        //Toast.makeText(ForgetPasActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasActivity.this, jsonObject.getString("message")).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(ForgetPasActivity.this, "请检查网络", 0).show();
            }
        });

    }

    //找回密码
    private void forgetPassword(String from, String name, String pwd) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ForgetPasActivity.this));
        String url = Urls.NEWFORGETPASSWORD;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("name", name);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String token = object.getString("token");
                        //Toast.makeText(ForgetPasActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasActivity.this, jsonObject.getString("message")).show();
                        finish();
                    } else {
                        //Toast.makeText(ForgetPasActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(ForgetPasActivity.this, jsonObject.getString("message")).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ForgetPasActivity.this, "请检查网络", 0).show();
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
