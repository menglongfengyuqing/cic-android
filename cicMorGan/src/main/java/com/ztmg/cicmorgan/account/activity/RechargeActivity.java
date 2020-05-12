package com.ztmg.cicmorgan.account.activity;

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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.pay.util.BaseHelper;
import com.ztmg.cicmorgan.pay.util.Constants;
import com.ztmg.cicmorgan.pay.util.PayOrder;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 充值
 *
 * @author pc
 */
public class RechargeActivity extends BaseActivity implements OnClickListener {
    private EditText et_recharge_money;
    private TextView tv_bank_name, tv_bank_num;
    private String money_order;
    private String oid_partner;
    private String risk_item;
    private String user_id;
    private String id_no;
    private String acct_name;
    private String no_order;
    private String dt_order, busi_partner, name_goods, valid_order;
    private String token;
    private String no_agree;
    public static RechargeActivity mContext;
    private String bindBankCardNo;//银行卡号
    private String bankName;//银行卡名字
    private String bankCode;
    private String notify_url;
    private String sign;
    private String id_type;
    private String info_order, sign_type;
    private Button bt_recharge;
    private TextView tv_limitation;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(RechargeActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_recharge);
        mContext = this;
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(RechargeActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginUserProvider.getUser(RechargeActivity.this) != null) {
            token = LoginUserProvider.getUser(RechargeActivity.this).getToken();
        }
        getBankInfo(token, "3");
        MobclickAgent.onResume(this); //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        progressHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长

    }

    public static RechargeActivity getInstance() {
        return mContext;
    }

    @Override
    protected void initView() {
        bt_recharge = (Button) findViewById(R.id.bt_recharge);
        bt_recharge.setOnClickListener(this);
        et_recharge_money = (EditText) findViewById(R.id.et_recharge_money);
        setPricePoint(et_recharge_money);
        et_recharge_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("0")) {
                    et_recharge_money.setText("");
                    bt_recharge.setBackgroundResource(R.drawable.bt_gray);
                    bt_recharge.setClickable(false);
                } else if (s.toString().equals("")) {
                    bt_recharge.setBackgroundResource(R.drawable.bt_gray);
                    bt_recharge.setClickable(false);
                } else {
                    bt_recharge.setBackgroundResource(R.drawable.bt_red);
                    bt_recharge.setClickable(true);
                }
            }
        });
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);
        tv_limitation = (TextView) findViewById(R.id.tv_limitation);
        setTitle("充值");
        //        setRightText("充值记录", new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                Intent intent = new Intent(RechargeActivity.this, RechargeRecordsActivity.class);
        //                startActivity(intent);
        //            }
        //        });
        setRight(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(RechargeActivity.this, "205002_chongzhi_lxkf_click");
                //联系客服
                ContactServiceDialog dialog = new ContactServiceDialog(RechargeActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(RechargeActivity.this, "205001_chongzhi_back_click");
                finish();
            }
        });

        //充值记录
        findViewById(R.id.tv_recharge_records).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(RechargeActivity.this, "205004_chongzhi_jilu_click");
                Intent intent = new Intent(RechargeActivity.this, RechargeRecordsActivity.class);
                startActivity(intent);
            }
        });

        //转账充值
        findViewById(R.id.tv_transfer_recharge).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(RechargeActivity.this, "205005_zzcz_click");
                Intent intent = new Intent(RechargeActivity.this, TransferActivity.class);
                startActivity(intent);
                finish();
            }
        });
        sc_content = (ScrollView) findViewById(R.id.sc_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    //获取银行信息
    private void getBankInfo(String token, String from) {
        //		CustomProgress.show(RechargeActivity.this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        progressHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RechargeActivity.this));
        //		String url = Urls.GETUSERBANKCARD;
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
                        bankCode = dataObj.getString("bankCode");
                        String dayLimitAmount = dataObj.getString("dayLimitAmount");//日限额
                        String singleLimitAmount = dataObj.getString("singleLimitAmount");//每次限额
                        String limitAmountTxt = dataObj.getString("limitAmountTxt");//单日限额
                        tv_bank_num.setText("(" + date(bindBankCardNo) + ")");
                        tv_bank_name.setText(bankName);
                        tv_limitation.setText(limitAmountTxt);
                    } else if (jsonObject.get("state").equals("5")) {//未绑卡
                        Toast.makeText(RechargeActivity.this, jsonObject.getString("message"), 0).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(RechargeActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RechargeActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RechargeActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(RechargeActivity.this);
                        //						LoginUserProvider.cleanDetailData(RechargeActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RechargeActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(RechargeActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RechargeActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                Toast.makeText(RechargeActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //快捷充值
    private void recharge(String token, String from, String amount) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RechargeActivity.this));
        String url = Urls.LANMAOSWIFTRECHARGE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("amount", amount);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                        Intent safetyIntent = new Intent(RechargeActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("reqData", reqData);
                        safetyIntent.putExtra("platformNo", platformNo);
                        safetyIntent.putExtra("sign", sign);
                        safetyIntent.putExtra("keySerial", keySerial);
                        safetyIntent.putExtra("serviceName", serviceName);
                        safetyIntent.putExtra("title","充值");
                        startActivity(safetyIntent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(RechargeActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RechargeActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RechargeActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(RechargeActivity.this);
                        //LoginUserProvider.cleanDetailData(RechargeActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RechargeActivity.this);
                        util.put("isLogin", "");

                    } else {
                        // Toast.makeText(RechargeActivity.this, jsonObject.getString("message"), 0).show();
                        ToastUtils.show(RechargeActivity.this, jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(RechargeActivity.this, "解析异常", 0).show();
                    ToastUtils.show(RechargeActivity.this, "解析异常");
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(RechargeActivity.this, "请检查网络", 0).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
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
        //		String str = "weicc-20100107-00001";
        str = str.substring(str.length() - 4, str.length());
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_recharge:
                onEvent(RechargeActivity.this, "205003_chongzhi_qrzf_btn_click");
                String amount = et_recharge_money.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    //Toast.makeText(RechargeActivity.this, "请填写充值金额", 0).show();
                    return;
                }
                if (!isNumeric(amount)) {
                    if (amount.indexOf(".") == -1) {
                        //Toast.makeText(RechargeActivity.this, "请检查充值金额是否为数字", 0).show();
                        ToastUtils.show(this, "请检查充值金额是否为数字");
                        return;
                    }
                }
                recharge(token, "3", amount);
                break;
            default:
                break;
        }
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
                    Intent intent = new Intent(RechargeActivity.this, OnlineContactWeActivity.class);
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
