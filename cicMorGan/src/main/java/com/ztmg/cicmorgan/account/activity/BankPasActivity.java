package com.ztmg.cicmorgan.account.activity;

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
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

/**
 * 交易密码
 *
 * @author pc
 */
public class BankPasActivity extends BaseActivity implements OnClickListener {
    private EditText et_bank_phone_code, et_bank_new_pas;
    private TextView tv_bank_get_phone_code;
    private Button bt_bank_submit;
    private TimeTask timeTask;
    private String ip;
    private String phone;
    private TextView tv_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_bank_pas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BankPasActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        phone = LoginUserProvider.getUser(BankPasActivity.this).getPhone().toString();
        initView();
        initData();
        timeTask = new TimeTask(60 * 1000);
    }

    @Override
    protected void initView() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(phone);
        et_bank_phone_code = (EditText) findViewById(R.id.et_bank_phone_code);
        et_bank_new_pas = (EditText) findViewById(R.id.et_bank_new_pas);
        et_bank_new_pas.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strContent = s.toString();
                int num = Integer.valueOf(strContent.length());
                if (strContent.length() > 6) {
                    Toast.makeText(BankPasActivity.this, "交易密码为6位", 0).show();
                    // 获取输入的字符
                    String newStrContent = et_bank_new_pas.getText().toString();
                    // 截取50个字符包括
                    String cutStringContent = newStrContent.substring(0, 6);
                    // 重新设置
                    et_bank_new_pas.setText(cutStringContent);
                    // 设置光标的位置，不然光标都跑到第一个位置了
                    et_bank_new_pas.setSelection(cutStringContent.length());
                }

            }
        });
        tv_bank_get_phone_code = (TextView) findViewById(R.id.tv_bank_get_phone_code);
        bt_bank_submit = (Button) findViewById(R.id.bt_bank_submit);
        String bankPas = LoginUserProvider.getUser(BankPasActivity.this).getBankPas();
        if (TextUtils.isEmpty(bankPas) || bankPas.equals("null")) {
            setTitle("设置交易密码");
        } else {
            setTitle("修改交易密码");
        }
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
        tv_bank_get_phone_code.setOnClickListener(this);
        bt_bank_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bank_get_phone_code:
                if (!TextUtils.isEmpty(phone)) {
                    String token = LoginUserProvider.getUser(BankPasActivity.this).getToken();
                    sendVerity(phone, "6", ip, "3", token);
                }
                break;
            case R.id.bt_bank_submit:
                String phoneCode = et_bank_phone_code.getText().toString();
                String newPas = et_bank_new_pas.getText().toString();
                if (TextUtils.isEmpty(phoneCode)) {
                    Toast.makeText(BankPasActivity.this, "验证码不能为空", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(newPas)) {
                    Toast.makeText(BankPasActivity.this, "密码不能为空", 0).show();
                    return;
                }
                if (newPas.length() != 6) {
                    Toast.makeText(BankPasActivity.this, "交易密码为6位，请检查交易密码是否正确", 0).show();
                    return;
                }
                String token = LoginUserProvider.getUser(BankPasActivity.this).getToken();
                verifySmsCode(phone, phoneCode, "3", token);
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(BankPasActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
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
            tv_bank_get_phone_code.setText("再获取验证码");
            tv_bank_get_phone_code.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_bank_get_phone_code.setText(millisUntilFinished / 1000 + "秒后重发");
            tv_bank_get_phone_code.setClickable(false);
        }
    }

    /**
     * 发送验证码
     *
     * @param mobilePhone
     * @param type
     * @param ip
     * @param from
     */
    public void sendVerity(String mobilePhone, String type, String ip, String from, String token) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BankPasActivity.this));
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
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                        timeTask.start();
                    } else if (jsonObject.getString("state").equals("2")) {//发送失败
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BankPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BankPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BankPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(BankPasActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BankPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BankPasActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    /**
     * 校验验证码是否正确
     */
    private void verifySmsCode(String mobilePhone, String smsCode, String from, String token) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BankPasActivity.this));
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
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//校验成功
                        String pas = et_bank_new_pas.getText().toString();
                        //						String MD5Pas = getMD5(pas);
                        String token = LoginUserProvider.getUser(BankPasActivity.this).getToken();
                        String oldBankPas = LoginUserProvider.getUser(BankPasActivity.this).getBankPas();//旧的交易密码
                        modifyLoginPas(token, "3", pas);

                    } else if (jsonObject.getString("state").equals("2")) {//校验失败
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BankPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BankPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BankPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        LoginUserProvider.cleanData(BankPasActivity.this);
                        LoginUserProvider.cleanDetailData(BankPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BankPasActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BankPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BankPasActivity.this, "请检查网络", 0).show();
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

    //修改交易密码
    private void modifyLoginPas(String token, String from, String pass) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BankPasActivity.this));
        String url = Urls.FINDTRADEPASSWORD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("pass", pass);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//修改密码成功
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                        dialog();
                        getUserInfo(LoginUserProvider.getUser(BankPasActivity.this).getToken(), "3");

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BankPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BankPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BankPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(BankPasActivity.this);
                        //						LoginUserProvider.cleanDetailData(BankPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BankPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BankPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BankPasActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //	private void modifyLoginPas(String token,String from,String pass){
    //		HttpUtils httpUtils = new HttpUtils(16000);
    //		httpUtils.configCurrentHttpCacheExpiry(0 * 1000);
    //		httpUtils.configDefaultHttpCacheExpiry(0 * 1000);
    //		httpUtils.configRequestRetryCount(0);
    //
    //		long timeSpan = System.currentTimeMillis()/1000;
    //
    //		HashMap<String, String> map = new HashMap<String, String>();
    //		map.put("token", String.valueOf(token));
    //		map.put("from", from);
    //		map.put("pass", pass);
    ////		map.put("timespan", String.valueOf(timeSpan));
    //		String md5 = "";
    //
    //		try {
    //			String sn =getSnString(map);
    //			md5 = CommonUtil.Md5(sn);
    //		} catch (NoSuchAlgorithmException e1) {
    //			e1.printStackTrace();
    //		}
    //
    //		String url = Urls.FINDTRADEPASSWORD
    //				+"?source="+source
    //				+"&version="+version
    //				+"&articleId="+articleId
    //				+"&timespan="+timeSpan+"&MD5="+md5;
    //		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
    //		});
    //		/**
    //		 * 获取签名string（对要签名的内容串进行排序拼接）
    //		 * @param parmes
    //		 * @return
    //		 * @throws NoSuchAlgorithmException
    //		 */
    //		public final String getSnString(Map<String, String> parmes)
    //				throws NoSuchAlgorithmException {
    //
    //			Object[] key_arr = parmes.keySet().toArray();
    //			Arrays.sort(key_arr);
    //			StringBuffer sb = new StringBuffer();
    //			for (Object key : key_arr) {
    //				String values = parmes.get(key);
    //				sb.append(key);
    //				sb.append("=");
    //				sb.append(values);
    //				sb.append("&");
    //			}
    ////			Log.e("bosssdk", "get pay Md5 " + sb.toString());
    //			return sb.toString().substring(0, sb.length()-1)+"&jmjkey="+Constant.jmjkey;
    //		}

    // 修改成功的对话框
    private void dialog() {

        final Dialog mdialog = new Dialog(BankPasActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dialog_setting);

        TextView tv_dialog_text = (TextView) mdialog.findViewById(R.id.tv_dialog_text);
        tv_dialog_text.setText("恭喜您   密码重置成功！");
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

    //交易密码MD5加密
    public static String getMD5(String content) {

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (Exception e) {
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString().toLowerCase();
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BankPasActivity.this));
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
                        if (dataObj.has("signed")) {
                            info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        }
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(BankPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(BankPasActivity.this);
                        util.put("isLogin", "isLogin");
                        util.put("mobile", dataObj.getString("name"));

                    } else {
                        Toast.makeText(BankPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BankPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(BankPasActivity.this, "请检查网络", 0).show();
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
