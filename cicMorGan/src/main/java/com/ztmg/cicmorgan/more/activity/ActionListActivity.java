package com.ztmg.cicmorgan.more.activity;

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
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.SafeInvestmentActivity;
import com.ztmg.cicmorgan.account.adapter.SafeInvestmentAdapter;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.entity.InvestmentEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.adapter.ActionCenterAdapter;
import com.ztmg.cicmorgan.more.adapter.ActionListAdapter;
import com.ztmg.cicmorgan.more.entity.ActionCenterEntity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

/**
 * 发现历史活动列表
 *
 * @author pc
 */
public class ActionListActivity extends BaseActivity implements OnRefreshListener2<ListView> {
    private PullToRefreshListView lv_action_list;
    private ActionListAdapter adapter;
    private List<ActionCenterEntity> actionList;
    private List<ActionCenterEntity> actionTotalList;//总集合
    private ActionCenterEntity entity;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 13;//当前页面的内容数目
    private RelativeLayout rl_no_action_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_action_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(ActionListActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        setTitle("历史活动");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionTotalList = new ArrayList<ActionCenterEntity>();
        lv_action_list = (PullToRefreshListView) findViewById(R.id.lv_action_list);

        lv_action_list.setMode(Mode.BOTH);
        lv_action_list.setOnRefreshListener(this);
        adapter = new ActionListAdapter(ActionListActivity.this, actionTotalList);
        lv_action_list.setAdapter(adapter);
        rl_no_action_list = (RelativeLayout) findViewById(R.id.rl_no_action_list);
        findViewById(R.id.tv_tips).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(ActionListActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
            }
        });
    }

    @Override
    protected void initData() {

    }

    //获取活动列表数据
    private void getActionData(int from, String type, final int pageNo, int pageSize, final int RequestCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //			AsyncHttpClient client = new AsyncHttpClient();
        //			client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ActionListActivity.this));
        String url = Urls.GETCMSLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("type", type);
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
                        String totalCount = dataObj.getString("totalCount");
                        int last = dataObj.getInt("pageCount");//总页数
                        actionList = new ArrayList<ActionCenterEntity>();
                        JSONArray cmsListArray = dataObj.getJSONArray("cmsList");
                        if (cmsListArray.length() > 0) {
                            lv_action_list.setVisibility(View.VISIBLE);
                            rl_no_action_list.setVisibility(View.GONE);
                            for (int i = 0; i < cmsListArray.length(); i++) {
                                JSONObject obj = cmsListArray.getJSONObject(i);
                                entity = new ActionCenterEntity();
                                entity.setImgUrl(obj.getString("imgPath"));//图片地址
                                entity.setType(obj.getString("state"));//0下线 1-上线
                                actionList.add(entity);
                            }
                            setView(actionList, RequestCode);
                            if (last == pageNo) {
                                lv_action_list.onRefreshComplete();
                                lv_action_list.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_action_list.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_action_list.setVisibility(View.GONE);
                            rl_no_action_list.setVisibility(View.VISIBLE);
                        }
                        //							actionAdapter = new ActionCenterAdapter(getActivity(), actionList);
                        //							lv_action.setAdapter(actionAdapter);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(ActionListActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(ActionListActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(ActionListActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(ActionListActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(ActionListActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ActionListActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(ActionListActivity.this, "请检查网络", 0).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNo = 1;
        //1 - 安心投 2-供应链
        //			newGetData(LoginUserProvider.getUser(SafeInvestmentActivity.this).getToken(),"3","1",pageNo,pageSize,REFRESH);
        getActionData(3, "1", pageNo, pageSize, REFRESH);
        lv_action_list.setMode(Mode.BOTH);
    }

    protected void setView(List<ActionCenterEntity> safeList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            actionTotalList.clear();
            if (safeList != null && safeList.size() > 0) {
                actionTotalList.addAll(safeList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            actionTotalList.addAll(safeList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    //int from,String type,final int pageNo,int pageSize,final int RequestCode
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getActionData(3, "1", pageNo, pageSize, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getActionData(3, "1", pageNo, pageSize, LOADMORE);
    }
}
