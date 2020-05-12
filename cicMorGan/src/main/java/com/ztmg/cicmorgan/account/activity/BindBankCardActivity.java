package com.ztmg.cicmorgan.account.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserAccountInfo;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.BaseHelper;
import com.ztmg.cicmorgan.pay.util.Constants;
import com.ztmg.cicmorgan.pay.util.MobileSecurePayer;
import com.ztmg.cicmorgan.pay.util.PayOrder;
import com.ztmg.cicmorgan.util.AndroidUtil;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.IdCardUtils;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 绑定银行卡
 *
 * @author pc
 */
public class BindBankCardActivity extends BaseActivity implements OnClickListener {
    private EditText et_user_name, et_user_id, et_bank_num;
    private Button bt_bank_card;
    private Dialog mdialog;
    private String money_order;
    private String oid_partner;
    private String risk_item;
    private String user_id;
    private String id_no;
    private String acct_name;
    private String card_no;
    private String no_order;
    private String dt_order, busi_partner, name_goods, valid_order;
    private String token;
    private LinearLayout ll_text;
    //	private LinearLayout ll_bank_name;
    private TextView tv_bank_name;
    private EditText et_bank_num1;
    private String isBindBank;
    //	private LinearLayout ll_bank_num;
    private static BindBankCardActivity mContext;
    private String isBackAccount;
    private String notify_url;
    private String sign, sign_type;
    private String info_order, id_type;
    private EditText et_bank_num_phone;
    private LinearLayout ll_bank_num_phone;
    private String realName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BindBankCardActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_bind_bank_card);
        mContext = this;
        Intent intent = getIntent();
        isBackAccount = intent.getStringExtra("isBackAccount");//1,显示返回账户中心，0，不显示
        getUserInfo(LoginUserProvider.getUser(BindBankCardActivity.this).getToken().toString(),"3");

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(BindBankCardActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
        //initData();
    }

    public static BindBankCardActivity getInstance() {
        return mContext;
    }

    @Override
    protected void initView() {
        if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("2")){ //1未开户  2已开户
            if(LoginUserProvider.getUser(BindBankCardActivity.this).getIsBindBank().equals("1")){//1未绑定 2已绑定
                setTitle("绑定银行卡");
            }else if(LoginUserProvider.getUser(BindBankCardActivity.this).getIsBindBank().equals("2")){
                setTitle("更换银行卡");
            }
        }else if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("1")){
            setTitle("开户信息");
        }
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(BindBankCardActivity.this, "305001_yhcg_back_click");
                finish();
            }
        });

        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_id = (EditText) findViewById(R.id.et_user_id);

        if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("2")){ //1未开户  2已开户
                et_user_id.setText(LoginUserProvider.getUser(BindBankCardActivity.this).getIdCard());
                et_user_id.setEnabled(false);//去掉点击时编辑框下面横线:
                et_user_id.setFocusable(false);//不可编辑
                et_user_id.setFocusableInTouchMode(false);//不可编辑

            et_user_name.setText(LoginUserProvider.getUser(BindBankCardActivity.this).getRealName());
            et_user_name.setEnabled(false);//去掉点击时编辑框下面横线:
            et_user_name.setFocusable(false);//不可编辑
            et_user_name.setFocusableInTouchMode(false);//不可编辑
        }
        //		ll_bank_num = (LinearLayout) findViewById(R.id.ll_bank_num);
        //		ll_bank_name = (LinearLayout) findViewById(R.id.ll_bank_name);//显示银行卡名字
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        ll_bank_num_phone = (LinearLayout) findViewById(R.id.ll_bank_num_phone);
        et_bank_num_phone = (EditText) findViewById(R.id.et_bank_num_phone);//预留手机号
        et_bank_num = (EditText) findViewById(R.id.et_bank_num);
        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        bt_bank_card = (Button) findViewById(R.id.bt_bank_card);
        bt_bank_card.setOnClickListener(this);
        findViewById(R.id.tv_tip).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String isBindBankCard = LoginUserProvider.getUser(BindBankCardActivity.this).getIsBindBank();
        token = LoginUserProvider.getUser(BindBankCardActivity.this).getToken().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }


    @Override
    public void onClick(View v) {
        if (AndroidUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.bt_bank_card://绑定银行卡
                onEvent(BindBankCardActivity.this, "305002_yhcg_next_btn_click");
                isBindBank = "0";//绑定银行卡
                String userName = et_user_name.getText().toString();
                String userId = et_user_id.getText().toString();

                String bankNum = et_bank_num.getText().toString();
                String bankNumPhone = et_bank_num_phone.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(BindBankCardActivity.this, "姓名不能为空", 0).show();
                    return;
                }
               if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("1")){
                    //开户
                    if (TextUtils.isEmpty(userId)) {
                        Toast.makeText(BindBankCardActivity.this, "身份证号不能为空", 0).show();
                        return;
                    } else {
                        try {
                            if (!IdCardUtils.IDCardValidate(userId.trim()).equals("有效") || IdCardUtils.IDCardValidate(userId.trim()) != "有效") {
                                Toast.makeText(BindBankCardActivity.this, IdCardUtils.IDCardValidate(userId.trim()), 0).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(BindBankCardActivity.this, "银行卡号不能为空", 0).show();
                    return;
                } else if (!PwdUtil.isCard(bankNum)) {
                    Toast.makeText(BindBankCardActivity.this, "银行卡号格式不正确", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(bankNumPhone)) {
                    Toast.makeText(BindBankCardActivity.this, "银行预留手机号不能为空", 0).show();
                    return;
                }

                if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("2")){ //1未开户  2已开户
                    if(LoginUserProvider.getUser(BindBankCardActivity.this).getIsBindBank().equals("1")){//1未绑定 2已绑定
                        setTitle("绑定银行卡");
                    }else{
                        setTitle("更换银行卡");
                    }
                    //绑定和更换银行卡
                    OpenAccount(LoginUserProvider.getUser(BindBankCardActivity.this).getToken().toString(), bankNum, "3", userId, userName, bankNumPhone, "01",Urls.CHANGEBANKCARD);
                }else if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("1")){
                    //开户
                    OpenAccount(LoginUserProvider.getUser(BindBankCardActivity.this).getToken().toString(), bankNum, "3", userId, userName, bankNumPhone, "01",Urls.ACCOUNTCREATEH5);//01-投资  02-借款
                }


                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(BindBankCardActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            default:
                break;
        }
    }
    //开户
    private void OpenAccount(String token, String bankCardNo, String from, String certNo, String realName, String bankCardPhone, String bizType,String url) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindBankCardActivity.this));
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("bankCardNo", bankCardNo);
        params.put("from", from);
        params.put("certNo", certNo);
        params.put("realName", realName);
        params.put("bankCardPhone", bankCardPhone);
        params.put("bizType", bizType);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.get("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String reqData = dataObj.getString("reqData");
                        String platformNo = dataObj.getString("platformNo");
                        String sign = dataObj.getString("sign");
                        String keySerial = dataObj.getString("keySerial");
                        String serviceName = dataObj.getString("serviceName");
                        Intent safetyIntent = new Intent(BindBankCardActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("reqData", reqData);
                        safetyIntent.putExtra("platformNo", platformNo);
                        safetyIntent.putExtra("sign", sign);
                        safetyIntent.putExtra("keySerial", keySerial);
                        safetyIntent.putExtra("serviceName", serviceName);
                        safetyIntent.putExtra("isBackAccount", isBackAccount);
                        if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("2")){ //1未开户  2已开户
                            if(LoginUserProvider.getUser(BindBankCardActivity.this).getIsBindBank().equals("1")){//1未绑定 2已绑定
                                safetyIntent.putExtra("title", "绑定银行卡");
                            }else{
                                safetyIntent.putExtra("title", "更换银行卡");
                            }
                        }else if(LoginUserProvider.getUser(BindBankCardActivity.this).getCertificateChecked().equals("1")){
                            safetyIntent.putExtra("title", "开户信息");
                        }
                        startActivity(safetyIntent);
                        finish();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindBankCardActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindBankCardActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindBankCardActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(BindBankCardActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BindBankCardActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindBankCardActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BindBankCardActivity.this, "请检查网络", 0).show();
            }
        });
    }
    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindBankCardActivity.this));
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
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(BindBankCardActivity.this);
                        initView();
                        //						Intent intent = new Intent(BindBankCardActivity.this,BindBankSuccessActivity.class);//绑卡成功
                        //						intent.putExtra("isBackAccount", isBackAccount);
                        //						startActivity(intent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BindBankCardActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BindBankCardActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BindBankCardActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(BindBankCardActivity.this);
                        //						LoginUserProvider.cleanDetailData(BindBankCardActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BindBankCardActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BindBankCardActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindBankCardActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BindBankCardActivity.this, "请检查网络", 0).show();
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
