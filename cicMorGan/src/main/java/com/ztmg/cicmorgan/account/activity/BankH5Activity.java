package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.OnceInvestmentActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 银行存管H5界面
 *
 * @author pc
 */
public class BankH5Activity extends BaseActivity {
    private WebView wv_safety;
    private WebSettings ws;
    private String tm, data, merchantId;
    private String reqData,platformNo,sign,keySerial,serviceName;
    private String isBackAccount;
    private String title;
    private String urlParameter = "";
    private String startedUrl;
    private String time = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_bank_h5);
        Intent intent = getIntent();
//        tm = intent.getStringExtra("tm");
//        data = intent.getStringExtra("data");
//        merchantId = intent.getStringExtra("merchantId");
        reqData = intent.getStringExtra("reqData");
        platformNo = intent.getStringExtra("platformNo");
        sign = intent.getStringExtra("sign");
        keySerial = intent.getStringExtra("keySerial");
        serviceName = intent.getStringExtra("serviceName");
        isBackAccount = intent.getStringExtra("isBackAccount");
        title = intent.getStringExtra("title");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BankH5Activity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
//        urlParameter = "?data=" + data + "&tm=" + tm + "&merchantId=" + merchantId;
//        urlParameter = "keySerial=" + keySerial + "&serviceName=" + serviceName+ "&reqData=" + reqData + "&sign=" + sign+ "&platformNo=" + platformNo;
        try {
            urlParameter = "keySerial=" + keySerial + "&serviceName=" + serviceName+ "&reqData=" + reqData + "&sign=" + URLEncoder.encode(sign,"UTF-8")+ "&platformNo=" + platformNo;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//            urlParameter = "keySerial=" + keySerial + "&serviceName=" + serviceName+ "&reqData=" + reqData + "&sign=" + sign.replace("+","%2B")+ "&platformNo=" + platformNo;
        initView();
        initData();
    }

    @Override
    protected void initView() {

        setTitle(title);
        setBack(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wv_safety = (WebView) findViewById(R.id.wv_safety);
        ws = wv_safety.getSettings();
        wv_safety.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        ws.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        ws.setSupportZoom(true);//是否可以缩放，默认true
        ws.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        ws.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        ws.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        ws.setAppCacheEnabled(true);//是否使用缓存
        ws.setDomStorageEnabled(true);//开启本地DOM存储
        ws.setLoadsImagesAutomatically(true); // 加载图片
        ws.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放

        wv_safety.setWebChromeClient(new WebChromeClient() {
            //刚加载完页面时，进度发生变化时，调用此方法
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        wv_safety.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wv_safety.loadUrl(url);
                if(startedUrl!=null){
                    if(startedUrl.contains("personalRegisterExpand/register")||startedUrl.contains("personalBindBankcardExpand/bindBankcardSms")||startedUrl.contains("unbindBankcard/unbind")) {
                        if (!TextUtils.isEmpty(isBackAccount) && isBackAccount != null) {//开户
                            getUserInfo(LoginUserProvider.getUser(BankH5Activity.this).getToken(), "3");
                        }
                    }else if (startedUrl.contains("recharge/rechargeSwift.do")) {
                        if (RechargeActivity.mContext != null) {//充值
                            finish();
                            RechargeActivity.mContext.finish();
                        }
                    }else if (startedUrl.contains("withdraw/withdraw")) {
                        if (WithdrawCashActivity.mContext != null) {//提现
                            finish();
                            WithdrawCashActivity.mContext.finish();
                        }
                    }else if(startedUrl.contains("modifyMobileExpand/sms")){//更改手机号
                        finish();
                    }else if(startedUrl.contains("activateStockedUser/activatePersonalUser")){//1.0版本用户激活
                        LoginUserProvider.cleanData(BankH5Activity.this);
                        LoginUserProvider.cleanDetailData(BankH5Activity.this);
                        DoCacheUtil util = DoCacheUtil.get(BankH5Activity.this);
                        util.put("isLogin", "");
                        finish();
                    }else if(startedUrl.contains("orderId")){//出借
                        Intent intent = new Intent(BankH5Activity.this, PromptActivity.class);
                        String s = url.substring(url.indexOf("orderId=") + 1);
                        String orderId = s.substring(s.indexOf("=") + 1);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                        if (OnceInvestmentActivity.getInstance() != null) {//充值界面完成后，返回到详情页面
                            OnceInvestmentActivity.getInstance().finish();
                        }
                        finish();
                    }
                }

//else {
//                    if (BindBankCardActivity.getInstance() != null) {
//                        BindBankCardActivity.getInstance().finish();
//                    }
//                    finish();
//                    if (RechargeActivity.getInstance() != null) {//充值界面完成后，返回到我的界面
//                        RechargeActivity.getInstance().finish();
//                    }
//                    if (OnceInvestmentActivity.getInstance() != null) {//充值界面完成后，返回到详情页面
//                        OnceInvestmentActivity.getInstance().finish();
//                    }
//                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //				CustomProgress.show(BankH5Activity.this);

                if(url.contains("recharge/rechargeSwift.do")||url.contains("withdraw/withdraw")||url.contains("modifyMobileExpand/sms")||url.contains("unbindBankcard/unbind")||url.contains("personalRegisterExpand/register")||url.contains("personalBindBankcardExpand/bindBankcardSms")||url.contains("activateStockedUser/activatePersonalUser")){
                    startedUrl = url;
                }else if(url.contains("orderId")){
                    startedUrl = url;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String finishedUrl = url;
                //				CustomProgress.CustomDismis();
            }
        });
    }

    @Override
    protected void initData() {
        wv_safety.postUrl(Urls.url_H5,urlParameter.getBytes());
//                wv_safety.postUrl(Urls.url_H5, EncodingUtils.getBytes(urlParameter, "UTF-8"));
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BankH5Activity.this));
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
                        LoginUserProvider.saveUserInfo(BankH5Activity.this);
                        if (!TextUtils.isEmpty(isBackAccount) && isBackAccount.equals("1")) {//交易记录
                            finish();
                            if (BindBankCardActivity.getInstance() != null) {
                                BindBankCardActivity.getInstance().finish();
                            }
                            if (AnsactionRecordsActivity.getInstance() != null) {
                                AnsactionRecordsActivity.getInstance().finish();
                            }
                        } else if (!TextUtils.isEmpty(isBackAccount) && isBackAccount.equals("2")) {//安全设置
                            finish();
                            if (BindBankCardActivity.getInstance() != null) {
                                BindBankCardActivity.getInstance().finish();
                            }
                            if (SecuritySettingActivity.getInstance() != null) {
                                SecuritySettingActivity.getInstance().finish();
                            }
                        } else if (!TextUtils.isEmpty(isBackAccount) && isBackAccount.equals("3")) {//注册

                            if (BindBankCardActivity.getInstance() != null) {
                                BindBankCardActivity.getInstance().finish();
                            }
                            finish();
                        } else {
                            if (BindBankCardActivity.getInstance() != null) {
                                BindBankCardActivity.getInstance().finish();
                            }
                            finish();
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BankH5Activity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BankH5Activity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BankH5Activity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //							LoginUserProvider.cleanData(BindBankCardActivity.this);
                        //							LoginUserProvider.cleanDetailData(BindBankCardActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BankH5Activity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BankH5Activity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BankH5Activity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BankH5Activity.this, "请检查网络", 0).show();
            }
        });
    }
}

