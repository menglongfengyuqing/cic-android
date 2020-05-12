package com.ztmg.cicmorgan.login;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.RequestFriendsActivity;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivityCom;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.SafeInvestmentDetailActivity;
import com.ztmg.cicmorgan.more.activity.IntegralShopActivity;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PwdUtil;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.ClearEditText;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * Created by dell on 2019/1/15.
 */

public class LoginByPhoneNumActivity extends BaseActivityCom implements View.OnClickListener {
    private ClearEditText et_phone;
    private EditText et_phone_code;
    private String phone;
    private String phoneCode;

    public static LoginByPhoneNumActivity mContext;
    private String overtime;
//    private Button iv_eyes_no;
    private boolean isShowPwd;//是否显示密码
    private String ip;
    private String logopath;
    private final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory() + "/download_test/";
    private String mFileName;
    private Bitmap mBitmap;
    private Button bt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(LoginByPhoneNumActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_login_by_phone_num);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = d.getHeight();   //高度设置为屏幕的1.0
        p.width = d.getWidth();    //宽度设置为屏幕的0.8
        getWindow().setAttributes(p);
        mContext = this;
        Intent intent = getIntent();
        overtime = intent.getStringExtra("overtime");//是否超时0超时
        //		if(overtime.equals("5")){//立即投资，无论登录返回还是登录成功都把当前界面finish
        //			overtime="0";
        //		}
        //		if(overtime.equals("6")){//在mainActivity中点击账户，无论登录返回还是登录成功都把当前界面finish
        //			overtime="0";
        //		}
        initView();
        initData();
//        getPhoneIp();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(LoginByPhoneNumActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


    }

    public static LoginByPhoneNumActivity getInstance() {
        return mContext;
    }

    @Override
    protected void initView() {
        //		setRightText("提交", null);
        et_phone = (ClearEditText) findViewById(R.id.et_phone_by_phone);
        DoCacheUtil util = DoCacheUtil.get(LoginByPhoneNumActivity.this);
        et_phone.setText(util.getAsString("mobile"));
        et_phone.addTextChangedListener(new TextWatcher() {

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
                if (strContent.length() > 11) {
                    Toast.makeText(LoginByPhoneNumActivity.this, "手机号码11位，请检查手机号是否正确", Toast.LENGTH_SHORT).show();
                    // 获取输入的字符
                    String newStrContent = et_phone.getText().toString();
                    // 截取50个字符包括
                    String cutStringContent = newStrContent.substring(0, 11);
                    // 重新设置
                    et_phone.setText(cutStringContent);
                    // 设置光标的位置，不然光标都跑到第一个位置了
                    et_phone.setSelection(cutStringContent.length());
                    return;
                }

            }
        });
        //		findViewById(R.id.bt_next).setOnClickListener(this);

        //		setBack(new OnClickListener() {
        //
        //			@Override
        //			public void onClick(View v) {
        //				if(!overtime.equals("0")){//0超时
        //					if(OnceInvestmentActivity.mContext!=null){//确认投资界面，返回投资详情
        //						OnceInvestmentActivity.mContext.finish();
        //					}
        //					finish();
        //				}else{
        //					Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        //					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
        //							| Intent.FLAG_ACTIVITY_NEW_TASK);
        //					startActivity(intent);
        //				}
        //			}
        //		});
        et_phone_code = (EditText) findViewById(R.id.et_phone_code);
        //Cancel();
        findViewById(R.id.iv_close_by_phone).setOnClickListener(this);
//        iv_eyes_no = (Button) findViewById(R.id.iv_eyes_no);
//        iv_eyes_no.setOnClickListener(this);
        findViewById(R.id.bt_login_by_phone).setOnClickListener(this);
        bt_register = (Button) findViewById(R.id.bt_register_by_phone);
        bt_register.setOnClickListener(this);
        findViewById(R.id.ll_agreeage_by_phone).setOnClickListener(this);
        findViewById(R.id.tv_login_by_password).setOnClickListener(this);//密码登录
