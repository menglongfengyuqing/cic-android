package com.ztmg.cicmorgan.account.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 我的银行卡
 */

public class MyBankCardActivity extends BaseActivity implements View.OnClickListener {
    private TextView txt_bankcard, txt_updatebankcard;

    private RelativeLayout relativelayout_replace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybankcard);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(MyBankCardActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
        getBankInfo(LoginUserProvider.getUser(MyBankCardActivity.this).getToken(), "3");
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MyBankCardActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
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
    protected void initView() {
        setTitle("我的银行卡");
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(MyBankCardActivity.this, "306001_yhk_back_click");
                finish();
            }
        });
        setRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //联系客服
                onEvent(MyBankCardActivity.this, "306002_yhk_lxkf_click");
                ContactServiceDialog dialog = new ContactServiceDialog(MyBankCardActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });


        relativelayout_replace = (RelativeLayout) findViewById(R.id.relativelayout_replace);
        relativelayout_replace.setOnClickListener(this);
        txt_bankcard = (TextView) findViewById(R.id.txt_bankcard);
        txt_updatebankcard = (TextView) findViewById(R.id.txt_updatebankcard);
        txt_updatebankcard.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    public String date(String str) {
        //		String str = "weicc-20100107-00001";
        str = str.substring(str.length() - 4, str.length());
        return str;
    }

    //获取银行信息
    private void getBankInfo(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETCGBUSERBANKCARD;
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
                        String bindBankCardNo = dataObj.getString("bindBankCardNo");
                        String bankName = dataObj.getString("bankName");
                        String bankCode = dataObj.getString("bankCode");
                        String dayLimitAmount = dataObj.getString("dayLimitAmount");//日限额
                        String singleLimitAmount = dataObj.getString("singleLimitAmount");//每次限额
                        String limitAmountTxt = dataObj.getString("limitAmountTxt");//单日限额
                        txt_bankcard.setText(bankName + "(" + date(bindBankCardNo) + ")");

                    } else if (jsonObject.get("state").equals("5")) {//未绑卡
                        Toast.makeText(MyBankCardActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyBankCardActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyBankCardActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyBankCardActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyBankCardActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyBankCardActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyBankCardActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MyBankCardActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_updatebankcard://更改银行卡
                onEvent(MyBankCardActivity.this, "306003_yhk_change_click");
                String url = Urls.CHANGEBANKCARDH5;
                ModifyAccount(LoginUserProvider.getUser(MyBankCardActivity.this).getToken(), "3", url);
                break;
            case R.id.relativelayout_replace:
                ReplacePhoneDialog dialog = new ReplacePhoneDialog(MyBankCardActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
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
                    Intent intent = new Intent(MyBankCardActivity.this, OnlineContactWeActivity.class);
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

    //更换银行卡和预留手机号
    public class ReplacePhoneDialog extends Dialog {
        Context context;

        public ReplacePhoneDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ReplacePhoneDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_replace_phone);

            TextView tv_unbind_card = findViewById(R.id.tv_unbind_card);//解绑银行卡
            View v_line = findViewById(R.id.v_line);
            TextView tv_replace_card = (TextView) findViewById(R.id.tv_replace_card);//更换银行卡
            if(LoginUserProvider.getUser(MyBankCardActivity.this).getCertificateChecked().equals("2")){ //1未开户  2已开户
                if(LoginUserProvider.getUser(MyBankCardActivity.this).getIsBindBank().equals("1")){//1未绑定 2已绑定
                    tv_replace_card.setVisibility(View.VISIBLE);
                    v_line.setVisibility(View.VISIBLE);
                }else{
                    tv_replace_card.setVisibility(View.GONE);
                    v_line.setVisibility(View.GONE);
                }
            }
            tv_replace_card.setTextColor(context.getResources().getColor(R.color.text_34393c));
            //TextView tv_investment = (TextView) findViewById(R.id.tv_investment);//投资业务
            TextView tv_replace_phone = (TextView) findViewById(R.id.tv_replace_phone);//更换银行预留手机号
            tv_replace_phone.setTextColor(context.getResources().getColor(R.color.text_34393c));
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            //解绑银行卡
            tv_unbind_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UnBindCard(LoginUserProvider.getUser(MyBankCardActivity.this).getToken(),"1",Urls.UNTYINGCARD);
                    dismiss();
                }
            });
            tv_replace_card.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent bindCardIntent = new Intent(MyBankCardActivity.this, BindBankCardActivity.class);
                    bindCardIntent.putExtra("isBackAccount", "0");
                    startActivity(bindCardIntent);
//                    ModifyAccount(LoginUserProvider.getUser(MyBankCardActivity.this).getToken(), "3", url);
                    dismiss();
                }
            });
            tv_replace_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = Urls.CHANGEBANKPHONEH5;//更换预留手机号
//                    ModifyAccount(LoginUserProvider.getUser(MyBankCardActivity.this).getToken(), "3", url);
                    UnBindCard(LoginUserProvider.getUser(MyBankCardActivity.this).getToken(),"1",Urls.MODIFYMOBILEEXPAND);
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

    //更改银行卡
    private void ModifyAccount(String token, String from, String url) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindBankCardActivity.this));
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
                        String tm = dataObj.getString("tm");
                        String data = dataObj.getString("data");
                        String merchantId = dataObj.getString("merchantId");
                        Intent safetyIntent = new Intent(MyBankCardActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("data", data);
                        safetyIntent.putExtra("tm", tm);
                        safetyIntent.putExtra("merchantId", merchantId);
                        safetyIntent.putExtra("isBackAccount", "1");
                        safetyIntent.putExtra("title","更改银行卡");
                        startActivity(safetyIntent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyBankCardActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyBankCardActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyBankCardActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyBankCardActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyBankCardActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyBankCardActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MyBankCardActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }



    //解绑银行卡
    private void UnBindCard(String token, String from, final String url) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(BindBankCardActivity.this));
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
                        Intent safetyIntent = new Intent(MyBankCardActivity.this, BankH5Activity.class);
                        safetyIntent.putExtra("reqData", reqData);
                        safetyIntent.putExtra("platformNo", platformNo);
                        safetyIntent.putExtra("sign", sign);
                        safetyIntent.putExtra("keySerial", keySerial);
                        safetyIntent.putExtra("serviceName", serviceName);
                        if(url.contains("untyingCard")){
                            safetyIntent.putExtra("title","解绑银行卡");
                        }else{
                            safetyIntent.putExtra("title","更换银行预留手机号");
                        }
                        safetyIntent.putExtra("isBackAccount", "1");
                        startActivity(safetyIntent);
                        finish();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyBankCardActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyBankCardActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyBankCardActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyBankCardActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyBankCardActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyBankCardActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MyBankCardActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
