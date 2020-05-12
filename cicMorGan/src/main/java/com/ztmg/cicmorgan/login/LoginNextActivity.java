package com.ztmg.cicmorgan.login;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.spec.IvParameterSpec;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.ztmg.cicmorgan.account.activity.LoginPasActivity;
import com.ztmg.cicmorgan.account.activity.RequestFriendsActivity;
import com.ztmg.cicmorgan.account.activity.SecuritySettingActivity;
import com.ztmg.cicmorgan.activity.CreateGesturePasswordActivity;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.test.TestQuestionFirstActivity;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.InComePop;

public class LoginNextActivity extends BaseActivity implements OnClickListener {
    private TextView tv_phone;
    private EditText et_password;
    private String phone;
    private String ip;
    public static LoginNextActivity instance;
    private boolean isShowPwd;//是否显示密码
    private Button iv_eyes_no;
    private String overtime;
    private String loginToken;
    private final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory() + "/download_test/";
    private ImageView mImageView;
    private Button mBtnSave;
    private ProgressDialog mSaveDialog = null;
    private Bitmap mBitmap;
    private String mFileName;
    private String mSaveMessage;
    private String logopath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login_next);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(LoginNextActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        instance = this;
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone").toString();
        overtime = intent.getStringExtra("overtime");
        if (overtime.equals("5")) {//立即投资，无论登录返回还是登录成功都把当前界面finish
            overtime = "0";
        }
        if (overtime.equals("6")) {//在mainActivity中点击账户，无论登录返回还是登录成功都把当前界面finish
            overtime = "0";
        }
        if (overtime.equals("11")) {//在确认投资界面，无论登录返回还是登录成功都把当前界面finish
            overtime = "0";
        }
        getPhoneIp();
        initView();
        initData();
    }

    @Override
    protected void initView() {

        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(phone);
        et_password = (EditText) findViewById(R.id.et_password);
        iv_eyes_no = (Button) findViewById(R.id.iv_eyes_no);
        iv_eyes_no.setOnClickListener(this);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.tv_forget_password).setOnClickListener(this);
        setTitle("登录");
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

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        String password = et_password.getText().toString();
        switch (v.getId()) {
            case R.id.bt_login://登录
                if (!TextUtils.isEmpty(password)) {
                    login(phone, "3", password, ip);
                } else {
                    Toast.makeText(LoginNextActivity.this, "请输入密码", 0).show();
                }

                break;
            case R.id.tv_forget_password://忘记密码
                Intent intent = new Intent(LoginNextActivity.this, ForgetPasActivity.class);
                intent.putExtra("phone", tv_phone.getText().toString());
                intent.putExtra("overtime", overtime);
                startActivity(intent);
                break;
            case R.id.iv_eyes_no://显示密码
                if (isShowPwd) {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPwd = false;
                    iv_eyes_no.setBackgroundResource(R.drawable.pic_pas);
                } else {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPwd = true;
                    iv_eyes_no.setBackgroundResource(R.drawable.pic_no_pas);
                }
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(LoginNextActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            default:
                break;
        }
    }

    //登录
    private void login(String mobile, String from, String pwd, String ip) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginNextActivity.this));
        String url = Urls.NEWLOGIN;
        RequestParams params = new RequestParams();
        params.put("mobile", mobile);
        params.put("from", from);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        params.put("ip", ip);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//登录成功
                        loginToken = jsonObject.getString("token");
                        getUserInfo(loginToken, "3");

                    } else {
                        Toast.makeText(LoginNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginNextActivity.this, "请检查网络", 0).show();
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
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginNextActivity.this));
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
                        info.setToken(loginToken);
                        info.setPhone(dataObj.getString("name"));
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
                        if (dataObj.has("signed")) {
                            info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        }
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(LoginNextActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginNextActivity.this);
                        util.put("isLogin", "isLogin");
                        util.put("mobile", dataObj.getString("name"));
                        getWeChatShareInfo(token, "2", "3");//获取分享图片
                        if (overtime.equals("0")) {//超时或者未登录状态
                            if (UnlockGesturePasswordActivity.mContext != null) {
                                UnlockGesturePasswordActivity.mContext.finish();
                            }
                            LoginActivity.getInstance().finish();
                            finish();
                        } else {//退出调到登录界面
                            Intent intent = new Intent(LoginNextActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.getInstance().finish();
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(LoginNextActivity.this, "请检查网络", 0).show();
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

    //获取分享图片
    private void getWeChatShareInfo(String token, String ordersum, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginNextActivity.this));
        String url = Urls.GETWECHATSHAREINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("ordersum", ordersum);
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
                        logopath = dataObj.getString("logopath");
                        getUrl();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(LoginNextActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(LoginNextActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(LoginNextActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //							LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //							LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginNextActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(LoginNextActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginNextActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(LoginNextActivity.this, "请检查网络", 0).show();
            }
        });
    }

    private void getUrl() {
        new Thread() {
            public void run() {
                try {
                    //						   logopath = "http://img.my.csdn.net/uploads/201211/21/1353511891_4579.jpg";
                    mFileName = "test.jpg";

                    //以下是取得图片的两种方法
                    //////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
                    byte[] data = getImage(logopath);
                    if (data != null) {
                        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                        sleep(2000);
                        saveBitmapToFile(mBitmap, ALBUM_PATH);
                    } else {
                        Toast.makeText(LoginNextActivity.this, "Image error!", 1).show();
                        logopath = "http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                    }
                    connectHanlder.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginNextActivity.this, "无法链接网络！", 1).show();
                    logopath = "http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";

                }

            }

            ;
        }.start();
    }

    public byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readStream(inStream);
        }
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    private Handler connectHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //		            Log.d(TAG, "display image");
            // 更新UI，显示图片
            if (mBitmap != null) {
                //		                mImageView.setImageBitmap(mBitmap);// display image
            }
        }
    };

    public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {

        // 3.保存Bitmap

        try {

            File path = new File(ALBUM_PATH);

            // 文件

            String filepath = ALBUM_PATH + "/test.jpg";

            File file = new File(filepath);

            if (!path.exists()) {

                path.mkdirs();

            }

            if (!file.exists()) {

                file.createNewFile();

            }

            FileOutputStream fos = null;

            fos = new FileOutputStream(file);

            if (null != fos) {

                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);

                fos.flush();

                fos.close();

            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
