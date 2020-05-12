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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.SafeInvestmentAdapter;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.entity.InvestmentEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

/**
 * 账户供应链
 *
 * @author pc
 */
public class SupplyChainActivity extends BaseActivity implements OnRefreshListener2<ListView> {

    private PullToRefreshListView ll_supply_chain;
    private SafeInvestmentAdapter adapter;
    private List<InvestmentEntity> supplyChainList;
    private List<InvestmentEntity> supplyChainListTotalList;
    private InvestmentEntity entity;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 13;//当前页面的内容数目
    private RelativeLayout rl_no_chain_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_supply_chain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(SupplyChainActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        setTitle("供应链");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        supplyChainListTotalList = new ArrayList<InvestmentEntity>();
        ll_supply_chain = (PullToRefreshListView) findViewById(R.id.ll_supply_chain);

        ll_supply_chain.setMode(Mode.BOTH);
        ll_supply_chain.setOnRefreshListener(this);
        adapter = new SafeInvestmentAdapter(SupplyChainActivity.this, supplyChainListTotalList);
        ll_supply_chain.setAdapter(adapter);
        rl_no_chain_message = (RelativeLayout) findViewById(R.id.rl_no_chain_message);
        findViewById(R.id.tv_tips).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(SupplyChainActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
            }
        });
    }

    @Override
    protected void initData() {

    }

    //获取数据
    private void getData(String token, String from, final String projectProductType, final int pageNo, final int pageSize, final int RequestCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETNEWMYBIDSDETAILH5;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("projectProductType", projectProductType);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        int last = dataObj.getInt("pageCount");//总页数
                        supplyChainList = new ArrayList<InvestmentEntity>();
                        JSONArray bidlistArray = dataObj.getJSONArray("userBidHistoryList");
                        if (bidlistArray.length() > 0) {
                            ll_supply_chain.setVisibility(View.VISIBLE);
                            rl_no_chain_message.setVisibility(View.GONE);
                            for (int i = 0; i < bidlistArray.length(); i++) {
                                JSONObject obj = bidlistArray.getJSONObject(i);
                                InvestmentEntity entity = new InvestmentEntity();
                                entity.setName(obj.getString("projectName"));
                                entity.setRate(obj.getString("rate"));
                                entity.setSpan(obj.getString("span"));
                                entity.setState(obj.getString("state"));
                                entity.setProjecttype(obj.getString("projectType"));
                                entity.setProjectProductType(projectProductType);
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                supplyChainList.add(entity);
                            }
                            setView(supplyChainList, RequestCode);
                            if (last == pageNo) {
                                ll_supply_chain.onRefreshComplete();
                                ll_supply_chain.setMode(Mode.PULL_FROM_START);
                            } else {
                                ll_supply_chain.setMode(Mode.BOTH);
                            }
                        } else {
                            ll_supply_chain.setVisibility(View.GONE);
                            rl_no_chain_message.setVisibility(View.VISIBLE);
                        }
                        //getData2(LoginUserProvider.getUser(SupplyChainActivity.this).getToken(),"3","3",pageNo,pageSize,REFRESH,supplyChainList);

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(SupplyChainActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(SupplyChainActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(SupplyChainActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //								LoginUserProvider.cleanData(InvestmentListActivity.this);
                        //								LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(SupplyChainActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(SupplyChainActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    ll_supply_chain.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ll_supply_chain.onRefreshComplete();
                    Toast.makeText(SupplyChainActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                ll_supply_chain.onRefreshComplete();
                Toast.makeText(SupplyChainActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNo = 1;
        getData(LoginUserProvider.getUser(SupplyChainActivity.this).getToken(), "3", "2", pageNo, pageSize, REFRESH);
        ll_supply_chain.setMode(Mode.BOTH);
    }

    protected void setView(List<InvestmentEntity> safeList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            supplyChainListTotalList.clear();
            if (safeList != null && safeList.size() > 0) {
                supplyChainListTotalList.addAll(safeList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            supplyChainListTotalList.addAll(safeList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getData(LoginUserProvider.getUser(SupplyChainActivity.this).getToken(), "3", "2", pageNo, pageSize, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(LoginUserProvider.getUser(SupplyChainActivity.this).getToken(), "3", "2", pageNo + 1, pageSize, LOADMORE);
    }
}
