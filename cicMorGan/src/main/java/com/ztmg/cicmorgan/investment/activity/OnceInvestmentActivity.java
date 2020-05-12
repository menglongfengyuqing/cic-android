package com.ztmg.cicmorgan.investment.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.BankH5Activity;
import com.ztmg.cicmorgan.account.activity.BindBankCardActivity;
import com.ztmg.cicmorgan.account.activity.RechargeActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserAccountInfo;
import com.ztmg.cicmorgan.investment.entity.ValueVoucherEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.test.TestQuestionFirstActivity;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 立即投资
 *
 * @author pc
 */
public class OnceInvestmentActivity extends BaseActivity implements OnClickListener {
    private EditText et_investment_money;
    private TextView tv_available_money, tv_surplus_and_highest_investment, tv_no_use, tv_voucher, tv_voucher_money, tv_rate, tv_income;
    private RelativeLayout rl_have_voucher;
    private Button bt_once_investment;
    private ImageView iv_check_agreement;
    private boolean isSelect = false;

    private List<ValueVoucherEntity> availableValueVoucheList;//可以使用的优惠券
    private List<ValueVoucherEntity> valueList;// 优惠券集合；
    private String availableAmount;
    private int valueCode = 101;
    private double returnUsableCoupons;//优惠券界面返回回来的优惠金额
    private List<ValueVoucherEntity> usableList;//根据最大优惠金额选出的优惠券集合
    private String value;//点击优惠券返回
    private String inputMoney; //输入金额
    private int valueNum = 0;//优惠券的数量
    private double usableCoupons;//可使用优惠券集合的value和
    private String inputMoneyNoSpot;
    private int totalCoupons;//输入金额可以使用多少钱优惠券
    private double doubleIncome;
    private double inCome1;
    private String minamount, rate, span, loandate, projectid, isUseVoucher;
    private String balanceamount;// 剩余可投金额
    private String stepamount;// 浮动金额
    private String maxamount;
    private StringBuilder sb;
    //private String inputMoneyStr;//输入的金额
    private Dialog investmentResult;
    private Dialog investmentProcessdialog;//出借过程的弹框
    private String projectProductType;
    private static OnceInvestmentActivity mContext;
    private ScrollView sc_content;
    private SlowlyProgressBar slowlyProgressBar;
    private Dialog dialog;
    int mindex;
    int newProgress = 0;
    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mindex++;
            if (mindex >= 5) {
                newProgress += 10;
                slowlyProgressBar.onProgressChange(newProgress);
                progressHandler.sendEmptyMessage(1);

            } else {
                newProgress += 5;
                slowlyProgressBar.onProgressChange(newProgress);
                progressHandler.sendEmptyMessageDelayed(1, 1500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_once_investment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(OnceInvestmentActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        mContext = this;
        Intent intent = getIntent();
        projectid = intent.getStringExtra("projectid");
        //projectid = "0c41738b3f33401a85d6b8bf304d5391";
        initView();
        initData();


        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(OnceInvestmentActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
        initAccountData(LoginUserProvider.getUser(OnceInvestmentActivity.this).getToken(), "3");
        //		initData(LoginUserProvider.getUser(OnceInvestmentActivity.this).getToken(),"3");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
        progressHandler.removeMessages(1);
    }

    public static OnceInvestmentActivity getInstance() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @Override
    protected void initView() {
        setTitle("出借");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(OnceInvestmentActivity.this, "303001_chujie_back_click");
                finish();
            }
        });
        //联系客服
        setRight(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(OnceInvestmentActivity.this, "303002_chujie_lxkf_click");
                ContactServiceDialog dialog = new ContactServiceDialog(OnceInvestmentActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }

        });

        et_investment_money = (EditText) findViewById(R.id.et_investment_money);//出借金额
        tv_available_money = (TextView) findViewById(R.id.tv_available_money);//可用余额
        findViewById(R.id.tv_recharge).setOnClickListener(this);//充值
        tv_surplus_and_highest_investment = (TextView) findViewById(R.id.tv_surplus_and_highest_investment);//剩余金额以及最高出借金额
        tv_no_use = (TextView) findViewById(R.id.tv_no_use);//优惠券无可用
        rl_have_voucher = (RelativeLayout) findViewById(R.id.rl_have_voucher);//优惠券有可用
        rl_have_voucher.setOnClickListener(this);
        tv_voucher = (TextView) findViewById(R.id.tv_voucher);//优惠券张数
        tv_voucher_money = (TextView) findViewById(R.id.tv_voucher_money);//优惠券抵扣金额
        tv_rate = (TextView) findViewById(R.id.tv_rate);//出借利率
        tv_income = (TextView) findViewById(R.id.tv_income);//预期出借利息
        bt_once_investment = (Button) findViewById(R.id.bt_once_investment);//确认出借
        bt_once_investment.setOnClickListener(this);
        findViewById(R.id.rl_check_agreement).setOnClickListener(this);
        iv_check_agreement = (ImageView) findViewById(R.id.iv_check_agreement);//知悉并同意协议
        findViewById(R.id.tv_agreement).setOnClickListener(this);
        findViewById(R.id.tv_electronic_signature).setOnClickListener(this);
        findViewById(R.id.tv_risk_hint).setOnClickListener(this);
        findViewById(R.id.tv_sex_hint).setOnClickListener(this);

