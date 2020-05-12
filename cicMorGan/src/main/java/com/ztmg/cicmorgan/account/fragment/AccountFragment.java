package com.ztmg.cicmorgan.account.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BuildConfig;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.MyApplication;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.AccountMessageActivity;
import com.ztmg.cicmorgan.account.activity.AccountSumActivity;
import com.ztmg.cicmorgan.account.activity.AnsactionRecordsActivity;
import com.ztmg.cicmorgan.account.activity.BindBankCardActivity;
import com.ztmg.cicmorgan.account.activity.MyInvestmentListActivity;
import com.ztmg.cicmorgan.account.activity.BackPaymentPlanActivity;
import com.ztmg.cicmorgan.account.activity.NewMessageActivity;
import com.ztmg.cicmorgan.account.activity.RechargeActivity;
import com.ztmg.cicmorgan.account.activity.SecuritySettingActivity;
import com.ztmg.cicmorgan.account.activity.ValueVoucherActivity;
import com.ztmg.cicmorgan.account.activity.WithdrawCashActivity;
import com.ztmg.cicmorgan.account.picture.CircleImageView;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseFragment;
import com.ztmg.cicmorgan.calendar.models.BeanDate;
import com.ztmg.cicmorgan.entity.UserAccountInfo;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.AboutWeActivity;
import com.ztmg.cicmorgan.more.activity.IntegralShopActivity;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.test.TestQuestionFirstActivity;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.AndroidUtil;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.UpdataInfoParser;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 账户
 *
 * @author pc
 */
public class AccountFragment extends BaseFragment implements OnClickListener {
    private TextView tv_account_sum, tv_available_balance, tv_accumulated_income, tv_frozen_money;
    private UserInfo info;
    private String isBindBank;
    private String availableAmount;//可用余额
    private SignDialog dialog;
    private TextView tv_sign;
    private TextView tv_sign_days;//签到天数以及积分个数
    private String continuousTime;//连续签到天数
    private String integral;//签到积分
    private String isSigned;//是否签到
    private TextView tv_my_score, tv_my_voucherNum;
    private List<BeanDate> paymentList;//回款计划
    private BeanDate entity;
    private TextView tv_user_phone;
    private CircleImageView iv_header;
    private TextView tv_type;
    private TextView tv_version_number;
    private SlowlyProgressBar slowlyProgressBar;
    private ScrollView sc_account;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private int oldVersion;
    private int newVersion;
    private String appversion;
    private String isForce;
    private List<String> featureList;
    private String downUrl;
    private Dialog mdialog;
    private ImageView iv_message;
    private RelativeLayout re_freeze;
    private RelativeLayout relativelayout_freeze;
    private LinearLayout account_fragment;
    boolean isGetData = false;
    int mindex;
    int newProgress = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mindex++;
            if (mindex >= 5) {
                newProgress += 10;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessage(1);

            } else {
                newProgress += 5;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        View view = inflater.inflate(R.layout.fragment_account, null);

        initView(view);
        initData();
        //getDataAmount(LoginUserProvider.getUser(getActivity()).getToken(), "3");
        return view;

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //   进入当前Fragment
        if (enter && !isGetData) {
            isGetData = true;
            //这里可以做网络请求或者需要的数据刷新操作
            //Common.mBoolean = false;
            //relativelayout_freeze.setVisibility(View.INVISIBLE);
            //LogUtil.e("--------------------------onCreateAnimation---fragment切换回来的调用-----------");
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isGetData) {
            //这里可以做网络请求或者需要的数据刷新操作
            isGetData = true;
        }
        if (LoginUserProvider.getUser(getActivity()) != null) {
            if (TextUtils.isEmpty(LoginUserProvider.getUser(getActivity()).getRealName()) || LoginUserProvider.getUser(getActivity()).getRealName().equals("null")) {
                tv_user_phone.setText(LoginUserProvider.getUser(getActivity()).getPhone());
            } else {
                tv_user_phone.setText(LoginUserProvider.getUser(getActivity()).getRealName());
            }
            //getUserInfo(LoginUserProvider.getUser(AccountMessageActivity.this).getToken(),"3");

            mImageLoader = ImageLoaderUtil.getImageLoader();
            mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_defaultheadimage, false, false, false);
            mImageLoader.displayImage(LoginUserProvider.getUser(getActivity()).getAvatarPath(), iv_header, mDisplayImageOptions);

            if (TextUtils.isEmpty(LoginUserProvider.getUser(getActivity()).getUserType())) {//未测评
                tv_type.setText("立即测评");
                tv_type.setTextColor(getResources().getColor(R.color.text_a11c3f));

            } else {
                tv_type.setText(LoginUserProvider.getUser(getActivity()).getUserType());
                tv_type.setTextColor(getResources().getColor(R.color.text_989898));
            }
            isReadLetter(LoginUserProvider.getUser(getActivity()).getToken(), "3");
            initData(LoginUserProvider.getUser(getActivity()).getToken(), "3");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        isGetData = false;
    }

