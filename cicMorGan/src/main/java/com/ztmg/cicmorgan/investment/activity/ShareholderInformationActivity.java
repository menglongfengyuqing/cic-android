package com.ztmg.cicmorgan.investment.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.adapter.ShareholderInformationAdapter;
import com.ztmg.cicmorgan.investment.entity.ShareholderInformationEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 股东信息 dong dong
 * 2018年8月30日
 */

public class ShareholderInformationActivity extends BaseActivity {

    private ShareholderInformationAdapter adapter;
    private ShareholderInformationEntity entity;
    private ListView lv_shareholder;
    private List<ShareholderInformationEntity> shareholderList;
    private LinearLayout ll_no_message;
    private String projectid;
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
            SystemBarTintManager tintManager = new SystemBarTintManager(ShareholderInformationActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activty_shareholder_information);
        initView();
        initData();
        Intent intent = getIntent();
        projectid = intent.getStringExtra("projectid");
        getData("3", projectid);
    }

    @Override
    protected void initView() {
        setTitle("股东信息");
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_shareholder = (ListView) findViewById(R.id.lv_shareholder);
        ll_no_message = (LinearLayout) findViewById(R.id.ll_no_message);//无数据

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onPause() {
        super.onPause();
        progressHandler.removeMessages(1);
    }

    //获取股东数据
    private void getData(String from, String projectid) {
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        progressHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETZTMGLOANBASICINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("projectid", projectid);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mindex = 5;
                progressHandler.sendEmptyMessage(1);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        shareholderList = new ArrayList<ShareholderInformationEntity>();
                        JSONArray shareholdersListArray = dataObj.getJSONArray("shareholdersList");
                        if (shareholdersListArray.length() > 0) {
                            lv_shareholder.setVisibility(View.VISIBLE);
                            ll_no_message.setVisibility(View.GONE);
                            for (int i = 0; i < shareholdersListArray.length(); i++) {
                                JSONObject obj = shareholdersListArray.getJSONObject(i);
                                entity = new ShareholderInformationEntity();
                                if (obj.has("shareholdersType")) {
                                    entity.setShareholdersType(obj.getString("shareholdersType"));
                                }
                                if (obj.has("shareholdersCertType")) {
                                    entity.setShareholdersCertType(obj.getString("shareholdersCertType"));
                                }
                                if (obj.has("shareholdersName")) {
                                    entity.setShareholdersName(obj.getString("shareholdersName"));
                                }
                                shareholderList.add(entity);
                            }
                            adapter = new ShareholderInformationAdapter(ShareholderInformationActivity.this, shareholderList);
                            lv_shareholder.setAdapter(adapter);
                        } else {
                            lv_shareholder.setVisibility(View.GONE);
                            ll_no_message.setVisibility(View.VISIBLE);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(ShareholderInformationActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(ShareholderInformationActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(ShareholderInformationActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(InvestmentListActivity.this);
                        //LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(ShareholderInformationActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(ShareholderInformationActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_shareholder.setVisibility(View.GONE);
                    ll_no_message.setVisibility(View.VISIBLE);
                    //Toast.makeText(ShareholderInformationActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ShareholderInformationActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

