package com.ztmg.cicmorgan.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.MyApplication;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.BankH5Activity;
import com.ztmg.cicmorgan.account.picture.CircleImageView;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.CellEntity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LockPatternUtils;
import com.ztmg.cicmorgan.util.LockPatternView;
import com.ztmg.cicmorgan.util.LockPatternView.Cell;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 设置过手势密码  启用手势密码
 *
 * @author yly
 */
public class UnlockGesturePasswordActivity extends BaseActivity implements OnClickListener {
    private LockPatternView mLockPatternView;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private CountDownTimer mCountdownTimer = null;
    private TextView mHeadTextView, mForgetTextView, tv_lock_wrong;
    private Animation mShakeAnim;
    private CircleImageView user_icon;
    private Toast mToast;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    // 接值
    private int intent;
    //从设置页面过来
    private boolean setFlag;
    boolean passwordFlag = false;
    private String overtime;//0是超时或者未登录
    // 显示用户头像
    //private CircleImageView gesturepwd_unlock_userpicture;

    private CellEntity data[] = {new CellEntity(0, 0), new CellEntity(1, 0),
            new CellEntity(2, 0), new CellEntity(0, 1), new CellEntity(1, 1),
            new CellEntity(2, 1), new CellEntity(0, 2), new CellEntity(1, 2),
            new CellEntity(2, 2)};


    private void showToast(CharSequence message) {
        if (null == mToast) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    public static UnlockGesturePasswordActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉信息栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.gesturepassword_unlock);
        mContext = this;
        setFlag = getIntent().getBooleanExtra("set", false);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        intent = getIntent().getIntExtra("unlock", 0);
        mPhone = mUser.getPhone();
        mGesture = mUser.getGesturePwd();
        overtime = getIntent().getStringExtra("overtime");

        mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_unlock_lockview);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(true);
        user_icon = (CircleImageView) findViewById(R.id.user_icon);
        mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
        tv_lock_wrong = (TextView) findViewById(R.id.tv_lock_wrong);
        if (intent == 1 || intent == 2) {
            mHeadTextView.setText("请输入原手势密码");
        }
        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
        mForgetTextView = (TextView) findViewById(R.id.password_login);//忘记手势密码
        loginOther = (TextView) findViewById(R.id.gesturepwd_unlock_forget);//登录其他账号
        if (setFlag) {
            mForgetTextView.setVisibility(View.INVISIBLE);
            loginOther.setVisibility(View.INVISIBLE);
        }
        //密码登陆
        mForgetTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(UnlockGesturePasswordActivity.this, "217001_ssmmdl_mmdl_click");
                //startActivity(new Intent(UnlockGesturePasswordActivity.this,LoginActivity.class).putExtra("forgetPWD", true));
                //finish();
                LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
                LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
                DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                util.put("isLogin", "");
                if (!TextUtils.isEmpty(overtime) && overtime.equals("1")) {//非超时
                    Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                    exitIntent.putExtra("overtime", "7");//1非超时
                    exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(exitIntent);

                } else {
                    Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                            LoginActivity.class);
                    exitIntent.putExtra("overtime", overtime);
                    startActivity(exitIntent);
                    finish();
                }

            }
        });
        //取消 第一次进入app时跳转到首页
        loginOther.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(UnlockGesturePasswordActivity.this, "217002_ssmmdl_qx_click");
                LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
                LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
                DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                util.put("isLogin", "");
                //				String tocken = LoginUserProvider.getUser(UnlockGesturePasswordActivity.this).getToken();
                if (!TextUtils.isEmpty(RESTRICTIONS_SERVICE)) {
                    //取消手势密码
                    //cancelHandPas(tocken,"3");
                    //LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
                    //LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
                    //util.put("isLogin", "");
                    if (!TextUtils.isEmpty(overtime) && overtime.equals("1")) {//非超时
                        Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                                MainActivity.class);
                        //						exitIntent.putExtra("overtime", overtime);//0未登录或者超时，1非超时
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(exitIntent);
                    } else if (!TextUtils.isEmpty(overtime) && overtime.equals("0")) {//超时
                        Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                                LoginActivity.class);
                        exitIntent.putExtra("overtime", overtime);//0未登录或者超时，1非超时
                        startActivity(exitIntent);
                        finish();
                    } else if (!TextUtils.isEmpty(overtime) && overtime.equals("11")) {//投资详情超时取消跳转到登录界面
                        Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                        exitIntent.putExtra("overtime", overtime);//0未登录或者超时，1非超时
                        startActivity(exitIntent);
                        finish();
                    }
                }
            }
        });


        //设置头像图片
        String avatarPath = LoginUserProvider.getUser(UnlockGesturePasswordActivity.this).getAvatarPath();
        if (!StringUtils.isEmpty(avatarPath)) {
            mInflater = LayoutInflater.from(UnlockGesturePasswordActivity.this);
            mImageLoader = ImageLoaderUtil.getImageLoader();
            mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_defaultheadimage, false, false, false);
            mImageLoader.displayImage(avatarPath, user_icon, mDisplayImageOptions);
        }

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(UnlockGesturePasswordActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
        util.put("isLogin", "");
        if (!TextUtils.isEmpty(overtime) && overtime.equals("1")) {//非超时
            Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, MainActivity.class);
            //exitIntent.putExtra("overtime", overtime);//0未登录或者超时，1非超时
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exitIntent);
        } else if (!TextUtils.isEmpty(overtime) && overtime.equals("0")) {//超时
            Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                    LoginActivity.class);
            exitIntent.putExtra("overtime", overtime);//0未登录或者超时，1非超时
            startActivity(exitIntent);
            finish();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
            //startActivity(new Intent(this, Set_Act.class));
            //finish();
        }
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountdownTimer != null)
            mCountdownTimer.cancel();
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
            patternInProgress();
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null) {
                return;
            } else {
                if (MyApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    if (intent == 1) {
                        Intent intent = new Intent(UnlockGesturePasswordActivity.this, CreateGesturePasswordActivity.class);
                        intent.putExtra("locked", "changedlock").putExtra("set", true);
                        startActivity(intent);
                    } else if (intent == 2) {
                        //showToast("设置已成功");
                        //DialogUtils.showDialog(UnlockGesturePasswordActivity.this,"设置已成功");
                        MyApplication.getInstance().getLockPatternUtils().clearLock();
                    } else if (intent == 3) {
                        Intent intent = new Intent(UnlockGesturePasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // 打开新的Activity
                        //showToast("解锁成功");
                        //DialogUtils.showDialog(UnlockGesturePasswordActivity.this,"解锁成功");
                        StringBuffer sb = new StringBuffer();
                        if (pattern != null && pattern.size() > 0) {
                            for (Cell entity : pattern) {
                                for (int i = 0; i < data.length; i++) {
                                    if (data[i].getX() == entity.getRow() && data[i].getY() == entity.getColumn()) {
                                        sb.append(String.valueOf(i));
                                    }
                                }
                            }
                        }
                        String gestruStr = sb.toString();
                        loginHandPas(mPhone, gestruStr, "3", pattern);
                        //finish();
                    }
                } else {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    //  if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                    mFailedPatternAttemptsSinceLastTimeout++;
                    int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                    if (retry >= 0) {
                        // if (retry == 0)
                        // showToast("您已5次输错密码，请30秒后再试");
                        tv_lock_wrong.setVisibility(View.VISIBLE);
                        tv_lock_wrong.setText("密码错误，还可以再输入" + retry + "次");
                        tv_lock_wrong.setTextColor(getResources().getColor(R.color.text_d40f42));
                        tv_lock_wrong.startAnimation(mShakeAnim);
                    }
                    // }else{
                    //    //showToast("输入长度不够，请重试");
                    //    //DialogUtils.showDialog(UnlockGesturePasswordActivity.this,"输入长度不够，请重试");
                    //    tv_lock_wrong.setVisibility(View.VISIBLE);
                    //    tv_lock_wrong.setText("至少连接4个点，请重新绘制");
                    //    tv_lock_wrong.setTextColor(getResources().getColor(R.color.text_d40f42));
                    //    tv_lock_wrong.startAnimation(mShakeAnim);
                    //}
                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
                        // 0解锁(默认)1修改2关闭    5次输错密码清零
                        // if (intent == 1) {
                        // startMSCActivity(Login.class, 1);
                        // } else if (intent == 2) {
                        // mHandler.postDelayed(attemptLockout, 2000);
                        // startMSCActivity(Login.class, 2);
                        // } else {
                        // startMSCActivity(Login.class, 3);
                        // }
                        //LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
                        //LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                        util.put("isLogin", "");
                        if (!TextUtils.isEmpty(overtime) && overtime.equals("1")) {
                            Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                            exitIntent.putExtra("overtime", "7");//1非超时
                            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(exitIntent);
                        } else if (!TextUtils.isEmpty(overtime) && overtime.equals("0")) {//超时
                            Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                            exitIntent.putExtra("overtime", overtime);//1非超时
                            //| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(exitIntent);
                        }

                    } else {
                        // mLockPatternView.postDelayed(mClearPatternRunnable,
                        // 2000);
                    }
                }

            }
        }

        public void onPatternCellAdded(List<Cell> pattern) {

        }

        private void patternInProgress() {
        }
    };
    private TextView tv_jump;
    private TextView loginOther;
    private String mPhone;
    private String str;
    private String mGesture;

    @Override
    public void onClick(View v) {

    }


    //验证手势登录
    public void verfyGes(List<LockPatternView.Cell> pattern) {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        // if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
        mFailedPatternAttemptsSinceLastTimeout++;
        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
                - mFailedPatternAttemptsSinceLastTimeout;
        if (retry >= 0) {
            // if (retry == 0)
            // showToast("您已5次输错密码，请30秒后再试");
            tv_lock_wrong.setVisibility(View.VISIBLE);
            tv_lock_wrong.setText("密码错误，还可以再输入" + retry + "次");
            tv_lock_wrong.setTextColor(getResources().getColor(R.color.text_d40f42));
            tv_lock_wrong.startAnimation(mShakeAnim);
        }
        //        } else {
        //            //showToast("输入长度不够，请重试");
        //            //DialogUtils.showDialog(UnlockGesturePasswordActivity.this,"输入长度不够，请重试");
        //            tv_lock_wrong.setVisibility(View.VISIBLE);
        //            tv_lock_wrong.setText("至少连接4个点，请重新绘制");
        //            tv_lock_wrong.setTextColor(getResources().getColor(R.color.text_d40f42));
        //            tv_lock_wrong.startAnimation(mShakeAnim);
        //        }
        if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
            // 0解锁(默认)1修改2关闭    5次输错密码清零
            // if (intent == 1) {
            // startMSCActivity(Login.class, 1);
            // } else if (intent == 2) {
            // mHandler.postDelayed(attemptLockout, 2000);
            // startMSCActivity(Login.class, 2);
            // } else {
            // startMSCActivity(Login.class, 3);
            // }
            //			LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
            //			LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
            DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
            util.put("isLogin", "");
            if (!TextUtils.isEmpty(overtime) && overtime.equals("1")) {
                Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                        LoginActivity.class);
                exitIntent.putExtra("overtime", "7");//1非超时
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
            } else if (!TextUtils.isEmpty(overtime) && overtime.equals("0")) {//超时
                Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                        LoginActivity.class);
                exitIntent.putExtra("overtime", overtime);//1非超时
                //exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
            }

        } else {
            // mLockPatternView.postDelayed(mClearPatternRunnable,
            // 2000);
        }

    }

    // Runnable attemptLockout = new Runnable() {
    //
    // @Override
    // public void run() {
    // mLockPatternView.clearPattern();
    // mLockPatternView.setEnabled(false);
    // mCountdownTimer = new CountDownTimer(
    // LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {
    //
    // @Override
    // public void onTick(long millisUntilFinished) {
    // int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
    // if (secondsRemaining > 0) {
    // // mHeadTextView.setText(secondsRemaining + " 秒后重试");
    // } else {
    // mHeadTextView.setText("请绘制手势密码");
    // mHeadTextView.setTextColor(Color.WHITE);
    // }
    // }
    //
    // @Override
    // public void onFinish() {
    // mLockPatternView.setEnabled(true);
    // mFailedPatternAttemptsSinceLastTimeout = 0;
    // }
    // }.start();
    // }
    // };
    //手势密码登录
    private void loginHandPas(String mobilePhone, String gesturePwd, String from, final List<LockPatternView.Cell> pattern) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(UnlockGesturePasswordActivity.this));
        String url = Urls.VERIFYGESTUREPWD;
        RequestParams params = new RequestParams();
        params.put("mobilePhone", mobilePhone);
        params.put("gesturePwd", gesturePwd);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (jsonObject.getString("state").equals("0")) {
                        Toast.makeText(UnlockGesturePasswordActivity.this, msg, 0).show();
                        String token = jsonObject.getString("token");
                        String certificateChecked = jsonObject.getString("certificateChecked");//1为开户
                        String isActivate = jsonObject.getString("isActivate");//1.0版本用户开户后是否需要激活
                        if(certificateChecked.equals("2") && isActivate.equals("FALSE")){
                            CustomProgress.CustomDismis();
                            //需要激活
                            dialog(token);
                        }else{
                            getUserInfo(token, "3");
                        }





                    } else if (jsonObject.getString("state").equals("4")) {
                        CustomProgress.CustomDismis();
                        if (!TextUtils.isEmpty("overtime") && overtime.equals("1")) {//非超时
                            Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                            exitIntent.putExtra("overtime", "7");//1非超时
                            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(exitIntent);
                        } else {
                            Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                            exitIntent.putExtra("overtime", overtime);//1非超时
                            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(exitIntent);
                        }

                        Toast.makeText(UnlockGesturePasswordActivity.this, jsonObject.getString("message"), 0).show();
                    } else {
                        CustomProgress.CustomDismis();
                        verfyGes(pattern);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.CustomDismis();
                    Toast.makeText(UnlockGesturePasswordActivity.this, "解析异常", 0).show();
                    if (!TextUtils.isEmpty("overtime") && overtime.equals("1")) {//非超时
                        Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                        exitIntent.putExtra("overtime", "7");//1非超时
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(exitIntent);
                    } else {
                        Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                        exitIntent.putExtra("overtime", overtime);
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(exitIntent);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(UnlockGesturePasswordActivity.this, "请检查网络", 0).show();
                if (!TextUtils.isEmpty("overtime") && overtime.equals("1")) {//非超时
                    Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                            LoginActivity.class);
                    exitIntent.putExtra("overtime", "7");//1非超时
                    exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(exitIntent);
                } else {
                    Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this,
                            LoginActivity.class);
                    exitIntent.putExtra("overtime", overtime);
                    exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(exitIntent);
                }
            }
        });
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

    //取消手势密码
    private void cancelHandPas(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(UnlockGesturePasswordActivity.this));
        String url = Urls.CANCELGESTUREPWD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        Toast.makeText(UnlockGesturePasswordActivity.this, jsonObject.getString("message"), 0).show();
                        //LoginUserProvider.cleanData(getApplicationContext());
                        DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                        MyApplication.getInstance().getLockPatternUtils().clearLock();
                        util.put("cellList", "");
                        Intent exitIntent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                        exitIntent.putExtra("overtime", "7");//1非超时
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(exitIntent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(UnlockGesturePasswordActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(UnlockGesturePasswordActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
                        //LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(UnlockGesturePasswordActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UnlockGesturePasswordActivity.this, "解析异常", 0).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(UnlockGesturePasswordActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(UnlockGesturePasswordActivity.this));
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
                        LoginUserProvider.saveUserInfo(UnlockGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                        util.put("isLogin", "isLogin");
                        if (overtime != null && overtime.equals("0")) {//超时
                            finish();
                        } else if (overtime != null && overtime.equals("11")) {//投资详情异地登录
                            finish();
                        } else {
                            startActivity(new Intent(UnlockGesturePasswordActivity.this, MainActivity.class));
                        }

                    } else {
                        Toast.makeText(UnlockGesturePasswordActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UnlockGesturePasswordActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(UnlockGesturePasswordActivity.this, "请检查网络", 0).show();
            }
        });
    }

    // 需要激活提示框
    private void dialog(final String token) {

        final Dialog mdialog = new Dialog(UnlockGesturePasswordActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dl_isactivation);

        TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
        tv_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Activation(token,"1");
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }
    //激活
    private void Activation(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindBankCardActivity.this));
        String url = Urls.MEMBERACTIVATION;
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
                    if (jsonObject.get("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String reqData = dataObj.getString("reqData");
                        String platformNo = dataObj.getString("platformNo");
                        String sign = dataObj.getString("sign");
                        String keySerial = dataObj.getString("keySerial");
                        String serviceName = dataObj.getString("serviceName");
                        Intent safetyIntent = new Intent(UnlockGesturePasswordActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("reqData", reqData);
                        safetyIntent.putExtra("platformNo", platformNo);
                        safetyIntent.putExtra("sign", sign);
                        safetyIntent.putExtra("keySerial", keySerial);
                        safetyIntent.putExtra("serviceName", serviceName);
                        safetyIntent.putExtra("title", "激活账户");
                        startActivity(safetyIntent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(UnlockGesturePasswordActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(UnlockGesturePasswordActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(UnlockGesturePasswordActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(UnlockGesturePasswordActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UnlockGesturePasswordActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(UnlockGesturePasswordActivity.this, "请检查网络", 0).show();
            }
        });
    }


}
