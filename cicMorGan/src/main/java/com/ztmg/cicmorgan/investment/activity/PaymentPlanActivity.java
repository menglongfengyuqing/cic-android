package com.ztmg.cicmorgan.investment.activity;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.adapter.PaymentPlanAdapter;
import com.ztmg.cicmorgan.investment.entity.PaymentPlanEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

/**
 * 还款计划
 *
 * @author pc
 */
public class PaymentPlanActivity extends BaseActivity implements OnRefreshListener2<ListView> {
    private PullToRefreshListView lv_paymentplan_list;
    private PaymentPlanAdapter adapter;
    private PaymentPlanEntity entity;
    private List<PaymentPlanEntity> paymentplanList;
    private List<PaymentPlanEntity> paymentplanTotalList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 13;//当前页面的内容数目
    private static int type = 3;//Android
    private String projectid;
    private TextView tv_tips;
    private LinearLayout ll_content;
    private SlowlyProgressBar slowlyProgressBar;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_paymentplan_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(PaymentPlanActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        Intent intent = getIntent();
        projectid = intent.getStringExtra("projectid");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        lv_paymentplan_list = (PullToRefreshListView) findViewById(R.id.lv_paymentplan_list);
        paymentplanTotalList = new ArrayList<PaymentPlanEntity>();
        lv_paymentplan_list.setMode(Mode.BOTH);
        lv_paymentplan_list.setOnRefreshListener(this);
        adapter = new PaymentPlanAdapter(PaymentPlanActivity.this, paymentplanTotalList);
        lv_paymentplan_list.setAdapter(adapter);
        setTitle("还款计划");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        tv_tips.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(PaymentPlanActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
            }
        });
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
    }

    //获取还款计划列表
    private void getPaymentplanList(int from, final int pageNo, int pageSize, String projectid, final int RequestCode) {
        //		CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(PaymentPlanActivity.this));
        String url = Urls.GETPROJECTREPAYPLANLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("projectid", projectid);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                ll_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                //				CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String totalCount = dataObj.getString("totalCount");
                        int last = dataObj.getInt("last");//总页数
                        paymentplanList = new ArrayList<PaymentPlanEntity>();
                        JSONArray repayplanlistArray = dataObj.getJSONArray("repayplanlist");
                        if (repayplanlistArray.length() > 0) {
                            for (int i = 0; i < repayplanlistArray.length(); i++) {
                                entity = new PaymentPlanEntity();
                                JSONObject obj = repayplanlistArray.getJSONObject(i);
                                entity.setAmount(obj.getString("amount"));
                                entity.setRepaysort(obj.getString("repaysort"));
                                entity.setRepaydate(obj.getString("repaydate"));
                                paymentplanList.add(entity);
                            }
                            setView(paymentplanList, RequestCode);
                            if (last == pageNo) {
                                lv_paymentplan_list.onRefreshComplete();
                                lv_paymentplan_list.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_paymentplan_list.setMode(Mode.BOTH);
                            }
                        } else {
                            tv_tips.setVisibility(View.VISIBLE);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(PaymentPlanActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(PaymentPlanActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(PaymentPlanActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }

                        //						LoginUserProvider.cleanData(PaymentPlanActivity.this);
                        //						LoginUserProvider.cleanDetailData(PaymentPlanActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(PaymentPlanActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(PaymentPlanActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    lv_paymentplan_list.onRefreshComplete();
                } catch (JSONException e) {
                    lv_paymentplan_list.onRefreshComplete();
                    e.printStackTrace();
                    Toast.makeText(PaymentPlanActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                lv_paymentplan_list.onRefreshComplete();
                Toast.makeText(PaymentPlanActivity.this, "请检查网络", 0).show();
                //				CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pageNo = 1;
        getPaymentplanList(type, pageNo, pageSize, projectid, REFRESH);
        lv_paymentplan_list.setMode(Mode.BOTH);
    }

    protected void setView(List<PaymentPlanEntity> paymentplanList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            paymentplanTotalList.clear();
            if (paymentplanList != null && paymentplanList.size() > 0) {
                paymentplanTotalList.addAll(paymentplanList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            paymentplanTotalList.addAll(paymentplanList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getPaymentplanList(type, pageNo, pageSize, projectid, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getPaymentplanList(type, pageNo + 1, pageSize, projectid, LOADMORE);
    }
}

