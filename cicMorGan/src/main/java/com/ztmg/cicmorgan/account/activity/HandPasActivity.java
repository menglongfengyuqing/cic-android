package com.ztmg.cicmorgan.account.activity;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.CreateGesturePasswordActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.MyApplication;
import com.ztmg.cicmorgan.entity.CellEntity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

/**
 * 手势密码
 *
 * @author pc
 */
public class HandPasActivity extends BaseActivity implements OnClickListener {
    private Button bt_hand_pas;
    private RelativeLayout rl_no_modify, rl_modify_hand_pas, rl_setting_hand_pas;
    private boolean isOpen;
    private Dialog mdialog;
    private String gesturePwd;
    private DoCacheUtil util;
    private final static int CANCEL = 101;
    String isBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_hand_pas);
        //0未设置1已设置
        //gesturePwd = LoginUserProvider.getUser(HandPasActivity.this).getGesturePwd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(HandPasActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    @Override
    protected void initView() {

        util = DoCacheUtil.get(this);
        List<CellEntity> list = (List<CellEntity>) util.getAsObject("cellList");
        rl_setting_hand_pas = (RelativeLayout) findViewById(R.id.rl_setting_hand_pas);
        rl_setting_hand_pas.setOnClickListener(this);//设置手势密码
        bt_hand_pas = (Button) findViewById(R.id.bt_hand_pas);//开关

        bt_hand_pas.setOnClickListener(this);
        rl_modify_hand_pas = (RelativeLayout) findViewById(R.id.rl_modify_hand_pas);
        rl_modify_hand_pas.setOnClickListener(this);//修改手势密码
        rl_no_modify = (RelativeLayout) findViewById(R.id.rl_no_modify);//不能修手势密码

        //		if(list!=null&&list.size()>0){//判断手势的集合是否为空
        //			isOpen = false;
        //			bt_hand_pas.setBackgroundResource(R.drawable.hand_open);
        //			rl_modify_hand_pas.setVisibility(View.VISIBLE);
        //			rl_no_modify.setVisibility(View.GONE);
        //			rl_setting_hand_pas.setClickable(true);
        //
        //		}else{
        //			isOpen = true;
        //			bt_hand_pas.setBackgroundResource(R.drawable.hand_close);
        //			rl_modify_hand_pas.setVisibility(View.GONE);
        //			rl_no_modify.setVisibility(View.VISIBLE);
        //			rl_setting_hand_pas.setClickable(false);
        //		}
        String mGesture = LoginUserProvider.getUser(HandPasActivity.this).getGesturePwd();
        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {//设置过手势密码
            isOpen = false;
            bt_hand_pas.setBackgroundResource(R.drawable.hand_open);
            rl_modify_hand_pas.setVisibility(View.VISIBLE);
            rl_no_modify.setVisibility(View.GONE);
            rl_setting_hand_pas.setClickable(true);

        } else {
            isOpen = true;
            bt_hand_pas.setBackgroundResource(R.drawable.hand_close);
            rl_modify_hand_pas.setVisibility(View.GONE);
            rl_no_modify.setVisibility(View.VISIBLE);
            rl_setting_hand_pas.setClickable(false);
        }

        setTitle("手势密码");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_hand_pas://设置手势密码
                break;
            case R.id.bt_hand_pas://开关
                isBt = "0";
                dialog();
                break;
            case R.id.rl_modify_hand_pas://修改手势密码
                isBt = "1";
                dialog();
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(HandPasActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            default:
                break;
        }
    }

    // 修改成功的对话框
    private void dialog() {

        mdialog = new Dialog(HandPasActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dialog_hand_pas);
        final EditText et_pas = (EditText) mdialog.findViewById(R.id.et_pas);
        mdialog.findViewById(R.id.bt_cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        mdialog.findViewById(R.id.bt_submit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String tokent = LoginUserProvider.getUser(HandPasActivity.this).getToken();
                String pas = et_pas.getText().toString();
                if (!TextUtils.isEmpty(pas)) {
                    checkOldPwd(tokent, "3", pas);
                } else {
                    Toast.makeText(HandPasActivity.this, "请输入密码", 0).show();
                }
            }
        });
        mdialog.show();
    }

    //原密码是否正确
    private void checkOldPwd(String token, String from, String pwd) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(HandPasActivity.this));
        String url = Urls.NEWCHECKOLDPWD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("pwd", Md5Algorithm.encrypt(pwd));
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        mdialog.dismiss();
                        if (isBt.equals("0")) {//点击开关
                            if (isOpen) {
                                // 开启手势密码
                                bt_hand_pas.setBackgroundResource(R.drawable.hand_open);
                                isOpen = false;
                                rl_modify_hand_pas.setVisibility(View.VISIBLE);
                                rl_no_modify.setVisibility(View.GONE);
                                rl_setting_hand_pas.setClickable(true);
                                Intent intent = new Intent(HandPasActivity.this, CreateGesturePasswordActivity.class);
                                intent.putExtra("enterType", "isModify");
                                intent.putExtra("overtime", "1");
                                startActivity(intent);

                            } else {
                                // 关闭手势密码
                                String token = LoginUserProvider.getUser(HandPasActivity.this).getToken().toString();
                                cancelHandPas(token, "3");

                            }
                        } else {//点击修改
                            Intent modifyIntent = new Intent(HandPasActivity.this, CreateGesturePasswordActivity.class);
                            modifyIntent.putExtra("enterType", "isModify");
                            startActivity(modifyIntent);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(HandPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(HandPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(HandPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(HandPasActivity.this);
                        //						LoginUserProvider.cleanDetailData(HandPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(HandPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(HandPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HandPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(HandPasActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    //取消手势密码
    private void cancelHandPas(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //			AsyncHttpClient client = new AsyncHttpClient();
        //			client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(HandPasActivity.this));
        String url = Urls.CANCELGESTUREPWD;
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
                        Toast.makeText(HandPasActivity.this, jsonObject.getString("message"), 0).show();
                        bt_hand_pas.setBackgroundResource(R.drawable.hand_close);
                        isOpen = true;
                        rl_modify_hand_pas.setVisibility(View.GONE);
                        rl_no_modify.setVisibility(View.VISIBLE);
                        rl_setting_hand_pas.setClickable(false);
                        MyApplication.getInstance().getLockPatternUtils().clearLock();
                        util.put("cellList", "");
                        getUserInfo(token, "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(HandPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(HandPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(HandPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //							LoginUserProvider.cleanData(CreateGesturePasswordActivity.this);
                        //							LoginUserProvider.cleanDetailData(CreateGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(HandPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(HandPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HandPasActivity.this, "解析异常", 0).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(HandPasActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //			AsyncHttpClient client = new AsyncHttpClient();
        //			client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(HandPasActivity.this));
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
                        info.setPhone(dataObj.getString("name"));
                        info.setToken(token);
                        info.setGesturePwd(dataObj.getString("gesturePwd"));//是否设置过手势密码，0设置1已设置
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
                        DoCacheUtil util = DoCacheUtil.get(HandPasActivity.this);
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(HandPasActivity.this);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(HandPasActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(HandPasActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(HandPasActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //							LoginUserProvider.cleanData(CreateGesturePasswordActivity.this);
                        //							LoginUserProvider.cleanDetailData(CreateGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(HandPasActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(HandPasActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HandPasActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(HandPasActivity.this, "请检查网络", 0).show();
            }
        });
    }
}