    //初始化视图
    private void initView(View v) {
        sc_account = (ScrollView) v.findViewById(R.id.sc_account);
        /*sc_account.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                relativelayout_freeze.setVisibility(View.INVISIBLE);
                Common.mBoolean = false;
            }
        });*/

        relativelayout_freeze = (RelativeLayout) v.findViewById(R.id.relativelayout_freeze);
        re_freeze = (RelativeLayout) v.findViewById(R.id.re_freeze);
        //re_freeze.setOnClickListener(this);

        tv_account_sum = (TextView) v.findViewById(R.id.tv_account_sum);
        tv_available_balance = (TextView) v.findViewById(R.id.tv_available_balance);
        tv_accumulated_income = (TextView) v.findViewById(R.id.tv_accumulated_income);
        tv_frozen_money = (TextView) v.findViewById(R.id.tv_frozen_money);

        iv_message = (ImageView) v.findViewById(R.id.iv_message);
        iv_message.setOnClickListener(this);
        v.findViewById(R.id.ll_withdraw_cash).setOnClickListener(this);
        v.findViewById(R.id.ll_recharge).setOnClickListener(this);
        v.findViewById(R.id.ll_account_sum).setOnClickListener(this);
        v.findViewById(R.id.ll_ansaction_records).setOnClickListener(this);
        //		v.findViewById(R.id.ll_my_investment).setOnClickListener(this);
        v.findViewById(R.id.ll_value_voucher).setOnClickListener(this);
        //		v.findViewById(R.id.ll_request_friend).setOnClickListener(this);
        //		v.findViewById(R.id.ll_setting).setOnClickListener(this);
        //		tv_sign = (TextView) v.findViewById(R.id.tv_sign);
        //		tv_sign.setOnClickListener(this);
        v.findViewById(R.id.ll_integral).setOnClickListener(this);
        //		v.findViewById(R.id.ll_public_donations).setOnClickListener(this);//公益捐助
        //		v.findViewById(R.id.ll_incestment_safe).setOnClickListener(this);//安心投
        //		v.findViewById(R.id.ll_supply_chain).setOnClickListener(this);//供应链
        v.findViewById(R.id.ll_my_investment).setOnClickListener(this);//我的出借
        v.findViewById(R.id.rl_account).setOnClickListener(this);//消息
        tv_my_score = (TextView) v.findViewById(R.id.tv_my_score);//我的积分
        tv_my_voucherNum = (TextView) v.findViewById(R.id.tv_my_voucherNum);//我的优惠券个数
        v.findViewById(R.id.ll_back_payment_plan).setOnClickListener(this);//回款计划
        v.findViewById(R.id.tv_tip).setOnClickListener(this);
        tv_user_phone = (TextView) v.findViewById(R.id.tv_user_phone);
        iv_header = (CircleImageView) v.findViewById(R.id.iv_header);
        tv_type = (TextView) v.findViewById(R.id.tv_type);
        v.findViewById(R.id.rl_test).setOnClickListener(this);
        v.findViewById(R.id.rl_safe_setting).setOnClickListener(this);
        v.findViewById(R.id.rl_about_we).setOnClickListener(this);
        v.findViewById(R.id.rl_version_number).setOnClickListener(this);//版本号
        tv_version_number = (TextView) v.findViewById(R.id.tv_version_number);//版本号
        v.findViewById(R.id.rl_contact_customer_service).setOnClickListener(this);

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) v.findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();

