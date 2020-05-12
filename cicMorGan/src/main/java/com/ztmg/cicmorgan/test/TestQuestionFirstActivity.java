package com.ztmg.cicmorgan.test;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.AccountMessageActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.test.entity.OptionEntity;
import com.ztmg.cicmorgan.test.entity.TestTitleEntity;
import com.ztmg.cicmorgan.test.view.TestRollViewPager;
import com.ztmg.cicmorgan.test.view.TestRollViewPager.OnTurnListening;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.FixedSpeedScroller;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;


/**
 * 风险测评 dong dong
 * 2018年8月29日
 */
public class TestQuestionFirstActivity extends BaseActivity implements OnTurnListening {

    private LinearLayout ll_top_view_pager;//用于存放viewpager
    private List<TestTitleEntity> titleList;
    private TestRollViewPager viewPageradapter;
    private ViewPager viewpager;
    private static int page;
    private CommonDialog dialogSuccess;
    private String isInvestment;//0投资详情界面1安全设置
    private String onceInvestment;//起投金额
    private String proName, proId, proTime, money, useMoney, stepamount, rate, loandate;//项目名称,项目id,项目期限,融资金额,可投余额,浮动金额,年化收益率，项目放款日期
    private String isCanUseCoupon, isCanUsePlusCoupon;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    viewpager.setCurrentItem(page, true);
                    break;
                case 1://投资详情
                    dialogSuccess.dismiss();
                    //				Intent intentInvestemnt = new Intent(
                    //						TestQuestionFirstActivity.this,
                    //						OnceInvestmentActivity.class);
                    //				intentInvestemnt.putExtra("onceInvestment", onceInvestment);// 起投金额
                    //				intentInvestemnt.putExtra("proName", proName);// 项目名称
                    //				intentInvestemnt.putExtra("proId", proId);// 项目id
                    //				intentInvestemnt.putExtra("proTime", proTime);// 项目期限
                    //				intentInvestemnt.putExtra("money", money);// 融资金额
                    //				intentInvestemnt.putExtra("useMoney", useMoney);// 可投余额
                    //				intentInvestemnt.putExtra("stepamount", stepamount);// 浮动金额
                    //				intentInvestemnt.putExtra("rate", rate);// 年化收益率
                    //				intentInvestemnt.putExtra("loandate", loandate);// 项目放款日期
                    //				intentInvestemnt.putExtra("isCanUseCoupon", isCanUseCoupon);// 是否可以使用抵用券
                    //				intentInvestemnt.putExtra("isCanUsePlusCoupon",
                    //						isCanUsePlusCoupon);// 是否可以使用加息券
                    //				startActivity(intentInvestemnt);
                    finish();
                    break;
                case 2:
                    dialogSuccess.dismiss();
                    if (AccountMessageActivity.getInstance() != null) {
                        AccountMessageActivity.getInstance().refresh();
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(TestQuestionFirstActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_test_question);
        Intent intent = getIntent();
        isInvestment = intent.getStringExtra("isInvestment");
        //		if(isInvestment.equals("0")){//投资详情
        //			onceInvestment = intent.getStringExtra("onceInvestment");//起投金额
        //			proName = intent.getStringExtra("proName");//项目名称
        //			proId = intent.getStringExtra("proId");//项目id
        //			proTime = intent.getStringExtra("proTime");//项目期限
        //			money = intent.getStringExtra("money");//融资金额
        //			useMoney = intent.getStringExtra("useMoney");//可投余额
        //			stepamount = intent.getStringExtra("stepamount");//浮动金额
        //			rate = intent.getStringExtra("rate");//年化收益率
        //			loandate = intent.getStringExtra("loandate");//项目放款日期
        //			isCanUseCoupon = intent.getStringExtra("isCanUseCoupon");//是否可以使用抵用券
        //			isCanUsePlusCoupon = intent.getStringExtra("isCanUsePlusCoupon");//是否可以使用加息券
        //		}
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(TestQuestionFirstActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuestionList(LoginUserProvider.getUser(TestQuestionFirstActivity.this).getToken());
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        setTitle("");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(TestQuestionFirstActivity.this, "211001_fxcp_back_click");
                finish();
            }
        });
        viewpager = (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    protected void initData() {
        titleList = new ArrayList<TestTitleEntity>();
        viewPageradapter = new TestRollViewPager(TestQuestionFirstActivity.this, titleList);
        viewPageradapter.setmListening(this);
        viewpager.setAdapter(viewPageradapter);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewpager.getContext(), new AccelerateInterpolator());
            field.set(viewpager, scroller);
            scroller.setmDuration(500);
        } catch (Exception e) {
        }
    }

    //获取数据
    private void getQuestionList(final String token) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(TestQuestionFirstActivity.this));
        String url = Urls.GETQUESTIONLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                //					saveStr2Sd(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        //							ToastUtils.show(TestQuestionFirstActivity.this, jsonObject.getString("message"));
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        setTitle(dataObject.getString("name"));
                        JSONArray topicListArray = dataObject.getJSONArray("topicList");//题目集合

                        if (topicListArray.length() > 0) {
                            for (int i = 0; i < topicListArray.length(); i++) {
                                JSONObject obj = topicListArray.getJSONObject(i);
                                TestTitleEntity entity = new TestTitleEntity();
                                entity.setId(obj.getString("id"));
                                entity.setName(obj.getString("name"));
                                JSONArray answerListArray = obj.getJSONArray("answerList");//选项集合
                                List<OptionEntity> optionList = new ArrayList<OptionEntity>();
                                if (answerListArray.length() > 0) {
                                    for (int j = 0; j < answerListArray.length(); j++) {
                                        JSONObject optionObj = answerListArray.getJSONObject(j);
                                        OptionEntity optionEntity = new OptionEntity();
                                        optionEntity.setId(optionObj.getString("id"));
                                        optionEntity.setName(optionObj.getString("name"));
                                        if (j == 0) {
                                            optionEntity.setFlag(true);
                                        } else {
                                            optionEntity.setFlag(false);
                                        }
                                        optionList.add(optionEntity);
                                    }
                                }
                                entity.setTestOption(optionList);
                                titleList.add(entity);
                            }
                            viewPageradapter.notifyDataSetChanged();
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(TestQuestionFirstActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(TestQuestionFirstActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(TestQuestionFirstActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(TestQuestionFirstActivity.this);
                        util.put("isLogin", "");
                    } else {
                        ToastUtils.show(TestQuestionFirstActivity.this, jsonObject.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show(TestQuestionFirstActivity.this, "解析异常");
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                CustomProgress.CustomDismis();
                ToastUtils.show(TestQuestionFirstActivity.this, "请检查网络");
            }
        });
    }

    /**
     * 跳转到下一页
     */
    @Override
    public void onDown(int position) {
        page = position + 1;
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 跳转到上一页
     */
    @Override
    public void onUp(int position) {
        page = position - 1;
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 刷新数据
     */
    @Override
    public void onRefresh(int parentPos, int childPos, boolean flag) {
        List<OptionEntity> childlist = titleList.get(parentPos).getTestOption();
        for (OptionEntity ent : childlist) {
            ent.setFlag(false);
        }
        childlist.get(childPos).setFlag(flag);
        viewPageradapter.notifyDataSetChanged();
    }

    @Override
    public void onNextQuestion() {
        String result = "";
        StringBuffer sbuffer = new StringBuffer();
        for (TestTitleEntity entity : titleList) {
            String str = "";
            String pid = "";
            pid = entity.getId();
            for (OptionEntity child : entity.getTestOption()) {
                if (child.isFlag()) {
                    str = pid + "--" + child.getId() + ",";
                    break;
                }
            }
            sbuffer.append(str);
        }

        Log.i("TAG", sbuffer.toString());
        String tempStr = sbuffer.toString();
        result = tempStr.substring(0, tempStr.lastIndexOf(","));//需要上传的字符串
        saveUserAnswer(LoginUserProvider.getUser(TestQuestionFirstActivity.this).getToken(), result);
    }

    //提交数据
    private void saveUserAnswer(final String token, String answer) {
        CustomProgress.show(TestQuestionFirstActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(TestQuestionFirstActivity.this));
        String url = Urls.SAVEUSERANSWER;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("answer", answer);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        String type = jsonObject.getString("score");
                        //ToastUtils.show(TestQuestionFirstActivity.this,jsonObject.getString("message"));
                        getUserInfo(token, "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(TestQuestionFirstActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(TestQuestionFirstActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(TestQuestionFirstActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(TestQuestionFirstActivity.this);
                        util.put("isLogin", "");
                    } else {
                        ToastUtils.show(TestQuestionFirstActivity.this, jsonObject.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show(TestQuestionFirstActivity.this, "解析异常");
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                CustomProgress.CustomDismis();
                ToastUtils.show(TestQuestionFirstActivity.this, "请检查网络");
            }
        });
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(TestQuestionFirstActivity.this));
        String url = Urls.GETUSERINFO;
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
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserInfo info = new UserInfo();
                        info.setToken(token);
                        info.setPhone(LoginUserProvider.getUser(TestQuestionFirstActivity.this).getPhone());
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
                        String address = dataObj.getString("address");
                        info.setAddress(address);//地址
                        info.setEmergencyUser(dataObj.getString("emergencyUser"));//紧急联系人
                        info.setEmergencyTel(dataObj.getString("emergencyTel"));//紧急联系电话
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(TestQuestionFirstActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(TestQuestionFirstActivity.this);
                        util.put("isLogin", "isLogin");
                        dialog(dataObj.getString("userType"));
                    } else {
                        Toast.makeText(TestQuestionFirstActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TestQuestionFirstActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(TestQuestionFirstActivity.this, "请检查网络", 0).show();
            }
        });
    }

    // 提交成功的对话框
    private void dialog(String type) {
        dialogSuccess = new CommonDialog(TestQuestionFirstActivity.this, R.style.MyDialog, type);
        dialogSuccess.show();
        /*if (isInvestment.equals("0")) {//投资详情
            mHandler.sendEmptyMessageDelayed(1, 1500);
        } else {//安全设置
            mHandler.sendEmptyMessageDelayed(2, 1500);
        }*/
    }

    public class CommonDialog extends Dialog {

        private String title;
        private Context ctx;
        private String confirmTxt, cancelTxt;
        private String type;

        public CommonDialog(Context context, int themeResId, String type) {
            super(context, themeResId);
            this.ctx = context;
            this.type = type;
        }

        public CommonDialog(Context context, String title, String confirmButtonText, String cacelButtonText) {
            super(context);
            this.ctx = context;
            this.title = title;
            this.confirmTxt = confirmButtonText;
            this.cancelTxt = cacelButtonText;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
                SystemBarTintManager tintManager = new SystemBarTintManager(TestQuestionFirstActivity.this);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
            }
            initView();
        }

        private void initView() {
            LayoutInflater mInflater = LayoutInflater.from(ctx);
            View view = mInflater.inflate(R.layout.dialog_test_question, null);
            setContentView(view);
            TextView txt_pz = (TextView) view.findViewById(R.id.tv_test_dialog_text);
            LinearLayout ll_determine = (LinearLayout) view.findViewById(R.id.ll_determine);
            LinearLayout ll_anew = (LinearLayout) view.findViewById(R.id.ll_anew);
            ll_determine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.sendEmptyMessageDelayed(2, 100);
                }
            });
            ll_anew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSuccess.dismiss();
                    page = 0;
                    mHandler.sendEmptyMessageDelayed(0, 100);
                }
            });

            txt_pz.setText(type);
            Window dialogWin = getWindow();
            WindowManager.LayoutParams lp = dialogWin.getAttributes();
            DisplayMetrics dm = ctx.getResources().getDisplayMetrics();// 获取屏幕宽高
            lp.width = (int) (dm.widthPixels * 1.0);
            lp.height = (int) (dm.heightPixels);
            dialogWin.setAttributes(lp);
        }
    }


}
