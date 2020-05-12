package com.ztmg.cicmorgan.account.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.ztmg.cicmorgan.home.activity.NoticeListActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 账户消息详情
 *
 * @author pc
 */
public class MessageDetail extends BaseActivity {
    private TextView tv_title_text, tv_content;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(MessageDetail.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_message_detail);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MessageDetail.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
        String token = LoginUserProvider.getUser(MessageDetail.this).getToken();
        getData("3", token, id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        setTitle("消息详情");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(MessageDetail.this, "401001_znxx_xq_back_click");
                finish();
            }
        });

        tv_title_text = (TextView) findViewById(R.id.tv_title_text);
        tv_content = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    protected void initData() {
    }

    //获取数据
    private void getData(String from, String token, String letterId) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MessageDetail.this));
        String url = Urls.LETTERINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("letterId", letterId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String id = dataObj.getString("id");
                        String body = dataObj.getString("body");
                        tv_content.setText(body);
                        String title = dataObj.getString("title");
                        tv_title_text.setText(title);
                        String letterType = dataObj.getString("letterType");
                        String userId = dataObj.getString("userId");
                        String sendTime = dataObj.getString("sendTime");
                        String state = dataObj.getString("state");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MessageDetail.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MessageDetail.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MessageDetail.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //					LoginUserProvider.cleanData(ValueVoucherActivity.this);
                        //					LoginUserProvider.cleanDetailData(ValueVoucherActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(MessageDetail.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MessageDetail.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MessageDetail.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(MessageDetail.this, "请检查网络", 0).show();
            }
        });
    }
}