        //String login_pwd = ACache.get(mActivity).getAsString("login_pwd");
    }

    public void initData() {
        oldVersion = AndroidUtil.getAppVersionCode(getActivity());
        tv_version_number.setText("v " + AndroidUtil.getAppVersionName(getActivity()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_message:
                onEvent(getActivity(), "104003_news_click");
                Intent mIntent = new Intent(getActivity(), NewMessageActivity.class);
                startActivity(mIntent);
                break;
            case R.id.ll_withdraw_cash://提现
                onEvent(getActivity(), "104006_tixian_click");
                if (isBindBank != null) {
                    if (isBindBank.equals("1")) {//未绑卡
                        dialog();//0提现
                    } else if (isBindBank.equals("2")) {//已绑定,提现
                        Intent intent = new Intent(getActivity(), WithdrawCashActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.ll_recharge://充值
                onEvent(getActivity(), "104005_chongzhi_click");
                if (isBindBank != null) {
                    if (isBindBank.equals("1")) {//未绑卡
                        dialog();//1充值
                    } else if (isBindBank.equals("2")) {//已绑定,充值
                        Intent rIntent = new Intent(getActivity(), RechargeActivity.class);
                        startActivity(rIntent);
                    }
                }
                break;
            case R.id.ll_account_sum://账户总览
                onEvent(getActivity(), "104004_wode_zichan_click");
                Intent aIntent = new Intent(getActivity(), AccountSumActivity.class);
                startActivity(aIntent);
                break;
            case R.id.ll_ansaction_records://交易记录
                onEvent(getActivity(), "104010_jyjl_click");
                Intent nIntent = new Intent(getActivity(), AnsactionRecordsActivity.class);
                startActivity(nIntent);
                break;
            case R.id.ll_value_voucher://优惠劵
                onEvent(getActivity(), "104008_yhq_click");
                Intent vIntent = new Intent(getActivity(), ValueVoucherActivity.class);
                startActivity(vIntent);
                break;
            case R.id.ll_integral://我的积分
                onEvent(getActivity(), "104011_wode_jifen_click");
                Intent integralIntent = new Intent(getActivity(), IntegralShopActivity.class);
                startActivity(integralIntent);
                break;
            case R.id.ll_my_investment://我的出借
                onEvent(getActivity(), "104007_wode_chujie_click");
                Intent myInvestmentIntent = new Intent(getActivity(), MyInvestmentListActivity.class);
                startActivity(myInvestmentIntent);
                break;
            case R.id.rl_account:
                onEvent(getActivity(), "104002_grxx_click");
                Intent intent = new Intent(getActivity(), AccountMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_back_payment_plan://回款计划
                onEvent(getActivity(), "104009_hkrl_click");
                Intent backPaymentIntent = new Intent(getActivity(), BackPaymentPlanActivity.class);
                //backPaymentIntent.putExtra("paymentlist", (Serializable) paymentList);
                startActivity(backPaymentIntent);
                break;
            case R.id.rl_test://风险测评
                onEvent(getActivity(), "104012_fxcp_click");
                Intent teamIntent = new Intent(getActivity(), TestQuestionFirstActivity.class);
                teamIntent.putExtra("isInvestment", "1");
                startActivity(teamIntent);
                break;
            case R.id.rl_safe_setting://安全设置
                onEvent(getActivity(), "104013_aqsz_click");
                Intent safeIntent = new Intent(getActivity(), SecuritySettingActivity.class);
                startActivity(safeIntent);
                break;
            case R.id.rl_about_we://关于我们
                onEvent(getActivity(), "104014_about_click");
                Intent aboutWeIntent = new Intent(getActivity(), AboutWeActivity.class);
                startActivity(aboutWeIntent);
                break;
            case R.id.rl_version_number://版本号
                requestVersionNum("3");
                break;
            case R.id.rl_contact_customer_service://联系客服
                onEvent(getActivity(), "104015_lxkf_click");
                ContactServiceDialog dialog = new ContactServiceDialog(getActivity(), R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(getActivity(), AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            //            case R.id.re_freeze:
            //                if (!Common.mBoolean) {
            //                    relativelayout_freeze.setVisibility(View.VISIBLE);
            //                    Common.mBoolean = true;
            //                } else {
            //                    relativelayout_freeze.setVisibility(View.INVISIBLE);
            //                    Common.mBoolean = false;
            //                }
            //                break;
            default:
                break;
        }
    }

    //初始化数据
    private void initData(String token, String from) {
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);

        //CustomProgress.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        //String url = Urls.GETUSERACCOUNT;
        String url = Urls.GETCGBUSERACCOUNT;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                sc_account.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserAccountInfo info = new UserAccountInfo();
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                        BigDecimal bdTotal = new BigDecimal(dataObj.getString("totalAmount")); //账户总额
                        info.setTotalAmount(bdTotal.toPlainString());
                        BigDecimal bd = new BigDecimal(dataObj.getString("availableAmount"));  //可用余额
                        info.setAvailableAmount(bd.toPlainString());
                        info.setCashAmount(dataObj.getString("cashAmount"));
                        info.setRechargeAmount(dataObj.getString("rechargeAmount"));
                        info.setFreezeAmount(dataObj.getString("freezeAmount"));
                        info.setTotalInterest(dataObj.getString("totalInterest"));
                        info.setCurrentAmount(dataObj.getString("currentAmount"));
                        info.setRegularDuePrincipal(dataObj.getString("regularDuePrincipal"));
                        info.setRegularDueInterest(dataObj.getString("regularDueInterest"));
                        info.setRegularTotalAmount(dataObj.getString("regularTotalAmount"));
                        info.setRegularTotalInterest(dataObj.getString("regularTotalInterest"));
                        info.setCurrentTotalInterest(dataObj.getString("currentTotalInterest"));
                        info.setCurrentYesterdayInterest(dataObj.getString("currentYesterdayInterest"));
                        info.setReguarYesterdayInterest(dataObj.getString("reguarYesterdayInterest"));
                        LoginUserProvider.setUserDetail(info);
                        String totalAmountDot = decimalFormat.format(Double.parseDouble(dataObj.getString("totalAmount")));
                        tv_account_sum.setText(totalAmountDot);
                        availableAmount = dataObj.getString("availableAmount");

                        String availableAmountDot = decimalFormat.format(Double.parseDouble(availableAmount));
                        tv_available_balance.setText(availableAmountDot);//可用余额

                        //double incom = Double.parseDouble(dataObj.getString("regularTotalInterest"))+Double.parseDouble(dataObj.getString("currentTotalInterest"));
                        double incom = Double.parseDouble(dataObj.getString("regularTotalInterest"));
                        String incomDot = decimalFormat.format(Double.parseDouble(String.valueOf(incom)));
                        tv_accumulated_income.setText(incomDot);

                        double freezeAmount = Double.parseDouble(dataObj.getString("freezeAmount"));
                        String freezeAmountDot = decimalFormat.format(Double.parseDouble(String.valueOf(freezeAmount)));
                        tv_frozen_money.setText(freezeAmountDot);

                        getUserInfo(LoginUserProvider.getUser(getActivity()).getToken(), "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                sc_account.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 没有绑定银行卡提示框
    private void dialog() {

        final Dialog mdialog = new Dialog(getActivity(), R.style.MyDialog);
        mdialog.setContentView(R.layout.dl_isbindbank);

        TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) mdialog.findViewById(R.id.tv_no);
        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
        tv_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到绑定银行卡界面
                Intent intent = new Intent(getActivity(), BindBankCardActivity.class);
                intent.putExtra("isBackAccount", "1");
                startActivity(intent);
                mdialog.dismiss();
            }
        });
        tv_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //不绑定银行卡，关闭弹框
                mdialog.dismiss();
            }
        });
        iv_dialog_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

    //签到弹出框
    public class SignDialog extends Dialog {
        Context context;

        public SignDialog(Context context) {
            super(context);
            this.context = context;
        }

        public SignDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_sign);
            initView1();
        }

        private void initView1() {
            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
            tv_sign_days = (TextView) findViewById(R.id.tv_sign_days);
            tv_sign_days.setText("已连续签到" + continuousTime + "天获得" + integral + "积分");
            parent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
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
                        isBindBank = dataObj.getString("cgbBindBankCard");
                        info.setIsBindBank(dataObj.getString("cgbBindBankCard"));//是否绑定银行卡 1未绑定 2已绑定
                        info.setGesturePwd(dataObj.getString("gesturePwd"));//是否设置过手势密码，0设置1已设置
                        info.setAddress(dataObj.getString("address"));//地址
                        info.setEmergencyUser(dataObj.getString("emergencyUser"));//紧急联系人
                        info.setEmergencyTel(dataObj.getString("emergencyTel"));//紧急联系电话
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        isSigned = dataObj.getString("signed");
                        info.setSigned(isSigned);//是否签到 3：未签到，2：已签到
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setScore(dataObj.getString("score"));//积分
                        info.setVoucherNum(dataObj.getString("voucherNum"));//优惠券个数
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        tv_my_score.setText(dataObj.getString("score") + "分");
                        tv_my_voucherNum.setText("优惠券（" + dataObj.getString("voucherNum") + "张）");
                        if (LoginUserProvider.class != null) {
                            LoginUserProvider.setUser(info);
                            LoginUserProvider.saveUserInfo(getActivity());
                        }
                        //下一个界面的回款计划
                        //getBackPaymentPlan(LoginUserProvider.getUser(getActivity()).getToken(), "3");
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //回款计划
    private void getBackPaymentPlan(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETNEWUSERINTERESTCOUNT;
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
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        paymentList = new ArrayList<BeanDate>();
                        if (dataArray.length() > 0) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONArray array = dataArray.getJSONArray(i);
                                entity = new BeanDate();
                                //entity.setDate(array.getString(0));
                                //entity.setMoney(array.getString(1));
                                paymentList.add(entity);
                            }
                            //calendarView.setPaymentList(paymentList);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
                //if(paymentList.size()>0){
                //	for(BeanDate bean:paymentList){
                //		dateList.add(bean.getDate());
                //	}
                //}
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //获取版本号
    private void requestVersionNum(String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.APPVERSION;
        RequestParams params = new RequestParams();
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        newVersion = dataObj.getInt("versionCode");//版本号
                        appversion = dataObj.getString("appversion");//版本name
                        JSONArray array = dataObj.getJSONArray("featureList");//内容
                        if (dataObj.has("isForce")) {
                            isForce = dataObj.getString("isForce");//0强制 1非强制
                        }
                        featureList = new ArrayList<String>();
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                featureList.add(array.getString(i));
                            }
                        }
                        downUrl = dataObj.getString("url");//升级地址
                        //downUrl = "http://192.168.1.14:8081/cic_loan/CicMorGan_120_jiagu_sign.apk";
                        //downUrl = "https://www.cicmorgan.com/upload/CicMorGan_121_jiagu_sign.apk";
                        //dialog("0");
                        if (oldVersion != 0 && newVersion != 0) {
                            if (oldVersion < newVersion) {
                                dialog(isForce);
                            } else {
                                Toast.makeText(getActivity(), "当前版本是最新版本,无需更新", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    //					dialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 版本更新的对话框
    private void dialog(String isForce) {//0强制 1非强制
        mdialog = new Dialog(getActivity(), R.style.MyDialogUpVersion);
        mdialog.setContentView(R.layout.dialog_version);
        mdialog.setCancelable(false);
        TextView tv_content = (TextView) mdialog.findViewById(R.id.tv_content);
        LinearLayout ll_no_update = (LinearLayout) mdialog.findViewById(R.id.ll_no_update);//暂不更新
        Button bt_force_update = (Button) mdialog.findViewById(R.id.bt_force_update);//强制更新
        //0强制 1非强制
        if (isForce.equals("0")) {
            ll_no_update.setVisibility(View.GONE);
            bt_force_update.setVisibility(View.VISIBLE);
        } else if (isForce.equals("1")) {
            ll_no_update.setVisibility(View.VISIBLE);
            bt_force_update.setVisibility(View.GONE);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < featureList.size(); i++) {
            sb.append(featureList.get(i) + "\n");
        }
        tv_content.setText(sb.toString());

        //立即更新
        mdialog.findViewById(R.id.bt_new).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                downLoadApk(downUrl);
                mdialog.dismiss();
            }
        });
        //立即更新
        mdialog.findViewById(R.id.bt_force_update).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                downLoadApk(downUrl);
                mdialog.dismiss();
            }
        });
        //稍暂不更新
        mdialog.findViewById(R.id.bt_not_update).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

    /*
 * 从服务器中下载APK
 */
    protected void downLoadApk(final String downloadURL) {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                   File file = UpdataInfoParser.getFileFromServer(downloadURL, pd);
                    sleep(3000);
                    installApk(file);
                    tv_version_number.setText("v " + newVersion);
                    pd.dismiss();
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * 判断是否是8.0系统,是的话需要获取此权限，判断开没开，没开的话处理未知应用来源权限问题,否则直接安装
     */
//    public void checkIsAndroidO(File file) {
//        if (Build.VERSION.SDK_INT >= 26) {
//            //PackageManager类中在Android Oreo版本中添加了一个方法：判断是否可以安装未知来源的应用
//            boolean b = getActivity().getPackageManager().canRequestPackageInstalls();
//            if (b) {
//                Log.i("ztmg","开始安装");
//                //安装应用的逻辑(写自己的就可以)
//                installApk(file);
//            } else {
//                //请求安装未知应用来源的权限
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
//            }
//        } else {
//            Log.i("ztmg","版本<26，开始安装");
//            installApk(file);
//        }
//    }


    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(getActivity(), "com.ztmg.cicmorgan.fileProvider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //i.setAction("android.intent.action.VIEW");
        //i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //i.setDataAndType(Uri.parse("file:///" + file.getPath()), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
//        DoCacheUtil util = DoCacheUtil.get(getActivity());
//        util.put("isUpdate", "");
    }



    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     * checkSelfPermission方法是在用来判断是否app已经获取到某一个权限
     * shouldShowRequestPermissionRationale方法用来判断是否
     * 显示申请权限对话框，如果同意了或者不在询问则返回false
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        getActivity(), perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    //联系客服弹框
    public class ContactServiceDialog extends Dialog {
        Context context;

        public ContactServiceDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ContactServiceDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_contact_service);

            TextView tv_on_line = (TextView) findViewById(R.id.tv_on_line);//在线客服
            tv_on_line.setTextColor(context.getResources().getColor(R.color.text_34393c));
            TextView tv_investment = (TextView) findViewById(R.id.tv_investment);//投资业务
            TextView tv_loan = (TextView) findViewById(R.id.tv_loan);//借款业务
            tv_loan.setTextColor(context.getResources().getColor(R.color.text_34393c));
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_on_line.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OnlineContactWeActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            tv_investment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(Intent.ACTION_CALL);
                    mIntent.setData(Uri.parse("tel:4006669068"));
                    startActivity(mIntent);
                    dismiss();
                }
            });
            tv_loan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006669068"));
                    startActivity(intent);
                    dismiss();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //取消
                    dismiss();
                }
            });
        }
    }

    //是否有未读消息
    private void isReadLetter(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.LETTERSTATE;
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
                        //letterState    1 有未读消息  0  没有未读消息
                        String letterState = dataObj.getString("letterState");
                        if (letterState.equals("1")) {
                            iv_message.setBackgroundResource(R.drawable.icon_msg_have);
                        } else if (letterState.equals("0")) {
                            iv_message.setBackgroundResource(R.drawable.icon_msg_nor);
                        }
                    } else {
                        iv_message.setBackgroundResource(R.drawable.icon_msg_nor);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
