package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.ztmg.cicmorgan.account.adapter.MessageAdapter;
import com.ztmg.cicmorgan.account.entity.MessageNoticeEntity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 站内消息
 *
 * @author pc
 */
public class MessageActivity extends BaseActivity implements OnRefreshListener2<ListView>, OnItemClickListener {

    private PullToRefreshListView lv_message;
    private MessageAdapter adapter;
    private List<MessageNoticeEntity> messsageList;
    private List<MessageNoticeEntity> messsageTotalList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private SharedPreferences sp;
    private DoCacheUtil doUtil;
    private String token;
    private TextView tv_tips;
    private RelativeLayout rl_no_message;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(MessageActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_msg);
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MessageActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        token = LoginUserProvider.getUser(MessageActivity.this).getToken();
        pageNo = 1;
        getStationList("3", token, pageNo, pageSize, "0", REFRESH);
        lv_message.setMode(Mode.BOTH);

        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        setRightText("", new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(MessageActivity.this, "301002_znxx_yjyd_click");
                String token = LoginUserProvider.getUser(MessageActivity.this).getToken();
                changeLetterState("3", token);
            }
        });
        setTitle("站内消息");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(MessageActivity.this, "301001_znxx_back_click");
                finish();
            }
        });
        rl_no_message = (RelativeLayout) findViewById(R.id.rl_no_message);
        messsageTotalList = new ArrayList<>();
        lv_message = (PullToRefreshListView) findViewById(R.id.lv_message);
        lv_message.setMode(Mode.BOTH);
        lv_message.setOnRefreshListener(this);
        lv_message.setOnItemClickListener(this);
        adapter = new MessageAdapter(MessageActivity.this, messsageTotalList);
        lv_message.setAdapter(adapter);
        doUtil = DoCacheUtil.get(MessageActivity.this);
        String flag = doUtil.getAsString("isReadMsg");
        if (flag != null && flag.equals("isReadMsg")) {
            changeColor(true);
        }
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    public void changeColor(boolean flag) {
        if (messsageTotalList != null && messsageTotalList.size() > 0) {
            if (flag) {
                doUtil.put("isReadMsg", "isReadMsg");
                for (MessageNoticeEntity entity : messsageTotalList) {
                    entity.setState("2");
                }
            } else {
                for (MessageNoticeEntity entity : messsageTotalList) {
                    entity.setState("1");
                }
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void getStationList(String from, String token, final int pageNo, int pageSize, String state, final int RequestCode) {
        //CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MessageActivity.this));
        String url = Urls.STATIONLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("state", state);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                String result = new String(responseBody);
                //CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        messsageList = new ArrayList<MessageNoticeEntity>();
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        int totalCount = dataObj.getInt("lastPage");
                        JSONArray array = dataObj.getJSONArray("letters");
                        if (array.length() > 0) {
                            lv_message.setVisibility(View.VISIBLE);
                            rl_no_message.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                MessageNoticeEntity entity = new MessageNoticeEntity();
                                JSONObject obj = array.getJSONObject(i);
                                entity.setId(obj.getString("id"));
                                entity.setLetterType(obj.getString("letterType"));
                                entity.setSendTime(obj.getString("sendTime"));
                                entity.setTitle(obj.getString("title"));
                                entity.setBody(obj.getString("body"));
                                entity.setState(obj.getString("state"));
                                messsageList.add(entity);
                            }
                            setView(messsageList, RequestCode);
                            if (totalCount == pageNo) {
                                lv_message.onRefreshComplete();
                                lv_message.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_message.setMode(Mode.BOTH);
                            }

                            setRightText("一键已读", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    String token = LoginUserProvider.getUser(MessageActivity.this).getToken();
                                    changeLetterState("3", token);
                                }
                            });
                        } else {
                            setRightText(" ", null);
                            lv_message.setVisibility(View.GONE);
                            rl_no_message.setVisibility(View.VISIBLE);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MessageActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MessageActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MessageActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(ValueVoucherActivity.this);
                        //						LoginUserProvider.cleanDetailData(ValueVoucherActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(MessageActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MessageActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    lv_message.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_message.onRefreshComplete();
                    Toast.makeText(MessageActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                lv_message.onRefreshComplete();
                CustomProgress.CustomDismis();
                Toast.makeText(MessageActivity.this, "请检查网络", 0).show();
            }
        });
    }

    protected void setView(List<MessageNoticeEntity> messsageList, int requestCode) {
        if (requestCode == REFRESH) {
            messsageTotalList.clear();
            if (messsageList != null && messsageList.size() > 0) {
                messsageTotalList.addAll(messsageList);
                pageNo = 1;
            }
        } else if (requestCode == LOADMORE) {
            messsageTotalList.addAll(messsageList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getStationList("3", token, pageNo, pageSize, "0", REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getStationList("3", token, pageNo + 1, pageSize, "0", LOADMORE);
    }

    //一键已读
    private void changeLetterState(String from, String token) {
        //		CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MessageActivity.this));
        String url = Urls.CHANGELETTERSTATE;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                //				CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        MessageActivity.this.changeColor(true);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MessageActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MessageActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MessageActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(ValueVoucherActivity.this);
                        //						LoginUserProvider.cleanDetailData(ValueVoucherActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(MessageActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MessageActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MessageActivity.this, "解析异常", 0).show();
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                Toast.makeText(MessageActivity.this, "请检查网络", 0).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        onEvent(MessageActivity.this, "301003_znxx_list_click");
        Intent intent = new Intent(MessageActivity.this, MessageDetail.class);
        intent.putExtra("id", messsageTotalList.get(position - 1).getId());
        startActivity(intent);
    }
}
