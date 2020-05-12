package com.ztmg.cicmorgan.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ztmg.cicmorgan.net.NetManger;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.ClearEditText;
import com.ztmg.cicmorgan.view.CodeView;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.UtilsView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 注册
 *
 * @author pc
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

    private ClearEditText tv_phone, et_password;
    private ClearEditText et_code, et_request_text;
    private AsyncHttpClient client;
    private String phone;
    private String password;
    private String overtime;
    private TextView ll_agreeage;
    private Button iv_eyes_no;
    private boolean isShowPwd;//是否显示密码
    public static RegisterActivity instance;
    private String recommendMobilePhone;
    private String key;
    private Dialog mdialog;
    private String pictureCode;
    private ImageView iv_showCode;
    private String isFinshActivity;
    private Button bt_login;
    @BindView(R.id.lin_content)
    LinearLayout lin_content;
    @BindView(R.id.line)
    LinearLayout line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        instance = this;
        client = NetManger.getAsyncHttpClient();
        Intent intent = getIntent();
        overtime = intent.getStringExtra("overtime");
        isFinshActivity = overtime;
        if (overtime.equals("5")) {//立即投资，无论登录返回还是登录成功都把当前界面finish
            overtime = "0";
        }
        if (overtime.equals("6")) {//在mainActivity中点击账户，无论登录返回还是登录成功都把当前界面finish
            overtime = "0";
        }
        if (overtime.equals("7")) {//退出，注册
            overtime = "0";
        }
        initView();
        initData();
        getPictureCode("3", iv_showCode);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(RegisterActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


    }


    @Override
    protected void initView() {
        setTitle("注册");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(RegisterActivity.this, "214001_zhuce_back_click");
                finish();
            }
        });
        tv_phone = (ClearEditText) findViewById(R.id.tv_phone);
        tv_phone.addTextChangedListener(new MyEditText());
        et_password = (ClearEditText) findViewById(R.id.et_password);
        et_password.addTextChangedListener(new MyEditText());
        //tv_phone.setText(phone);
        et_request_text = (ClearEditText) findViewById(R.id.et_request_text);//邀请码
        et_code = (ClearEditText) findViewById(R.id.et_code);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
        bt_login.setEnabled(false);
        ll_agreeage = (TextView) findViewById(R.id.ll_agreeage);
        ll_agreeage.setOnClickListener(this);
        iv_eyes_no = (Button) findViewById(R.id.iv_eyes_no);
        iv_eyes_no.setOnClickListener(this);
        iv_showCode = (ImageView) findViewById(R.id.iv_showCode);//验证码
        iv_showCode.setOnClickListener(this);
        findViewById(R.id.tv_login).setOnClickListener(this);//登录

        //findViewById(R.id.tv_tip).setOnClickListener(this);

        //keepLoginBtnNotOver(lin_content, line);
        //触摸外部，键盘消失
        //        lin_content.setOnTouchListener(new View.OnTouchListener() {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                UtilsView.closeKeyboard(RegisterActivity.this);
        //                return false;
        //            }
        //        });
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
            if (tv_phone.getText().toString().length() > 0 && et_password.getText().toString().length() > 0) {
                bt_login.setEnabled(true);
                bt_login.setBackgroundResource(R.drawable.bt_red);
            } else {
                bt_login.setEnabled(false);
                bt_login.setBackgroundResource(R.drawable.bt_gray_36);
            }
        }
    }

    /**
     * 保持登录按钮始终不会被覆盖
     *
     * @param root
     * @param subView
     */
    private void keepLoginBtnNotOver(final View root, final View subView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                // 获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                // 获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                // 若不可视区域高度大于200，则键盘显示,其实相当于键盘的高度
                if (rootInvisibleHeight > 200) {
                    // 显示键盘时
                    int srollHeight = rootInvisibleHeight - (root.getHeight() - subView.getHeight()) - UtilsView.getNavigationBarHeight(root.getContext());
                    if (srollHeight > 0) {//当键盘高度覆盖按钮时
                        root.scrollTo(0, srollHeight);
                    }
                } else {
                    // 隐藏键盘时
                    root.scrollTo(0, 0);
                }
            }
        });
    }


    @Override
    protected void initData() {


    }

    @Override
    public void onClick(View v) {
        phone = tv_phone.getText().toString().trim();
        String code = et_code.getText().toString();
        password = et_password.getText().toString();
        switch (v.getId()) {
            case R.id.bt_login:
                onEvent(RegisterActivity.this, "214002_zhuce_tyyhxybzc_click");
                if (TextUtils.isEmpty(phone)) {
                    new CustomToastUtils(this, "手机号不能为空").show();
                    //Toast.makeText(RegisterActivity.this, "手机号不能为空", 0).show();
                    return;
                }
                Pattern p = Pattern.compile("[1][123456789]\\d{9}");
                Matcher m = p.matcher(phone);
                if (!m.matches()) {//手机格式正确
                    new CustomToastUtils(this, "请输入正确的手机号").show();
                    //Toast.makeText(RegisterActivity.this, "请输入正确的手机号", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    new CustomToastUtils(this, "密码不能为空").show();
                    //Toast.makeText(RegisterActivity.this, "密码不能为空", 0).show();
                    return;
                }
                if (!PwdUtil.isPWD(password)) {
                    new CustomToastUtils(this, "请输入数字和字母的组合，6位及以上的密码").show();
                    //Toast.makeText(RegisterActivity.this, "请输入数字和字母的组合，6位及以上的密码", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    new CustomToastUtils(this, "验证码不能为空").show();
                    //Toast.makeText(RegisterActivity.this, "验证码不能为空", 0).show();
                    return;
                }
                if (!pictureCode.equals(code)) {
                    new CustomToastUtils(this, "请输入正确的验证码").show();
                    //ToastUtils.show(RegisterActivity.this, "请输入正确的验证码");
                    return;
                }
                if (phone.equals(et_request_text.getText().toString())) {
                    new CustomToastUtils(this, "邀请人不能为本人").show();
                    return;
                }
                recommendMobilePhone = et_request_text.getText().toString();//邀请码
                Matcher mRequest = p.matcher(recommendMobilePhone);
                if (!recommendMobilePhone.equals("") && !mRequest.matches()) {//手机格式正确
                    new CustomToastUtils(this, "请检查推荐人手机号是否正确").show();
                    //Toast.makeText(RegisterActivity.this, "请检查推荐人手机号是否正确", 0).show();
                    return;
                }
                ifRegister(phone, "3");
                break;
            case R.id.iv_showCode:
                getPictureCode("3", iv_showCode);
                break;
            case R.id.ll_agreeage:
                onEvent(RegisterActivity.this, "214003_zhuce_ckyhxy_click");
                Intent agreeageIntent = new Intent(RegisterActivity.this, AgreementActivity.class);
                agreeageIntent.putExtra("title", "注册协议");
                agreeageIntent.putExtra("path", Urls.LOGINAGREEMENT);
                startActivity(agreeageIntent);
                break;
            case R.id.iv_eyes_no://显示密码
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
            case R.id.tv_login:
                onEvent(RegisterActivity.this, "214004_zhuce_yyzhzjdl_click");
                if (isFinshActivity.equals("6")) {//首页点击新手注册，点击登录账号
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("overtime", isFinshActivity);//无论登录界面返回还是登录成功，都是finish当前界面
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
                break;
            //            case R.id.tv_tip:
            //                Intent riskTipFirstIntent = new Intent(RegisterActivity.this, AgreementActivity.class);
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
        CustomProgress.show(RegisterActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RegisterActivity.this));
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
                        Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(RegisterActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //判断是否注册过
    private void ifRegister(String mobilePhone, String from) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RegisterActivity.this));
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
                        ToastUtils.show(RegisterActivity.this, "该手机号已注册");
                    } else if (jsonObject.getString("state").equals("5")) {//未注册
                        Intent intent = new Intent(RegisterActivity.this, SendCodeActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        intent.putExtra("recommendMobilePhone", recommendMobilePhone);
                        intent.putExtra("key", key);
                        intent.putExtra("pictureCode", pictureCode);
                        intent.putExtra("overtime", overtime);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(RegisterActivity.this, "请检查网络", 0).show();
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
}
