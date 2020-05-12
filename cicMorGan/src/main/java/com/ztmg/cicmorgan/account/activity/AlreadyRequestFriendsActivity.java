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
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.RequestFriendsAdapter;
import com.ztmg.cicmorgan.account.entity.RequestFriendsEntity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
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

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 已邀请好友
 *
 * @author pc
 */
public class AlreadyRequestFriendsActivity extends BaseActivity implements OnRefreshListener2<ListView> {
    private PullToRefreshListView lv_already_request_friends_list;
    private RequestFriendsAdapter adapter;
    private List<RequestFriendsEntity> alreadyRequestFriendsList;
    private List<RequestFriendsEntity> alreadyRequestFriendsTotalList;
    private RequestFriendsEntity entity;
    private RelativeLayout rl_no_request_friends_message;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(AlreadyRequestFriendsActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_already_request_friends);
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(AlreadyRequestFriendsActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void initView() {
        setTitle("邀请好友详情");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(AlreadyRequestFriendsActivity.this, "312001_yqhyxq_back_click");
                finish();
            }
        });
        rl_no_request_friends_message = (RelativeLayout) findViewById(R.id.rl_no_request_friends_message);
        lv_already_request_friends_list = (PullToRefreshListView) findViewById(R.id.lv_already_request_friends_list);
        alreadyRequestFriendsTotalList = new ArrayList<RequestFriendsEntity>();
        lv_already_request_friends_list.setMode(Mode.BOTH);
        lv_already_request_friends_list.setOnRefreshListener(this);
        adapter = new RequestFriendsAdapter(AlreadyRequestFriendsActivity.this, alreadyRequestFriendsTotalList, "1");
        lv_already_request_friends_list.setAdapter(adapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        pageNo = 1;
        token = LoginUserProvider.getUser(AlreadyRequestFriendsActivity.this).getToken();
        getData("3", pageNo, pageSize, token, REFRESH);
        lv_already_request_friends_list.setMode(Mode.BOTH);
    }

    //获取数据
    private void getData(String from, final int pageNo, int pageSize, String token, final int RequestCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(AlreadyRequestFriendsActivity.this));
        String url = Urls.GETINVITEFRIENDSLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        int totalCount = dataObj.getInt("totalCount");//总条数
                        alreadyRequestFriendsList = new ArrayList<RequestFriendsEntity>();
                        JSONArray listArray = dataObj.getJSONArray("list");
                        if (listArray.length() > 0) {
                            lv_already_request_friends_list.setVisibility(View.VISIBLE);
                            rl_no_request_friends_message.setVisibility(View.GONE);
                            for (int i = 0; i < listArray.length(); i++) {
                                JSONObject obj = listArray.getJSONObject(i);
                                RequestFriendsEntity entity = new RequestFriendsEntity();
                                entity.setMobilePhone(obj.getString("mobilePhone"));
                                entity.setRealName(obj.getString("realName"));
                                entity.setRegisterDate(obj.getString("registerDate"));
                                alreadyRequestFriendsList.add(entity);
                            }
                            setView(alreadyRequestFriendsList, RequestCode);
                            int num = 0;
                            //1%10=1     10%10=0    11%10=1
                            if (totalCount < 10) {
                                num = 1;
                            } else {
                                //10/10=1   100/10=10  11/10=1
                                if (totalCount % 10 == 0) {
                                    num = totalCount / 10;
                                } else {
                                    num = totalCount / 10 + 1;
                                }
                            }
                            if (num == pageNo) {
                                lv_already_request_friends_list.onRefreshComplete();
                                lv_already_request_friends_list.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_already_request_friends_list.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_already_request_friends_list.setVisibility(View.GONE);
                            rl_no_request_friends_message.setVisibility(View.VISIBLE);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(AlreadyRequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(AlreadyRequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(AlreadyRequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(InvestmentListActivity.this);
                        //LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(AlreadyRequestFriendsActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(AlreadyRequestFriendsActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    lv_already_request_friends_list.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_already_request_friends_list.onRefreshComplete();
                    Toast.makeText(AlreadyRequestFriendsActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                rl_no_request_friends_message.setVisibility(View.VISIBLE);
                lv_already_request_friends_list.onRefreshComplete();
                Toast.makeText(AlreadyRequestFriendsActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    protected void setView(List<RequestFriendsEntity> list, int requestCode) {
        if (requestCode == REFRESH) {
            alreadyRequestFriendsTotalList.clear();
            if (list != null && list.size() > 0) {
                alreadyRequestFriendsTotalList.addAll(list);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            alreadyRequestFriendsTotalList.addAll(list);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getData("3", pageNo, pageSize, token, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData("3", pageNo + 1, pageSize, token, LOADMORE);
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
}
