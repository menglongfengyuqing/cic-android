package com.ztmg.cicmorgan.account.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.CreateGesturePasswordActivity;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.MyApplication;
import com.ztmg.cicmorgan.entity.CellEntity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.Md5Algorithm;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 安全设置
 *
 * @author pc
 */
public class SecuritySettingActivity extends BaseActivity implements OnClickListener {
    private String isBindBankCard;//1未绑定 2已绑定
    private static SecuritySettingActivity mContext;
    private TextView tv_test, tv_test_type, tv_again_test;
    private TextView tv_open_account;
    private TextView tv_hand_password;
    private RelativeLayout rl_gesture_pas;
    private Button bt_hand_pas;
    private boolean isOpen;
    private DoCacheUtil util;
    private String isBt;
    private Dialog mdialog;
    private TextView tv_hands_text;
    private View v_line;
    private String mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(SecuritySettingActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_security_setting);
        mContext = this;
        isBindBankCard = LoginUserProvider.getUser(SecuritySettingActivity.this).getIsBindBank();//1未绑定 2已绑定
        initView();
        //		refresh();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(SecuritySettingActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    public static SecuritySettingActivity getInstance() {
        return mContext;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String gesturePwd = LoginUserProvider.getUser(SecuritySettingActivity.this).getGesturePwd();
        if (gesturePwd.equals("1") && !gesturePwd.equals("") && gesturePwd != null) {
            //设置手势密码
            bt_hand_pas.setBackgroundResource(R.drawable.hand_open);
            rl_gesture_pas.setVisibility(View.VISIBLE);
            v_line.setVisibility(View.VISIBLE);
            tv_hands_text.setText("停用手势密码");
        } else {
            //未设置手势密码
            bt_hand_pas.setBackgroundResource(R.drawable.hand_close);
            rl_gesture_pas.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
            tv_hands_text.setText("启用手势密码");
        }
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        util = DoCacheUtil.get(this);
        List<CellEntity> list = (List<CellEntity>) util.getAsObject("cellList");

