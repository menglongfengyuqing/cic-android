package com.ztmg.cicmorgan.account.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.AnsactionRecordsAdapter;
import com.ztmg.cicmorgan.account.adapter.RechargeRecordAdapter;
import com.ztmg.cicmorgan.account.entity.AnsactionRecordsEntity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 充值记录
 *
 * @author pc
 */
public class RechargeRecordsActivity extends BaseActivity implements OnRefreshListener2<ListView> {
    private PullToRefreshListView ll_recharge_record;
    private RechargeRecordAdapter adapter;
    private List<AnsactionRecordsEntity> rechargeList;
    private List<AnsactionRecordsEntity> rechargeTotalList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private static int from = 3;//来源
    private static int type = 0;//类型
    private String TokenStr;
    private int last;
    private RelativeLayout rl_no_recharge_message;

    private RelativeLayout rl_content;
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
            SystemBarTintManager tintManager = new SystemBarTintManager(RechargeRecordsActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_recharge_records);
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(RechargeRecordsActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void initView() {
        rl_no_recharge_message = (RelativeLayout) findViewById(R.id.rl_no_recharge_message);
        rechargeTotalList = new ArrayList<>();
        ll_recharge_record = (PullToRefreshListView) findViewById(R.id.ll_recharge_record);
        ll_recharge_record.setMode(Mode.BOTH);
        ll_recharge_record.setOnRefreshListener(this);
        adapter = new RechargeRecordAdapter(RechargeRecordsActivity.this, rechargeTotalList);
        ll_recharge_record.setAdapter(adapter);
        setTitle("充值记录");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(RechargeRecordsActivity.this, "307001_czjl_back_click");
                finish();
            }
        });

        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    private void initData(String token, final int pageNo, int pageSize, int from, int type, final int RequestCode) {
        //		CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        progressHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RechargeRecordsActivity.this));
        String url = Urls.GETCGBUSERTRANSDETAILLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("from", from);
        params.put("type", type);
        client.post(url, params, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                //				CustomProgress.CustomDismis();
                mindex = 5;
                progressHandler.sendEmptyMessage(1);
                rl_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        last = dataObj.getInt("last");
                        rechargeList = new ArrayList<AnsactionRecordsEntity>();
                        JSONArray array = dataObj.getJSONArray("translist");
                        if (array.length() > 0) {
                            ll_recharge_record.setVisibility(View.VISIBLE);
                            rl_no_recharge_message.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                AnsactionRecordsEntity entity = new AnsactionRecordsEntity();
                                entity.setAmount(obj.getString("amount"));
                                entity.setBalancemoney(obj.getString("balancemoney"));
                                entity.setTranddate(obj.getString("tranddate"));
                                entity.setTransstate(obj.getString("transstate"));
                                entity.setTranstype(obj.getString("transtype"));
                                entity.setName(obj.getString("name"));
                                entity.setType(obj.getString("type"));
                                rechargeList.add(entity);
                            }
                            setView(rechargeList, RequestCode);
                            if (last == pageNo) {
                                ll_recharge_record.onRefreshComplete();
                                ll_recharge_record.setMode(Mode.PULL_FROM_START);
                            } else {
                                ll_recharge_record.setMode(Mode.BOTH);
                            }
                        } else {
                            ll_recharge_record.setVisibility(View.GONE);
                            rl_no_recharge_message.setVisibility(View.VISIBLE);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(RechargeRecordsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RechargeRecordsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RechargeRecordsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(RechargeRecordsActivity.this);
                        //						LoginUserProvider.cleanDetailData(RechargeRecordsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RechargeRecordsActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(RechargeRecordsActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    ll_recharge_record.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ll_recharge_record.onRefreshComplete();
                    Toast.makeText(RechargeRecordsActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                ll_recharge_record.onRefreshComplete();
                Toast.makeText(RechargeRecordsActivity.this, "请检查网络", 0).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginUserProvider.currentStatus) {
            TokenStr = LoginUserProvider.getUser(this).getToken();
        }
        pageNo = 1;
        initData(TokenStr, pageNo, pageSize, from, type, REFRESH);
        ll_recharge_record.setMode(Mode.BOTH);
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    protected void setView(List<AnsactionRecordsEntity> rechargeList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            rechargeTotalList.clear();
            if (rechargeList != null && rechargeList.size() > 0) {
                rechargeTotalList.addAll(rechargeList);
                pageNo = 1;
            }
        } else if (requestCode == LOADMORE) {
            rechargeTotalList.addAll(rechargeList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        initData(TokenStr, pageNo, pageSize, from, type, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        initData(TokenStr, pageNo + 1, pageSize, from, type, LOADMORE);
    }

}