//        findViewById(R.id.tv_forgot_password).setOnClickListener(this);
        //findViewById(R.id.tv_tip).setOnClickListener(this);
    }

//    private void Cancel() {
//        et_password.setLongClickable(false); //禁止长按
//        et_password.setTextIsSelectable(false); // 禁止被用户选择
//        // 取消横屏复制粘贴
//        et_password.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//        // 取消复制粘贴
//        et_password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//        });
//    }

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
        phone = et_phone.getText().toString();
        phoneCode = et_phone_code.getText().toString();
        switch (v.getId()) {
            case R.id.iv_close_by_phone:
                onEvent(LoginByPhoneNumActivity.this, "105001_denglu_close_click");
                if (overtime.equals("8")) {//忘记密码跳转登录界面
                    Intent intent = new Intent(LoginByPhoneNumActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (!overtime.equals("0")) {//0超时
                    if (IntegralShopActivity.mContext != null) {//确认投资界面，返回投资详情
                        IntegralShopActivity.mContext.finish();
                    }
                    if (RequestFriendsActivity.mContext != null) {
                        RequestFriendsActivity.mContext.finish();
                    }
                    finish();
                } else {
                    Intent intent = new Intent(LoginByPhoneNumActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                break;
//            case R.id.iv_eyes_no://显示密码
//                if (isShowPwd) {
//                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    isShowPwd = false;
//                    iv_eyes_no.setBackgroundResource(R.drawable.pic_pas);
//                    et_password.setSelection(et_password.getText().toString().length());
//                } else {
//                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    isShowPwd = true;
//                    iv_eyes_no.setBackgroundResource(R.drawable.pic_no_pas);
//                    et_password.setSelection(et_password.getText().toString().length());
//                }
//                break;
            case R.id.bt_login_by_phone:
                onEvent(LoginByPhoneNumActivity.this, "105002_denglu_tyyhxybdl_click");
                if (TextUtils.isEmpty(phone)) {
                    CustomToastUtils customToast = new CustomToastUtils(LoginByPhoneNumActivity.this, "手机号不能为空");
                    customToast.show();
                    //ToastUtils.show(LoginActivity.this, "手机号不能为空");
                    return;
                }
                Pattern p = Pattern.compile("[1][123456789]\\d{9}");
                Matcher m = p.matcher(phone);
                if (!m.matches()) {//手机格式正确
                    CustomToastUtils customToast = new CustomToastUtils(LoginByPhoneNumActivity.this, "请输入正确的手机号");
                    customToast.show();
                    //Toast.makeText(LoginActivity.this, "请输入正确的手机号", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(phoneCode)) {
                    CustomToastUtils customToast = new CustomToastUtils(LoginByPhoneNumActivity.this, "验证码不能为空");
                    customToast.show();
                    //Toast.makeText(LoginActivity.this, "密码不能为空", 0).show();
                    return;
                }
//                if (!PwdUtil.isPWD(password)) {
//                    new CustomToastUtils(this, "请输入6-16位的数字和字母组合密码").show();
//                    //Toast.makeText(RegisterActivity.this, "请输入数字和字母的组合，6位及以上的密码", 0).show();
//                    return;
//                }
                //判断手机号是否注册
                ifRegister(phone, "3");
                break;
            case R.id.bt_register_by_phone:
                onEvent(LoginByPhoneNumActivity.this, "105003_denglu_zclhb_click");
                Intent registerIntent = new Intent(LoginByPhoneNumActivity.this, RegisterActivity.class);
                registerIntent.putExtra("overtime", overtime);
                startActivity(registerIntent);
                break;
            case R.id.ll_agreeage_by_phone:
                onEvent(LoginByPhoneNumActivity.this, "105004_denglu_ckyhxy_click");
                Intent agreeageIntent = new Intent(LoginByPhoneNumActivity.this, AgreementActivity.class);
                agreeageIntent.putExtra("path", Urls.LOGINAGREEMENT);
                agreeageIntent.putExtra("title", "注册协议");
                startActivity(agreeageIntent);
                break;
            case R.id.tv_login_by_password://密码登录
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                    startActivity(intent);
                    finish();
                break;
//            case R.id.tv_forgot_password:
//                onEvent(LoginByPhoneNumActivity.this, "105005_denglu_wjmm_click");
//                Intent intent = new Intent(LoginByPhoneNumActivity.this, ForgetPasActivity.class);
//                intent.putExtra("phone", phone);
//                startActivity(intent);
//                break;
            //            case R.id.tv_tip:
            //                Intent riskTipFirstIntent = new Intent(LoginActivity.this, AgreementActivity.class);
            //                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
            //                riskTipFirstIntent.putExtra("title", "风险提示书");
            //                startActivity(riskTipFirstIntent);
            //                break;
            default:
                break;
        }
    }


    //登录
    private void login(String mobile, String from, String pwd, String ip) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginActivity.this));
        //String encrypt = Md5Algorithm.encrypt(pwd);
        String url = Urls.NEWLOGIN;
        RequestParams params = new RequestParams();
        params.put("mobile", mobile);
        params.put("from", from);
        //ACache.get(this).put("login_pwd", pwd);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//登录成功
                        String loginToken = jsonObject.getString("token");
                        getUserInfo(loginToken, "3");
                        //getRequest(loginToken, "3");
                    } else {
                        CustomProgress.CustomDismis();
                        //Toast.makeText(LoginActivity.this, jsonObject.getString("message"), 0).show();
                        //ToastUtils.show(LoginActivity.this, jsonObject.getString("message"));

                        CustomToastUtils customToast = new CustomToastUtils(LoginByPhoneNumActivity.this, jsonObject.getString("message"));
                        customToast.show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(LoginByPhoneNumActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginByPhoneNumActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRequest(final String token, String from) {//TODO
        HashMap<String, String> params = new HashMap();
        params.put("token", token);
        params.put("from", from);
        //params.put("ip", ip);
        //        OkUtil.post(Urls.GETUSERINFO, this, params, new StringCallBackWithError() {
        //            @Override
        //            public void onSuccess(Response<ResponseBean<SimpleBean>> response) {
        //                LogUtil.e("------------------1" + response.body().data);
        //                // LogUtils.printJson(this, response.body().data, "");
        //
        //                LogUtil.e("------------------2" + response.body().data);
        //            }
        //
        //        });
        OkUtil.post("GETUSERINFO", Urls.GETUSERINFO, params, LoginByPhoneNumActivity.this, this);


    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginActivity.this));
        String url = Urls.GETUSERINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
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
                        if (dataObj.has("bindBankCard")) {
                            info.setIsBindBank(dataObj.getString("cgbBindBankCard"));//是否绑定银行卡 1未绑定 2已绑定
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
                        LoginUserProvider.saveUserInfo(LoginByPhoneNumActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(LoginByPhoneNumActivity.this);
                        util.put("isLogin", "isLogin");
                        util.put("mobile", dataObj.getString("name"));
                        getWeChatShareInfo(token, "2", "3");//获取分享图片

                        if (overtime.equals("8")) {//退出调到登录界面
                            Intent intent = new Intent(LoginByPhoneNumActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (!overtime.equals("7")) {//超时或者未登录状态
                            if (UnlockGesturePasswordActivity.mContext != null) {
                                UnlockGesturePasswordActivity.mContext.finish();
                            }
                           /* if (InvestmentDetailActivity.mContext != null) {
                                InvestmentDetailActivity.mContext.closeInvestmentDialog();
                            }*/
                            if (SafeInvestmentDetailActivity.mContext != null) {
                                SafeInvestmentDetailActivity.mContext.closeInvestmentDialog();
                            }
                            finish();
                        } else if (overtime.equals("7")) {//退出调到登录界面
                            Intent intent = new Intent(LoginByPhoneNumActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginByPhoneNumActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginByPhoneNumActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginByPhoneNumActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获取分享图片
    private void getWeChatShareInfo(String token, String ordersum, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginActivity.this));
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
                        String mGesture = LoginUserProvider.getUser(LoginByPhoneNumActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(LoginByPhoneNumActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(LoginByPhoneNumActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(LoginByPhoneNumActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(LoginByPhoneNumActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginByPhoneNumActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginByPhoneNumActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUrl() {
        new Thread() {
            public void run() {
                try {
                    mFileName = "test.jpg";
                    //以下是取得图片的两种方法
                    //////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
                    byte[] data = getImage(logopath);
                    if (data != null) {
                        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                        sleep(2000);
                        saveBitmapToFile(mBitmap, ALBUM_PATH);
                    } else {
                        Toast.makeText(LoginByPhoneNumActivity.this, "Image error!", Toast.LENGTH_LONG).show();
                        logopath = "http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                    }
                    connectHanlder.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    //					logopath = "http://cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                    //					Toast.makeText(LoginActivity.this,"无法链接网络！", 1).show();

                }
            }

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


    //判断是否注册过
    private void ifRegister(String mobilePhone, String from) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		DefaultHttpClient declient = new DefaultHttpClient();
        //		declient = HttpClientHelper.getHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(LoginActivity.this));
        String url = Urls.CHECKMOBILEPHONEISREGISTERED;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("from", from);
        //"https://tapi.gangpiaowang.com/Api/app_initialize"
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {//已注册
                        login(phone, "3", phoneCode, ip);
                        //etRequest(phone, "3", password, ip);

                    } else if (jsonObject.getString("state").equals("5")) {//未注册
                        CustomProgress.CustomDismis();
                        CustomToastUtils customToast = new CustomToastUtils(LoginByPhoneNumActivity.this, "该手机号尚未注册");
                        customToast.show();
                        //ToastUtils.show(LoginActivity.this, "您还没注册请点击注册");
                    } else {
                        CustomProgress.CustomDismis();
                        CustomToastUtils customToast = new CustomToastUtils(LoginByPhoneNumActivity.this, jsonObject.getString("message"));
                        customToast.show();
                        //Toast.makeText(LoginActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(LoginByPhoneNumActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(LoginByPhoneNumActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }


//    //获取手机ip
//    private void getPhoneIp() {
//        //获取wifi服务
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        //判断wifi是否开启
//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
//        }
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        ip = intToIp(ipAddress);
//    }
//
//    private String intToIp(int i) {
//
//        return (i & 0xFF) + "." +
//                ((i >> 8) & 0xFF) + "." +
//                ((i >> 16) & 0xFF) + "." +
//                (i >> 24 & 0xFF);
//    }

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    private long exitTime = 0;

    //	//获取返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //这里重写返回键
            if (overtime.equals("7")) {//退出跳转到登录界面，点击返回需要退出程序
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
            } else if (overtime.equals("0")) {//0超时
                Intent intent = new Intent(LoginByPhoneNumActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                if (IntegralShopActivity.mContext != null) {
                    IntegralShopActivity.mContext.finish();
                }
                if (RequestFriendsActivity.mContext != null) {
                    RequestFriendsActivity.mContext.finish();
                }
                finish();
            }
            return true;
        }
        return false;
    }
//
    @Override
    protected void responseData(String string, String json) {
        switch (string) {
            case "GETUSERINFO":

                //LogUtils.printJson("1111111111", json, "----------");
                // LogUtils.printJson("1111111111", "", "----------");
                // LogUtils.printJson("1111111111", "", "----------");
                //LogUtil.e("1111111111" + json);
                //ToastUtils.show(this, "返回成功");


                break;


        }
    }

}
