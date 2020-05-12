package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
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
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 安心投
 *
 * @author pc
 */
public class SafeInvestmentActivity extends BaseActivity implements OnRefreshListener2<ListView> {

    private PullToRefreshListView ll_safe_investment;
    private SafeInvestmentAdapter adapter;
    private List<InvestmentEntity> safeInvestmentList;//安心投
    private List<InvestmentEntity> safeInvestmentTotalList;//总集合
    private InvestmentEntity entity;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 13;//当前页面的内容数目
    private RelativeLayout rl_no_investment_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_safe_investment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(SafeInvestmentActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        setTitle("安心投");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        safeInvestmentTotalList = new ArrayList<InvestmentEntity>();
        ll_safe_investment = (PullToRefreshListView) findViewById(R.id.ll_safe_investment);

        ll_safe_investment.setMode(Mode.BOTH);
        ll_safe_investment.setOnRefreshListener(this);
        adapter = new SafeInvestmentAdapter(SafeInvestmentActivity.this, safeInvestmentTotalList);
        ll_safe_investment.setAdapter(adapter);
        rl_no_investment_message = (RelativeLayout) findViewById(R.id.rl_no_investment_message);
        findViewById(R.id.tv_tips).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(SafeInvestmentActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
            }
        });
    }

    @Override
    protected void initData() {

    }

    //获取新手标数据
    private void getData(String token, String from, final String projectProductType, final int pageNo, final int pageSize, final int RequestCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(SafeInvestmentActivity.this));
        String url = Urls.GETNEWMYBIDSDETAILH5;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("projectProductType", projectProductType);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        int last = dataObj.getInt("pageCount");//总页数
                        safeInvestmentList = new ArrayList<InvestmentEntity>();
                        JSONArray bidlistArray = dataObj.getJSONArray("userBidHistoryList");
                        if (bidlistArray.length() > 0) {
                            ll_safe_investment.setVisibility(View.VISIBLE);
                            rl_no_investment_message.setVisibility(View.GONE);
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
                                safeInvestmentList.add(entity);
                            }
                            setView(safeInvestmentList, RequestCode);
                            if (last == pageNo) {
                                ll_safe_investment.onRefreshComplete();
                                ll_safe_investment.setMode(Mode.PULL_FROM_START);
                            } else {
                                ll_safe_investment.setMode(Mode.BOTH);
                            }
                        } else {
                            ll_safe_investment.setVisibility(View.GONE);
                            rl_no_investment_message.setVisibility(View.VISIBLE);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(SafeInvestmentActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(SafeInvestmentActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(SafeInvestmentActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //								LoginUserProvider.cleanData(InvestmentListActivity.this);
                        //								LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(SafeInvestmentActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(SafeInvestmentActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    ll_safe_investment.onRefreshComplete();
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    ll_safe_investment.onRefreshComplete();
                    Toast.makeText(SafeInvestmentActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                ll_safe_investment.onRefreshComplete();
                Toast.makeText(SafeInvestmentActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    //	//获取数据
    //		private void getData(String token,String from,String projecttype,final int pageNo,int pageSize,final int RequestCode) {
    //			AsyncHttpClient client = new AsyncHttpClient();
    //			String url = Urls.GETMYBIDSDETAILH5;
    //			RequestParams params = new RequestParams();
    //			params.put("token", token);
    //			params.put("from", from);
    //			params.put("projecttype", projecttype);
    //			params.put("pageNo", pageNo);
    //			params.put("pageSize", pageSize);
    //			client.post(url, params, new AsyncHttpResponseHandler() {
    //
    //				@Override
    //				public void onSuccess(int statusCode, Header[] headers,
    //						byte[] responseBody) {
    //					String result = new String(responseBody);
    //					CustomProgress.CustomDismis();
    //					try {
    //						JSONObject jsonObject = new JSONObject(result);
    //						if (jsonObject.getString("state").equals("0")) {
    //							JSONObject dataObj = jsonObject.getJSONObject("data");
    //							safeInvestmentList = new ArrayList<InvestmentEntity>();
    //							int last = dataObj.getInt("pageCount");//总页数
    //							if(RequestCode==REFRESH){//刷新
    //								safeInvestmentList.addAll(newInvestmentList);
    //							}
    //							JSONArray bidlistArray = dataObj.getJSONArray("userBidHistoryList");
    //							if(bidlistArray.length()>0){
    //								for(int i=0;i<bidlistArray.length();i++){
    //									JSONObject obj = bidlistArray.getJSONObject(i);
    //									InvestmentEntity entity = new InvestmentEntity();
    //									entity.setName(obj.getString("projectName"));
    //									entity.setRate(obj.getString("rate"));
    //									entity.setSpan(obj.getString("span"));
    //									entity.setState(obj.getString("state"));
    //									entity.setProjecttype(obj.getString("projectType"));
    //									safeInvestmentList.add(entity);
    //								}
    //							}
    //							if(safeInvestmentList.size()>0){
    //								ll_safe_investment.setVisibility(View.VISIBLE);
    //								rl_no_investment_message.setVisibility(View.GONE);
    //								setView(safeInvestmentList,RequestCode);
    //								if(last == pageNo){
    //									ll_safe_investment.onRefreshComplete();
    //									ll_safe_investment.setMode(Mode.PULL_FROM_START);
    //								}else{
    //									ll_safe_investment.setMode(Mode.BOTH);
    //								}
    //							}else{
    //								ll_safe_investment.setVisibility(View.GONE);
    //								rl_no_investment_message.setVisibility(View.VISIBLE);
    //							}
    //						}else if(jsonObject.getString("state").equals("4")){//系统超时
    //							String mGesture = LoginUserProvider.getUser(SafeInvestmentActivity.this).getGesturePwd();
    //							if(mGesture.equals("1")&&!mGesture.equals("")&&mGesture!=null){// 判断是否设置手势密码
    //								//设置手势密码
    //								Intent intent = new Intent(SafeInvestmentActivity.this,UnlockGesturePasswordActivity.class);
    //								intent.putExtra("overtime", "0");
    //								startActivity(intent);
    //							}else{
    //								//未设置手势密码
    //								Intent intent = new Intent(SafeInvestmentActivity.this,LoginActivity.class);
    //								intent.putExtra("overtime", "0");
    //								startActivity(intent);
    //							}
    ////							LoginUserProvider.cleanData(InvestmentListActivity.this);
    ////							LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
    //							DoCacheUtil util=DoCacheUtil.get(SafeInvestmentActivity.this);
    //							util.put("isLogin", "");
    //
    //						}else{
    //							Toast.makeText(SafeInvestmentActivity.this, jsonObject.getString("message"), 0).show();
    //						}
    //						ll_safe_investment.onRefreshComplete();
    //					} catch (JSONException e) {
    //						e.printStackTrace();
    //						ll_safe_investment.onRefreshComplete();
    //						Toast.makeText(SafeInvestmentActivity.this, "解析异常", 0).show();
    //					}
    //				}
    //
    //				@Override
    //				public void onFailure(int statusCode,org.apache.http.Header[] headers, byte[] responseBody,
    //						Throwable error) {
    //					ll_safe_investment.onRefreshComplete();
    //					Toast.makeText(SafeInvestmentActivity.this, "请检查网络", 0).show();
    //					CustomProgress.CustomDismis();
    //				}
    //			});
    //		}

    @Override
    protected void onResume() {
        super.onResume();
        pageNo = 1;
        //1 - 安心投 2-供应链
        //			newGetData(LoginUserProvider.getUser(SafeInvestmentActivity.this).getToken(),"3","1",pageNo,pageSize,REFRESH);
        getData(LoginUserProvider.getUser(SafeInvestmentActivity.this).getToken(), "3", "1", pageNo, pageSize, REFRESH);
        ll_safe_investment.setMode(Mode.BOTH);
    }

    protected void setView(List<InvestmentEntity> safeList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            safeInvestmentTotalList.clear();
            if (safeList != null && safeList.size() > 0) {
                safeInvestmentTotalList.addAll(safeList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            safeInvestmentTotalList.addAll(safeList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getData(LoginUserProvider.getUser(SafeInvestmentActivity.this).getToken(), "3", "1", pageNo, pageSize, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(LoginUserProvider.getUser(SafeInvestmentActivity.this).getToken(), "3", "1", pageNo + 1, pageSize, LOADMORE);
    }
}
