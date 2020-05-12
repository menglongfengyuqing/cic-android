package com.ztmg.cicmorgan.account.activity;

import java.text.DecimalFormat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yintong.secure.c.p;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.CommonPopCityUtil;
import com.ztmg.cicmorgan.util.CommonPopCityUtil.ProvinceListener;
import com.ztmg.cicmorgan.util.CommonPopUtil;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.CommonPopUtil.CityListener;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 提现
 *
 * @author pc
 */
public class WithdrawCashActivity extends BaseActivity implements OnClickListener {
    private String token;
    private TextView tv_money, tv_bank_name, tv_bank_num, tv_free_times, tv_surplus_times;
    private EditText et_withdraw_cash_money;
    public static WithdrawCashActivity mContext;
    private com.ztmg.cicmorgan.util.CommonPopUtil con;
    private CommonPopCityUtil conCity;
    private String bindBankCardNo;
    private String bankName;
    private String codeId;//城市id
    private TextView tv_cash_times;//提现次数
    private String freeCash;
    private String availableAmount;
    private TextView tv_withdrawcash_charge;
    private Button bt_withdraw_cash;

    private ScrollView sc_content;
    private SlowlyProgressBar slowlyProgressBar;
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
        super.setContentView(R.layout.activity_withdraw_cash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(WithdrawCashActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        mContext = this;
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(WithdrawCashActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginUserProvider.getUser(WithdrawCashActivity.this) != null) {
            token = LoginUserProvider.getUser(WithdrawCashActivity.this).getToken();
        }
        getBankInfo(token, "3");
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    public static WithdrawCashActivity getInstance() {
        return mContext;
    }

    @Override
    protected void initView() {
        setTitle("提现");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(WithdrawCashActivity.this, "206001_tixiani_back_click");
                finish();
            }
        });
        setRight(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(WithdrawCashActivity.this, "206002_tixiani_lxkf_click");
                //联系客服
                ContactServiceDialog dialog = new ContactServiceDialog(WithdrawCashActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });
        tv_money = (TextView) findViewById(R.id.tv_money);//可提现金额
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);//银行卡名称
        tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);//银行卡卡号
        et_withdraw_cash_money = (EditText) findViewById(R.id.et_withdraw_cash_money);//提现金额
        setPricePoint(et_withdraw_cash_money);
        bt_withdraw_cash = (Button) findViewById(R.id.bt_withdraw_cash);
        bt_withdraw_cash.setOnClickListener(this);
        et_withdraw_cash_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    bt_withdraw_cash.setBackgroundResource(R.drawable.bt_gray);
                    bt_withdraw_cash.setClickable(false);
                } else {
                    bt_withdraw_cash.setBackgroundResource(R.drawable.bt_red);
                    bt_withdraw_cash.setClickable(true);
                }
            }
        });

        tv_cash_times = (TextView) findViewById(R.id.tv_cash_times);//提现次数
        tv_withdrawcash_charge = (TextView) findViewById(R.id.tv_withdrawcash_charge);

        sc_content = (ScrollView) findViewById(R.id.sc_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_withdraw_cash://提现按钮
                onEvent(WithdrawCashActivity.this, "206003_tixiani_qrtx_btn_click");
                //String token,String from,String amount,String branchBank,String cityCode,String busiPwd
                String money = et_withdraw_cash_money.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    Toast.makeText(WithdrawCashActivity.this, "提现金额不能为空", 0).show();
                    return;
                }
                if (!isNumeric(money)) {
                    if (money.indexOf(".") == -1) {
                        Toast.makeText(WithdrawCashActivity.this, "请检查提现金额是否为数字", 0).show();
                        return;
                    }
                }
                double moneyDou = Double.parseDouble(money);
                if (!TextUtils.isEmpty(availableAmount)) {
                    double availableAmountDou = Double.parseDouble(availableAmount);//可用余额
                    if (moneyDou > availableAmountDou) {
                        Toast.makeText(WithdrawCashActivity.this, "提现金额不能大于可用余额", 0).show();
                        return;
                    }
                }

                if (moneyDou < 1.00) {
                    Toast.makeText(WithdrawCashActivity.this, "提现金额不能小于1元", 0).show();
                    return;
                }
                WitchdrawCash(money, "4", token);
                break;
            default:
                break;
        }
    }

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    //申请提现
    private void applyWithdrawCash(String token, String from, final String amount,/*String branchBank,String cityCode,*/String busiPwd) {
        CustomProgress.show(WithdrawCashActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(WithdrawCashActivity.this));
        String url = Urls.CASH;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("amount", amount);
        //		params.put("branchBank", branchBank);
        //		params.put("cityCode", cityCode);
        params.put("busiPwd", busiPwd);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.get("state").equals("0")) {//申请提现成功
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String feeAmount = dataObj.getString("feeAmount");//手续费
                        Intent intent = new Intent(WithdrawCashActivity.this, WithdrawCashSuccess.class);
                        intent.putExtra("feeAmount", feeAmount);
                        intent.putExtra("amount", amount);//输入的金额
                        intent.putExtra("bankName", bankName);//银行卡名字
                        intent.putExtra("bankCode", bindBankCardNo);//银行卡号
                        startActivity(intent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(WithdrawCashActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(WithdrawCashActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(WithdrawCashActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //						LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(WithdrawCashActivity.this);
                        util.put("isLogin", "");

                    } else {//提现失败
                        Toast.makeText(WithdrawCashActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
            }
        });
    }

    //提现
    private void WitchdrawCash(String amount, String from, String token) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(WithdrawCashActivity.this));
        String url = Urls.WITHDRAWH5;
        RequestParams params = new RequestParams();
        params.put("amount", amount);
        params.put("from", from);
        params.put("token", token);
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
                        Intent safetyIntent = new Intent(WithdrawCashActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("reqData", reqData);
                        safetyIntent.putExtra("platformNo", platformNo);
                        safetyIntent.putExtra("sign", sign);
                        safetyIntent.putExtra("keySerial", keySerial);
                        safetyIntent.putExtra("serviceName", serviceName);
                        safetyIntent.putExtra("title","提现");
                        startActivity(safetyIntent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(WithdrawCashActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(WithdrawCashActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(WithdrawCashActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //								LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //								LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(WithdrawCashActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(WithdrawCashActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(WithdrawCashActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(WithdrawCashActivity.this, "请检查网络", 0).show();
            }
        });
    }


    //获取银行信息
    private void getBankInfo(String token, String from) {
        //		CustomProgress.show(WithdrawCashActivity.this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        progressHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(WithdrawCashActivity.this));
        //			String url = Urls.GETUSERBANKCARD;
        String url = Urls.GETCGBUSERBANKCARD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                //				CustomProgress.CustomDismis();
                mindex = 5;
                progressHandler.sendEmptyMessage(1);
                sc_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.get("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        bindBankCardNo = dataObj.getString("bindBankCardNo");
                        bankName = dataObj.getString("bankName");
                        String bankCode = dataObj.getString("bankCode");
                        freeCash = dataObj.getString("freeCash");//剩余提现次数
                        if (dataObj.has("availableAmount")) {
                            availableAmount = dataObj.getString("availableAmount");//剩余可用余额
                        }
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                        if (!TextUtils.isEmpty(availableAmount)) {
                            String availableAmountDot = decimalFormat.format(Double.parseDouble(availableAmount));
                            tv_money.setText(availableAmountDot);
                        }
                        tv_bank_num.setText("(" + date(bindBankCardNo) + ")");
                        tv_bank_name.setText(bankName);
                        tv_cash_times.setText(freeCash);
                        if (freeCash.equals("0")) {
                            tv_withdrawcash_charge.setText("1.00元");
                        }
                    } else if (jsonObject.get("state").equals("5")) {//未绑卡
                        Toast.makeText(WithdrawCashActivity.this, jsonObject.getString("message"), 0).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(WithdrawCashActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(WithdrawCashActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(WithdrawCashActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //							LoginUserProvider.cleanData(WithdrawCashActivity.this);
                        //							LoginUserProvider.cleanDetailData(WithdrawCashActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(WithdrawCashActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(WithdrawCashActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(WithdrawCashActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                Toast.makeText(WithdrawCashActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //判断输入的金额是否是数字
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String date(String str) {
        //			String str = "weicc-20100107-00001";
        str = str.substring(str.length() - 6, str.length());
        return str;
    }

    // 没有设置交易密码提示框
    private void dialog() {

        final Dialog mdialog = new Dialog(WithdrawCashActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dl_issetpas);

        TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) mdialog.findViewById(R.id.tv_no);
        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
        tv_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转设置交易密码界面
                Intent intent = new Intent(WithdrawCashActivity.this, BankPasActivity.class);
                startActivity(intent);
                mdialog.dismiss();
            }
        });
        tv_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //不设置交易密码，关闭弹框
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
                    Intent intent = new Intent(WithdrawCashActivity.this, OnlineContactWeActivity.class);
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
}