        findViewById(R.id.rl_login_pas).setOnClickListener(this);
        v_line = findViewById(R.id.v_line);
        rl_gesture_pas = (RelativeLayout) findViewById(R.id.rl_gesture_pas);//修改手势密码
        rl_gesture_pas.setOnClickListener(this);
        String bankPas = LoginUserProvider.getUser(SecuritySettingActivity.this).getBankPas();
        tv_hand_password = (TextView) findViewById(R.id.tv_hand_password);//设置手势密码
        tv_hands_text = (TextView) findViewById(R.id.tv_hands_text);//启用还是停用
        setTitle("安全设置");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(SecuritySettingActivity.this, "212001_aqsz_back_click");
                finish();
            }
        });
        findViewById(R.id.bt_exit).setOnClickListener(this);
        bt_hand_pas = (Button) findViewById(R.id.bt_hand_pas);
        bt_hand_pas.setOnClickListener(this);
        mGesture = LoginUserProvider.getUser(SecuritySettingActivity.this).getGesturePwd();
        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {
            //设置过手势密码
            isOpen = false;
            bt_hand_pas.setBackgroundResource(R.drawable.hand_open);
            rl_gesture_pas.setVisibility(View.VISIBLE);
            tv_hands_text.setText("停用手势密码");
            v_line.setVisibility(View.VISIBLE);
        } else {
            isOpen = true;
            bt_hand_pas.setBackgroundResource(R.drawable.hand_close);
            rl_gesture_pas.setVisibility(View.GONE);
            tv_hands_text.setText("启用手势密码");
            v_line.setVisibility(View.GONE);
        }


    }

    @Override
    protected void initData() {

    }

    public void refresh() {
        if (TextUtils.isEmpty(LoginUserProvider.getUser(SecuritySettingActivity.this).getUserType())) {//未测评
            tv_test.setText("风险测评");
            tv_test_type.setText("");
            tv_again_test.setText("开始测评");
        } else {
            tv_test.setText("风险测评：");
            tv_test_type.setText(LoginUserProvider.getUser(SecuritySettingActivity.this).getUserType());
            tv_again_test.setText("重新测评");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_login_pas://登录密码
                onEvent(SecuritySettingActivity.this, "212002_aqsz_xgdlmm_click");
                Intent loginPasIntent = new Intent(SecuritySettingActivity.this, LoginPasActivity.class);
                startActivity(loginPasIntent);
                break;
            case R.id.bt_hand_pas://启用手势密码
                if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {
                    //设置过手势密码   //启用就是显示停用，停用就是显示启用
                    //tv_hands_text.setText("停用手势密码");
                    onEvent(SecuritySettingActivity.this, "212004_aqsz_qyssmm_click");
                } else {
                    //tv_hands_text.setText("启用手势密码");
                    onEvent(SecuritySettingActivity.this, "212005_aqsz_tyssmm_click");
                }
                isBt = "0";
                dialog();
                break;
            case R.id.rl_gesture_pas://修改手势密码
                onEvent(SecuritySettingActivity.this, "212003_aqsz_xgssmm_click");
                isBt = "1";
                dialog();
                //			Intent handPasIntent = new Intent(SecuritySettingActivity.this,HandPasActivity.class);
                //			startActivity(handPasIntent);
                break;
            case R.id.bt_exit://退出登录
                onEvent(SecuritySettingActivity.this, "212006_aqsz_aqtc_click");
                LoginUserProvider.cleanData(SecuritySettingActivity.this);
                LoginUserProvider.cleanDetailData(SecuritySettingActivity.this);
                DoCacheUtil util = DoCacheUtil.get(SecuritySettingActivity.this);
                util.put("isLogin", "");
                Intent exitIntent = new Intent(SecuritySettingActivity.this, MainActivity.class);
                //exitIntent.putExtra("overtime", "7");//1非超时
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
                break;
            default:
                break;
        }
    }

    // 修改成功的对话框
    private void dialog() {
        mdialog = new Dialog(SecuritySettingActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dialog_hand_pas);
        mdialog.setCanceledOnTouchOutside(true);
        final EditText et_pas = (EditText) mdialog.findViewById(R.id.et_pas);
        mdialog.findViewById(R.id.bt_submit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String tokent = LoginUserProvider.getUser(SecuritySettingActivity.this).getToken();
                String pas = et_pas.getText().toString();
                if (!TextUtils.isEmpty(pas)) {
                    checkOldPwd(tokent, "3", pas);
                } else {
                    Toast.makeText(SecuritySettingActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mdialog.show();
    }

    //原密码是否正确
    private void checkOldPwd(String token, String from, String pwd) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
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
                                rl_gesture_pas.setVisibility(View.VISIBLE);
                                v_line.setVisibility(View.VISIBLE);
                                tv_hands_text.setText("停用手势密码");
                                Intent intent = new Intent(SecuritySettingActivity.this, CreateGesturePasswordActivity.class);
                                intent.putExtra("enterType", "isModify");
                                intent.putExtra("overtime", "1");
                                startActivity(intent);

                            } else {
                                // 关闭手势密码
                                String token = LoginUserProvider.getUser(SecuritySettingActivity.this).getToken().toString();
                                cancelHandPas(token, "3");

                            }
                        } else {//点击修改
                            Intent modifyIntent = new Intent(SecuritySettingActivity.this, CreateGesturePasswordActivity.class);
                            modifyIntent.putExtra("enterType", "isModify");
                            startActivity(modifyIntent);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(SecuritySettingActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(SecuritySettingActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(SecuritySettingActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(HandPasActivity.this);
                        //						LoginUserProvider.cleanDetailData(HandPasActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(SecuritySettingActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(SecuritySettingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SecuritySettingActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(SecuritySettingActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    //取消手势密码
    private void cancelHandPas(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
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
                        Toast.makeText(SecuritySettingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        bt_hand_pas.setBackgroundResource(R.drawable.hand_close);
                        isOpen = true;
                        rl_gesture_pas.setVisibility(View.GONE);
                        v_line.setVisibility(View.GONE);
                        tv_hands_text.setText("启用手势密码");
                        MyApplication.getInstance().getLockPatternUtils().clearLock();
                        util.put("cellList", "");
                        getUserInfo(token, "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(SecuritySettingActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(SecuritySettingActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(SecuritySettingActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //							LoginUserProvider.cleanData(CreateGesturePasswordActivity.this);
                        //							LoginUserProvider.cleanDetailData(CreateGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(SecuritySettingActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(SecuritySettingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SecuritySettingActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(SecuritySettingActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
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
                        DoCacheUtil util = DoCacheUtil.get(SecuritySettingActivity.this);
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(SecuritySettingActivity.this);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(SecuritySettingActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(SecuritySettingActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(SecuritySettingActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(SecuritySettingActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(SecuritySettingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SecuritySettingActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(SecuritySettingActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