        usableList = new ArrayList<>();
        et_investment_money.addTextChangedListener(new EditTextWatcher());

        sc_content = (ScrollView) findViewById(R.id.sc_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();

    }

    @Override
    protected void initData() {

    }

    /**
     * 输入金额的时候，来自动请求优惠劵
     */
    private class EditTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //et_investment_money.setCursorVisible(true);
            LogUtil.e("00000000000000000000000000000000000000");
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                    s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                    et_investment_money.setText(s);
                    et_investment_money.setSelection(s.length());
                    //Toast.makeText(BaseActivity.this, "小数点后最多输入两位", 0).show();
                    //Toast.makeText(mContext, "小数点后最多输入两位", 0).show();
                    ToastUtils.show(mContext, "小数点后最多输入两位");
                    LogUtil.e("1111111111111111111111111111111111");
                    return;
                }
            }
            if (s.toString().trim().substring(0).equals(".")) {
                //s = "0" + s;
                et_investment_money.removeTextChangedListener(this);
                et_investment_money.setText("");
                et_investment_money.addTextChangedListener(new EditTextWatcher());
                LogUtil.e("222222222222222222222222222222");
                //et_investment_money.setSelection(2);
            }

            if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                LogUtil.e("33333333333333333333333333333");
                if (!s.toString().substring(1, 2).equals(".")) {
                    LogUtil.e("4444444444444444444444444");
                    et_investment_money.setText(s.subSequence(0, 1));
                    et_investment_money.setSelection(1);
                    return;
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            LogUtil.e("5555555555555555555555555555555555");
        }

        private double inputMoneyDou;
        private double inputMoneySecond;

        @Override
        public void afterTextChanged(Editable s) {
            LogUtil.e("6666666666666666666666666666666666");
            if (s.toString().equals("0")) {
                et_investment_money.setHint("请输入100的整数倍");
                et_investment_money.setText("");
                bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                bt_once_investment.setClickable(false);
                bt_once_investment.setText("确认");
            } else if (s.toString().equals("")) {
                bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                bt_once_investment.setClickable(false);
                bt_once_investment.setText("确认");
            } /*else if (s.toString().equals(".")) {
                bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                bt_once_investment.setClickable(false);
                bt_once_investment.setText("确认");
                et_investment_money.setText("");
            } */ else {
                if (s.toString().contains(".")) {
                    if ((s.length() - 1 - s.toString().indexOf(".")) <= 2) {
                        //s1 = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        LogUtil.e("=============================" + (s.length() - 1 - s.toString().indexOf(".")));
                        bt_once_investment.setBackgroundResource(R.drawable.bt_red);
                        bt_once_investment.setClickable(true);
                        bt_once_investment.setText("确认出借" + s + "元");
                    }
                } else {
                    bt_once_investment.setBackgroundResource(R.drawable.bt_red);
                    bt_once_investment.setClickable(true);
                    bt_once_investment.setText("确认出借" + s + "元");
                }
            }
            availableValueVoucheList = new ArrayList<>();
            value = "";
            inputMoney = s.toString().trim();
            if (inputMoney.equals(".")) {
                et_investment_money.setText(s.toString().trim().substring(0, s.toString().indexOf(".")));
                inputMoney = "";
            }
            if (inputMoney != null && !inputMoney.equals("")) {
                inputMoneyDou = Double.parseDouble(inputMoney);//输入的金额double
                if (inputMoneySecond > 0 && inputMoneySecond != inputMoneyDou) {
                    valueNum = 0;
                    usableCoupons = 0;
                    if (valueList != null && valueList.size() > 0) {
                        for (ValueVoucherEntity entity : valueList) {
                            String limitAmoun = entity.getLimitAmount();
                            double limitAmounInt = Double.parseDouble(limitAmoun);//优惠券的起投金额
                            double minamountDou = Double.parseDouble(minamount);//起投金额
                            if (inputMoneyDou >= limitAmounInt && inputMoneyDou >= minamountDou) {
                                inputMoneySecond = inputMoneyDou;
                                valueNum = valueNum + 1;
                                //可以使用的优惠券筛选出来的集合
                                availableValueVoucheList.add(entity);
                            }
                        }
                    } else {
                        tv_no_use.setVisibility(View.VISIBLE);
                        rl_have_voucher.setVisibility(View.GONE);
                    }
                    if (inputMoney.contains(".")) {
                        inputMoneyNoSpot = inputMoney.substring(0, inputMoney.indexOf(".")).toString();
                    } else {
                        inputMoneyNoSpot = inputMoney;
                    }
                    //最终可用优惠券的金额
                    totalCoupons = (int) Double.parseDouble(inputMoneyNoSpot) / 1000 * 10;
                    //totalCoupons = Integer.parseInt(inputMoneyNoSpot)/1000*10;
                    //usableCoupons = totalCoupons;//使用可用优惠券的金额
                    //usableList = new ArrayList<ValueVoucherEntity>();
                    usableList.clear();
                    for (int i = 0; i < availableValueVoucheList.size(); i++) {
                        int value = Integer.parseInt(availableValueVoucheList.get(i).getValue().substring(0, availableValueVoucheList.get(i).getValue().indexOf(".")).toString());
                        if (totalCoupons >= value) {
                            usableList.add(availableValueVoucheList.get(i));
                            totalCoupons -= value;
                            if (totalCoupons == 0) {
                                break;
                            }
                        }
                    }
                    if (usableList.size() > 0) {
                        for (int j = 0; j < usableList.size(); j++) {
                            usableCoupons += Double.parseDouble(usableList.get(j).getValue());
                        }
                    }
                } else {
                    if (valueList != null && valueList.size() > 0) {
                        usableCoupons = 0;
                        for (ValueVoucherEntity entity : valueList) {
                            String limitAmoun = entity.getLimitAmount();
                            double limitAmounInt = Double.parseDouble(limitAmoun);//优惠券的起投金额
                            double minamountDou = Double.parseDouble(minamount);//起投金额
                            if (inputMoneyDou >= limitAmounInt && inputMoneyDou >= minamountDou) {
                                inputMoneySecond = inputMoneyDou;
                                valueNum = valueNum + 1;
                                //可以使用的优惠券筛选出来的集合
                                availableValueVoucheList.add(entity);
                            }
                        }
                        if (inputMoney.contains(".")) {
                            inputMoneyNoSpot = inputMoney.substring(0, inputMoney.indexOf(".")).toString();
                        } else {
                            inputMoneyNoSpot = inputMoney;
                        }
                        totalCoupons = Integer.parseInt(inputMoneyNoSpot) / 1000 * 10;//使用优惠券的总金额
                        //usableCoupons = totalCoupons;//使用可用优惠券的金额
                        //usableList = new ArrayList<ValueVoucherEntity>();
                        usableList.clear();
                        for (int i = 0; i < availableValueVoucheList.size(); i++) {
                            int value = Integer.parseInt(availableValueVoucheList.get(i).getValue().substring(0, availableValueVoucheList.get(i).getValue().indexOf(".")).toString());
                            if (totalCoupons >= value) {
                                usableList.add(availableValueVoucheList.get(i));
                                totalCoupons -= value;
                                if (totalCoupons == 0) {
                                    break;
                                }
                            }
                        }
                        if (usableList.size() > 0) {
                            for (int j = 0; j < usableList.size(); j++) {
                                usableCoupons += Double.parseDouble(usableList.get(j).getValue());
                            }
                        }
                    } else {
                        tv_no_use.setVisibility(View.VISIBLE);
                        rl_have_voucher.setVisibility(View.GONE);
                    }
                }
            } else {
                valueNum = 0;
            }
            if (valueNum == 0) {
                tv_no_use.setVisibility(View.VISIBLE);
                rl_have_voucher.setVisibility(View.GONE);
                tv_voucher.setText("已选择" + valueNum + "张(最优方案)");
                tv_voucher_money.setText("抵扣支付金额" + usableCoupons + "元");
            } else {
                tv_no_use.setVisibility(View.GONE);
                rl_have_voucher.setVisibility(View.VISIBLE);
                tv_voucher.setText("已选择" + usableList.size() + "张(最优方案)");
                tv_voucher_money.setText("抵扣支付金额" + usableCoupons + "元");
                //						tv_voucher.setText("您有"+valueNum+"张优惠券可用");
            }

            double onceInvestMoney = Double.parseDouble(minamount);//起投金额
            if (!TextUtils.isEmpty(inputMoney)) {
                if (inputMoney != null && !inputMoney.equals("") && inputMoneyDou >= 0.0) {
                    //rl_pop.setVisibility(View.GONE);
                    //(投资金额*年化收益率)*(项目期限+投资日期至放款日期之间的天数))/(365*100)\,改版之前的算法
                    double rateMoney = Double.parseDouble(rate);
                    double projectDate = Double.parseDouble(span);
                    //loandate
                    //String time = "2016-06-17";
                    //预期收益=出借金额*预期出借利率/365*出借期限
                    double days = Double.parseDouble(getDays(loandate) + "");
                    //inCome1 = ((inputMoneyDou*rateMoney)*(projectDate+days))/(365*100);
                    doubleIncome = inputMoneyDou * rateMoney / 100 / 365;
                    BigDecimal b = new BigDecimal(doubleIncome);
                    String strIncom = b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                    doubleIncome = Double.parseDouble(strIncom);
                    inCome1 = doubleIncome * projectDate;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String distanceString = decimalFormat.format(inCome1);//format 返回的是字符串
                    tv_income.setText(distanceString);
                } else {
                    tv_income.setText("0.00");
                }
            } else {
                tv_income.setText("0.00");
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_have_voucher://优惠券
                onEvent(OnceInvestmentActivity.this, "303004_chujie_yhq_click");
                if (valueNum != 0) {
                    Intent mIntent = new Intent(OnceInvestmentActivity.this, ValueVoucherActivity.class);
                    mIntent.putExtra("inputmoney", inputMoney);
                    mIntent.putExtra("usableList", (Serializable) usableList);
                    mIntent.putExtra("usableCoupons", usableCoupons);
                    mIntent.putExtra("projectid", projectid);

                    startActivityForResult(mIntent, valueCode);
                }
                break;
            case R.id.rl_check_agreement://知悉并同意
                if (isSelect) {
                    iv_check_agreement.setBackgroundResource(R.drawable.icon_checkbox_nor);
                    isSelect = false;
                } else {
                    iv_check_agreement.setBackgroundResource(R.drawable.icon_checkbox_sltd);
                    isSelect = true;
                }
                break;
            case R.id.bt_once_investment://确认
                onEvent(OnceInvestmentActivity.this, "303005_chujie_qrcj_tab_click");
                String isOnceBindBank = LoginUserProvider.getUser(OnceInvestmentActivity.this).getIsBindBank();
                if (isOnceBindBank.equals("1")) {//未绑卡
                    dialog();
                } else if (isOnceBindBank.equals("2")) {
                    if (LoginUserProvider.getUser(OnceInvestmentActivity.this).getIsTest().equals("0")) {//未测试
                        testDialog();
                    } else {
                        String minamountInt = minamount.substring(0, minamount.indexOf(".")).toString();//起投金额
                        String maxamountInt = maxamount.substring(0, maxamount.indexOf(".")).toString();//单笔最高可出借金额
                        String balanceamountInt = balanceamount/*.substring(0, balanceamount.indexOf(".")).toString()*/;//剩余可投余额
                        String mStepamount = stepamount.substring(0, stepamount.indexOf(".")).toString();//浮动金额
                        //立即投资
                        String investmentMoney = et_investment_money.getText().toString().trim();
                        int stepamountInt = Integer.parseInt(mStepamount);
                        if (!TextUtils.isEmpty(investmentMoney)) {
                            //if(!inputMoney.equals(tv_available_money.getText().toString().trim())){
                            if (balanceamountInt.compareTo(minamountInt) > 0) {//可投余额大于起投金额
                                //double availableAmountDou = Double.parseDouble(availableAmount);//账户余额
                                double availableAmountDou = Double.parseDouble(tv_available_money.getText().toString().trim());
                                //if(inputMoney.compareTo(availableAmount)>0){//投资金额小于可用余额
                                double inputMoneyDou = Double.parseDouble(investmentMoney);
                                if (inputMoneyDou > availableAmountDou) {//投资金额小于可用余额
                                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "账户余额不足，请先进行充值");
                                    customToast.show();
                                    return;
                                }
                            }
                            if (Double.parseDouble(balanceamountInt) > Double.parseDouble(minamountInt)) {//可投余额大于起投金额
                                if (!investmentMoney.equals(balanceamountInt)) {//输入金额与可投金额不相等时，必须是递增金额的倍数，输入金额与可投金额相等时，不是递增金额的倍数也可以通过
                                    if (balanceamountInt.toString().contains(".")) {
                                        if ((investmentMoney.length() - 1 - investmentMoney.toString().indexOf(".")) <= 0) {
                                            //s1 = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                                            CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请输入" + stepamountInt + "的整数倍金额");
                                            customToast.show(2000);
                                            return;
                                        }
                                    }
                                    if ((Double.parseDouble(investmentMoney) - Double.parseDouble(minamountInt)) % stepamountInt != 0 && Double.parseDouble(balanceamountInt) != Double.parseDouble(investmentMoney)) {
                                        //投资金额须为起投金额与递增金额（1000）整数倍之和
                                        CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请输入" + stepamountInt + "的整数倍金额");
                                        customToast.show(2000);
                                        return;
                                    }
                                }
                            }
                            if (Double.parseDouble(balanceamountInt) < Double.parseDouble(minamountInt)) {
                                //可投余额小于起投金额
                                if (Double.parseDouble(investmentMoney) > Double.parseDouble(balanceamountInt)) {
                                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "所投金额大于剩余可投金额，请一次性输入剩余可投金额");
                                    customToast.show(2000);
                                    return;
                                } else if (Double.parseDouble(investmentMoney) < Double.parseDouble(balanceamountInt)) {
                                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "所投金额小于剩余可投金额，请一次性输入剩余可投金额");
                                    customToast.show(2000);
                                    return;
                                }
                            } else {
                                if (Double.parseDouble(investmentMoney) < Double.parseDouble(minamountInt)) {
                                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "所投金额请不小于起投金额");
                                    customToast.show(2000);
                                    return;
                                }
                                if (Double.parseDouble(investmentMoney) > Double.parseDouble(balanceamountInt)) {  //输入金额大于可投金额
                                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "输入金额请不大于项目剩余可投金额");
                                    customToast.show(2000);
                                    return;
                                }
                                if (Double.parseDouble(investmentMoney) > Double.parseDouble(maxamountInt)) {
                                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "所投金额请不大于单笔最高出借金额");
                                    customToast.show(2000);
                                    return;
                                }
                            }
                            if (!isSelect) {
                                CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请勾选并同意相关协议后再进行出借");
                                customToast.show(2000);
                                return;
                            }
                            //剩余金额-输入金额 <  起投金额 = 提示
                            if (Double.parseDouble(balanceamountInt) - Double.parseDouble(investmentMoney) < 100 && Double.parseDouble(balanceamountInt) != Double.parseDouble(investmentMoney)) {
                                //提示填入完整的剩余钱
                                //BigDecimal b1 = new BigDecimal("3.14");
                                DialogPrompt(balanceamountInt);
                                dialog.show();
                                return;
                            }
                            if (isUseVoucher.equals("1")) {//是否可用抵用券（0-是   1-否）,是否可用加息券（0-是   1-否）
                                usableList.clear();
                            }
                            newOnceInvestment(LoginUserProvider.getUser(OnceInvestmentActivity.this).getToken(), investmentMoney, projectid, usableList, "3");
                        } else {
                            CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请输入出借金额");
                            customToast.show(2000);
                        }
                    }
                }

                break;
            case R.id.tv_recharge://充值
                onEvent(OnceInvestmentActivity.this, "303003_chujie_chongzhi_click");
                String isBindBank = LoginUserProvider.getUser(OnceInvestmentActivity.this).getIsBindBank();
                if (isBindBank.equals("1")) {//未绑卡
                    dialog();//1充值
                } else if (isBindBank.equals("2")) {//已绑定,充值
                    Intent rechargeIntent = new Intent(OnceInvestmentActivity.this, RechargeActivity.class);
                    startActivity(rechargeIntent);
                }
                break;
            case R.id.tv_agreement://出借协议  区分安心投(INVESTMINEAGREEMENT)和供应链
                onEvent(OnceInvestmentActivity.this, "303006_chujie_xieyi_wenjian_click");
                if (projectProductType.equals("1")) {//安心投
                    Intent agreeageIntent = new Intent(OnceInvestmentActivity.this, AgreementActivity.class);
                    agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENT);
                    startActivity(agreeageIntent);
                } else if (projectProductType.equals("2")) {//供应链
                    Intent agreeageIntent = new Intent(OnceInvestmentActivity.this, AgreementActivity.class);
                    agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENTSCF);
                    startActivity(agreeageIntent);
                }
                break;
            case R.id.tv_risk_hint:  //风险提示书
                onEvent(OnceInvestmentActivity.this, "303006_chujie_xieyi_wenjian_click");
                Intent riskHintFirstIntent = new Intent(OnceInvestmentActivity.this, AgreementActivity.class);
                riskHintFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskHintFirstIntent.putExtra("title", "出借人网络借贷风险提示");
                startActivity(riskHintFirstIntent);
                break;
            case R.id.tv_sex_hint:  //禁止性行为
                onEvent(OnceInvestmentActivity.this, "303006_chujie_xieyi_wenjian_click");
                Intent sexHintIntent = new Intent(OnceInvestmentActivity.this, AgreementActivity.class);
                sexHintIntent.putExtra("path", Urls.ZTPROTOCOLPROHIBIT);
                sexHintIntent.putExtra("title", "出借人网络借贷禁止性行为提示");
                startActivity(sexHintIntent);
                break;
            case R.id.tv_electronic_signature:  //电子签章
                onEvent(OnceInvestmentActivity.this, "303006_chujie_xieyi_wenjian_click");
                Intent electronicSignatureIntent = new Intent(OnceInvestmentActivity.this, AgreementActivity.class);
                electronicSignatureIntent.putExtra("path", Urls.ZTELECTRONICSIGNATURE);
                electronicSignatureIntent.putExtra("title", "电子签章证书");
                startActivity(electronicSignatureIntent);
                break;
            default:
                break;
        }
    }

    //计算两个时间之间的天数

    public long getDays(String time) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = sdf.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long beforeTime = date.getTime();
        long days;
        if (currentTime == beforeTime) {
            days = (beforeTime - currentTime) / 1000 / 3600 / 24;
        } else {
            days = (long) (((beforeTime - currentTime) / 1000 / 3600 / 24) + 1.0);
        }
        //		long days=(beforeTime-currentTime)/1000/ 3600 / 24;
        return days;
    }

    //获取优惠券
    private void getValueVoucher(String token, String from, String state, String projectid) {

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETUSERAWARDSHISTORYLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("state", state);
        params.put("projectId", projectid);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONArray dataArr = dataObj.getJSONArray("awardsList");
                        valueList = new ArrayList<ValueVoucherEntity>();
                        if (dataArr.length() > 0) {
                            ValueVoucherEntity valueEntity;
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject valueObj = dataArr.getJSONObject(i);
                                String limitAmount = valueObj.getString("limitAmount");

                                valueEntity = new ValueVoucherEntity();
                                valueEntity.setId(valueObj.getString("id"));
                                valueEntity.setOverdueDate(valueObj.getString("overdueDate"));
                                valueEntity.setGetDate(valueObj.getString("getDate"));
                                valueEntity.setLimitAmount(limitAmount);
                                valueEntity.setValue(valueObj.getString("value"));
                                valueEntity.setType(valueObj.getString("type"));//1抵用券2加息券
                                valueEntity.setState(valueObj.getString("state"));
                                if (valueObj.has("spans")) {
                                    valueEntity.setSpans(valueObj.getString("spans"));//什么期限可以使用
                                }
                                valueList.add(valueEntity);

                            }
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(OnceInvestmentActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "11");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "11");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(OnceInvestmentActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, jsonObject.getString("message"));
                        customToast.show(2000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "解析异常");
                    customToast.show(2000);
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请检查网络");
                customToast.show(2000);
            }
        });
    }

    //获取可用余额
    private void initAccountData(final String token, String from) {
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        progressHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETCGBUSERACCOUNT;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                mindex = 5;
                progressHandler.sendEmptyMessage(1);
                sc_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                getData("3", projectid);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserAccountInfo info = new UserAccountInfo();
                        info.setTotalAmount(dataObj.getString("totalAmount"));
                        BigDecimal bd = new BigDecimal(dataObj.getString("availableAmount"));
                        String dialogAvailableAmount = bd.toPlainString();
                        info.setAvailableAmount(dialogAvailableAmount);
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

                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        String availableAmountDot = decimalFormat.format(Double.parseDouble(dialogAvailableAmount));
                        if (tv_available_money != null) {
                            tv_available_money.setText(availableAmountDot);//可用余额
                        }

                        LoginUserProvider.setUserDetail(info);

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(OnceInvestmentActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "11");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "11");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(OnceInvestmentActivity.this);
                        util.put("isLogin", "");
                    } else {
                        CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, jsonObject.getString("message"));
                        customToast.show(2000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "解析异常");
                    customToast.show(2000);
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请检查网络");
                customToast.show(2000);
            }
        });
    }

    //立即投资 新接口
    private void newOnceInvestment(String token, final String amount, String projectId, List<ValueVoucherEntity> idList, String from) {
        investmentProcessDialog();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.NEWUSERTOINVEST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("amount", amount);
        params.put("projectId", projectId);
        params.put("from", from);
        sb = new StringBuilder();
        if (idList.size() > 0) {
            List<String> a = new ArrayList<String>();
            for (int i = 0; i < idList.size(); i++) {
                a.add(idList.get(i).getId());
                sb.append(idList.get(i).getId());//拼接单引号,到数据库后台用in查询.
                if (i != idList.size() - 1) {//前面的元素后面全拼上",",最后一个元素后不拼
                    sb.append(",");
                }
            }
        }
        params.put("vouchers", sb);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                investmentProcessdialog.dismiss();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String tm = dataObj.getString("tm");
                        String data = dataObj.getString("data");
                        String merchantId = dataObj.getString("merchantId");

                        Intent safetyIntent = new Intent(OnceInvestmentActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("data", data);
                        safetyIntent.putExtra("tm", tm);
                        safetyIntent.putExtra("merchantId", merchantId);
                        startActivity(safetyIntent);

                        //ToastUtils.show(OnceInvestmentActivity.this, "可以出借");

                        //	InvestmentResultDialog("出借成功");
                        //Intent intent = new Intent(InvestmentDetailActivity.this,InvestmentSuccessActivity.class);
                        //intent.putExtra("proName", name);//项目名称
                        //intent.putExtra("amount", amount);//投资金额
                        //intent.putExtra("income", inCome1);//预期收益
                        //startActivity(intent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(OnceInvestmentActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(OnceInvestmentActivity.this);
                        util.put("isLogin", "");

                    } else {
                        if (jsonObject.getString("message").equals("null")) {
                            InvestmentResultDialog("系统错误");//投资失败
                        } else {
                            InvestmentResultDialog(jsonObject.getString("message"));//投资失败
                        }
                        //startActivity(new Intent(InvestmentDetailActivity.this,InvestmentFailActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "解析异常");
                    customToast.show(2000);
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                //CustomProgress.CustomDismis();
                investmentProcessdialog.dismiss();
                CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请检查网络");
                customToast.show(2000);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == valueCode) {
            returnUsableCoupons = 0;
            List<ValueVoucherEntity> valueVoucherList = (List<ValueVoucherEntity>) data.getSerializableExtra("valueVoucherList");
            usableList.clear();
            if (valueVoucherList != null && valueVoucherList.size() > 0) {
                for (int i = 0; i < valueVoucherList.size(); i++) {
                    if (valueVoucherList.get(i).isCheck()) {
                        returnUsableCoupons += Double.parseDouble(valueVoucherList.get(i).getValue());
                        ValueVoucherEntity entity = new ValueVoucherEntity();
                        entity.setId(valueVoucherList.get(i).getId());
                        entity.setCheck(valueVoucherList.get(i).isCheck());
                        entity.setValue(valueVoucherList.get(i).getValue());
                        entity.setGetDate(valueVoucherList.get(i).getGetDate());
                        entity.setLimitAmount(valueVoucherList.get(i).getLimitAmount());
                        entity.setOverdueDate(valueVoucherList.get(i).getOverdueDate());
                        entity.setState(valueVoucherList.get(i).getState());
                        entity.setType(valueVoucherList.get(i).getType());
                        usableList.add(entity);
                    }
                }

                tv_voucher.setText("已选择" + usableList.size() + "张(最优方案)");
                tv_voucher_money.setText("抵扣支付金额" + returnUsableCoupons + "元");
            } else {
                tv_voucher.setText("已选择0张(最优方案)");
                tv_voucher_money.setText("抵扣支付金额0元");
            }
        }

    }

    //获取项目详情
    private void getData(final String from, final String projectid) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETPROJECTINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("projectid", projectid);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                        if (dataObj.has("isCanUseCoupon")) {
                            isUseVoucher = dataObj.getString("isCanUseCoupon");// 是否可用抵用券（0-是
                            // 1-否）
                            if (isUseVoucher.equals("1")) {//是否可用抵用券（0-是   1-否）
                                tv_no_use.setVisibility(View.VISIBLE);
                                rl_have_voucher.setVisibility(View.GONE);
                            } else {
                                getValueVoucher(LoginUserProvider.getUser(OnceInvestmentActivity.this).getToken(), "3", "1", projectid);
                            }
                        }
                        if (dataObj.has("isCanUsePlusCoupon")) {
                            String isCanUsePlusCoupon = dataObj
                                    .getString("isCanUsePlusCoupon");// 是否可用加息券（0-是
                            // 1-否）
                        }
                        if (dataObj.has("loandate")) {
                            loandate = dataObj.getString("loandate");// 项目放款日期
                        }
                        if (dataObj.has("proState")) {
                            String prostate = dataObj.getString("proState");// 是否可以投资
                            //							if (prostate != null && prostate.equals("4")) {
                            //
                            //								bt_once_investment.setBackgroundResource(R.drawable.bt_red);
                            //								bt_once_investment.setClickable(true);
                            //								if (!TextUtils.isEmpty(loandate) && !loandate.equals("null")) {
                            //									boolean isBidders = TimeUtil.compareInverstmentListNowTime(loandate);
                            //									if (isBidders) {
                            //										bt_once_investment.setBackgroundResource(R.drawable.bt_red);
                            //										bt_once_investment.setClickable(true);
                            //									} else {
                            //										bt_once_investment.setText("已到期");
                            //										bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                            //										bt_once_investment.setClickable(false);
                            //									}
                            //								}
                            //							} else if (prostate != null && prostate.equals("3")) {
                            //								bt_once_investment.setText("即将上线");
                            //								bt_once_investment.setBackgroundResource(R.drawable.bt_red);
                            //								bt_once_investment.setClickable(false);
                            //							} else if (prostate != null && prostate.equals("6")) {
                            //								bt_once_investment.setText("还款中");
                            //								bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                            //								bt_once_investment.setClickable(false);
                            //							} else if (prostate != null && prostate.equals("7")) {
                            //								bt_once_investment.setText("已还完");
                            //								bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                            //								bt_once_investment.setClickable(false);
                            //							} else if (prostate != null && prostate.equals("5")) {
                            //								bt_once_investment.setText("还款中");
                            //								bt_once_investment.setBackgroundResource(R.drawable.bt_gray);
                            //								bt_once_investment.setClickable(false);
                            //							}
                        }
                        if (dataObj.has("stepamount")) {
                            stepamount = dataObj.getString("stepamount");
                        }

                        if (dataObj.has("percentage")) {
                            String percentage = dataObj.getString("percentage");
                        }
                        if (dataObj.has("minamount")) {
                            minamount = dataObj.getString("minamount");// 起投金额
                            String minamountDot = decimalFormat.format(Double.parseDouble(minamount));
                        }
                        if (dataObj.has("maxamount")) {  //最大可投金额
                            maxamount = dataObj.getString("maxamount");
                        }
                        if (dataObj.has("balanceamount")) {
                            balanceamount = dataObj.getString("balanceamount");
                        }
                        //起投金额10000元，最高出借金额10000元
                        tv_surplus_and_highest_investment.setText("起投金额" + minamount.substring(0, minamount.indexOf(".")).toString() + "元,剩余最高可投金额" + balanceamount/*.substring(0, balanceamount.indexOf(".")).toString() */ + "元");
                        if (dataObj.has("countdowndate")) {
                            String countdowndate = dataObj.getString("countdowndate");
                        }
                        if (dataObj.has("amount")) {// 项目金额
                            String amount = dataObj.getString("amount");
                        }
                        if (dataObj.has("rate")) {// 年化收益
                            rate = dataObj.getString("rate");
                            String rateDot = decimalFormat.format(Double.parseDouble(rate));
                            tv_rate.setText(rateDot + "%");
                        }
                        if (dataObj.has("currentamount")) {
                            String currentamount = dataObj.getString("currentamount");// 当前投资金额
                        }

                        if (dataObj.has("span")) {
                            span = dataObj.getString("span");
                        }
                        if (dataObj.has("projectProductType")) {//1安心投 2 供应链
                            projectProductType = dataObj.getString("projectProductType");
                        }
                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(
                                OnceInvestmentActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("")
                                && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(
                                    OnceInvestmentActivity.this,
                                    UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(OnceInvestmentActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(OnceInvestmentActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, jsonObject.getString("message"));
                        customToast.show(2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "解析异常");
                    customToast.show(2000);
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomToastUtils customToast = new CustomToastUtils(OnceInvestmentActivity.this, "请检查网络");
                customToast.show(2000);
            }
        });
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
                    Intent intent = new Intent(OnceInvestmentActivity.this, OnlineContactWeActivity.class);
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

    // 投资成功或失败
    private void InvestmentResultDialog(String result) {
        investmentResult = new Dialog(OnceInvestmentActivity.this, R.style.MyDialog);
        investmentResult.setContentView(R.layout.dl_investment_result);

        TextView tv_investment_result = (TextView) investmentResult.findViewById(R.id.tv_investment_result);
        tv_investment_result.setText(result);
        TextView tv_text = (TextView) investmentResult.findViewById(R.id.tv_text);
        tv_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                investmentResult.dismiss();
            }
        });
        investmentResult.show();
    }

    // 出借过程的对话框
    private void investmentProcessDialog() {
        investmentProcessdialog = new Dialog(OnceInvestmentActivity.this, R.style.MyDialogUpVersion);
        investmentProcessdialog.setContentView(R.layout.dialog_investment);
        investmentProcessdialog.setCancelable(false);
        investmentProcessdialog.show();
    }

    // 没有做风险测评提示框
    private void testDialog() {

        final Dialog testdialog = new Dialog(OnceInvestmentActivity.this, R.style.MyDialog);
        testdialog.setContentView(R.layout.dl_istest);

        TextView tv_yes = (TextView) testdialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) testdialog.findViewById(R.id.tv_no);
        tv_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到测试题界面
                Intent testIntent = new Intent(OnceInvestmentActivity.this, TestQuestionFirstActivity.class);
                testIntent.putExtra("isInvestment", "0");
                startActivity(testIntent);
                testdialog.dismiss();
            }
        });
        tv_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //不做测试，关闭弹框
                testdialog.dismiss();
            }
        });
        testdialog.show();
    }

    // 没有绑定银行卡提示框
    private void dialog() {

        final Dialog mdialog = new Dialog(this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dl_isbindbank);

        TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) mdialog.findViewById(R.id.tv_no);
        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
        tv_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到绑定银行卡界面
                Intent intent = new Intent(OnceInvestmentActivity.this, BindBankCardActivity.class);
                intent.putExtra("isBackAccount", "0");
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

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }


    // 温馨提示的dialog
    public void DialogPrompt(final String balanceamountInt) {

        dialog = new Dialog(mContext, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_prompt);
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_money = (TextView) dialog.findViewById(R.id.tv_money);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) dialog.findViewById(R.id.tv_no);
        int i = Double.valueOf(balanceamountInt).intValue();
        int a = i / 100 * 100;
        tv_money.setText("您当前出借后剩余可投金额小于" + 100 + "元，可一次性出借全部剩余可投金额，或者出借不大于" + (a - 100) + "元。");
        tv_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                et_investment_money.setText(balanceamountInt);
                dialog.dismiss();
            }
        });
        //        //如果没有登录，按物理返回键 就把当前activity 也关闭掉
        //        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        //            @Override
        //            public void onCancel(DialogInterface dialog) {
        //                finish();
        //            }
        //        });

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_investment_money.setText("");
                dialog.dismiss();
            }
        });
        // dialog.show();
    }
}
