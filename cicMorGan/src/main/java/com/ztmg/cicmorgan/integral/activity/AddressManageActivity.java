package com.ztmg.cicmorgan.integral.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.integral.adapter.AddressManagerAdapter;
import com.ztmg.cicmorgan.integral.adapter.AddressManagerAdapter.OnUpdateAddring;
import com.ztmg.cicmorgan.integral.entity.AddressManagerEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 地址管理
 *
 * @author pc
 */

public class AddressManageActivity extends BaseActivity implements OnRefreshListener2<ListView>, OnUpdateAddring {
    private PullToRefreshListView lv_address_manager_list;
    private AddressManagerAdapter adapter;
    private AddressManagerEntity entity;
    private List<AddressManagerEntity> addressList;
    private List<AddressManagerEntity> addressTotalList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private final int REFRESHED = 100;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 13;//当前页面的内容数目
    private static final int ADDRESS = 101;

    private RelativeLayout rl_no_recharge_message;
    private SlowlyProgressBar slowlyProgressBar;
    private String isDefault;//是否有默认
    int mindex;
    int newProgress = 0;
    @SuppressLint("HandlerLeak")
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
            SystemBarTintManager tintManager = new SystemBarTintManager(AddressManageActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_address_manager);
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(AddressManageActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


    }

    @Override
    protected void initView() {
        Title();
        addressTotalList = new ArrayList<>();
        rl_no_recharge_message = (RelativeLayout) findViewById(R.id.rl_no_recharge_message);
        lv_address_manager_list = (PullToRefreshListView) findViewById(R.id.lv_address_manager_list);
        lv_address_manager_list.setMode(Mode.BOTH);
        lv_address_manager_list.setOnRefreshListener(this);
        adapter = new AddressManagerAdapter(AddressManageActivity.this, addressTotalList);
        adapter.setmOnUpdateAddring(this);
        lv_address_manager_list.setAdapter(adapter);
        //点击条目返回
        lv_address_manager_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent();
                intent.putExtra("address", (Serializable) addressList.get(position - 1));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();

    }

    private void Title() {
        setTitle("收货地址");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(AddressManageActivity.this, "701001_glshdz_back_click");
                setResult(REFRESHED);
                finish();
            }
        });
        //新建地址
        //iv_img_right.setBackgroundResource(R.drawable.account);
        //iv_img_right.setVisibility(View.VISIBLE);
        setRightAdd(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(AddressManageActivity.this, "701002_glshdz_addnew_click");
                if (addressList.size() < 8) {
                    if (!Lists.isEmpty(addressList)) {
                        for (AddressManagerEntity addressManagerEntity : addressList) {
                            String s = addressManagerEntity.getIsDefault();
                            if (s.equals("是")) {
                                isDefault = "是";
                                break;
                            } else if (s.equals("否")) {
                                isDefault = "否";
                            }
                        }
                    } else {
                        isDefault = "否";
                    }
                    Intent intent = new Intent(AddressManageActivity.this, BuildReceiptAddressActivity.class);
                    intent.putExtra("newifmodify", "new");
                    intent.putExtra("isDefault", isDefault);
                    startActivityForResult(intent, ADDRESS);
                } else {
                    Toast.makeText(AddressManageActivity.this, "最多可以新建8个地址", 0).show();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
        pageNo = 1;
        getData("3", LoginUserProvider.getUser(AddressManageActivity.this).getToken(), pageNo, pageSize, REFRESH);
        lv_address_manager_list.setMode(Mode.BOTH);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //获取数据
    private void getData(String from, String token, final int pageNo, int pageSize, final int RequestCode) {
        //CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(AddressManageActivity.this));
        String url = Urls.ADDRESSLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                //CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        int last = jsonObject.getInt("lastPage");//总页数
                        addressList = new ArrayList<>();
                        if (null == jsonObject.getJSONArray("data")) {
                            addressList.clear();
                            adapter.notifyDataSetChanged();
                        }
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            lv_address_manager_list.setVisibility(View.VISIBLE);
                            rl_no_recharge_message.setVisibility(View.GONE);
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                AddressManagerEntity entity = new AddressManagerEntity();
                                entity.setId(obj.getString("id"));
                                entity.setAddress(obj.getString("address"));
                                entity.setIsDefault(obj.getString("isDefault"));
                                entity.setName(obj.getString("name"));
                                entity.setProvince(obj.getString("province"));
                                entity.setMobile(obj.getString("mobile"));
                                entity.setCity(obj.getString("city"));
                                if ("是".equals(obj.getString("isDefault")))
                                    addressList.add(0, entity);
                                else
                                    addressList.add(entity);
                            }
                            setView(addressList, RequestCode);
                            if (last == pageNo) {
                                lv_address_manager_list.onRefreshComplete();
                                lv_address_manager_list.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_address_manager_list.setMode(Mode.BOTH);
                            }

                        } else {
                            adapter = null;
                            addressList.clear();
                            lv_address_manager_list.removeAllViews();
                            rl_no_recharge_message.setVisibility(View.VISIBLE);
                            lv_address_manager_list.setVisibility(View.GONE);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(AddressManageActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(AddressManageActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(AddressManageActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(AddressManageActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(AddressManageActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    lv_address_manager_list.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_address_manager_list.onRefreshComplete();
                    Toast.makeText(AddressManageActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                lv_address_manager_list.onRefreshComplete();
                Toast.makeText(AddressManageActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }


    protected void setView(List<AddressManagerEntity> addressList, int requestCode) {
        if (requestCode == REFRESH) {
            addressTotalList.clear();
            if (addressList != null && addressList.size() > 0) {
                addressTotalList.addAll(addressList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            addressTotalList.addAll(addressList);
            pageNo = 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getData("3", LoginUserProvider.getUser(AddressManageActivity.this).getToken(), pageNo, pageSize, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData("3", LoginUserProvider.getUser(AddressManageActivity.this).getToken(), pageNo, pageSize, LOADMORE);
    }

    //	@Override
    //	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //		super.onActivityResult(requestCode, resultCode, data);
    //		if(resultCode==RESULT_OK){
    //			switch (requestCode) {
    //			case ADDRESS:
    //				//重新加载
    //				getData("3",LoginUserProvider.getUser(AddressManageActivity.this).getToken(),pageNo,pageSize,REFRESH);
    //				break;
    //
    //			default:
    //				break;
    //			}
    //		}
    //	}
    @Override
    public void onRefreshDefault(int positon) {
        for (AddressManagerEntity entity : addressTotalList) {
            entity.setIsDefault("否");
        }
        addressTotalList.get(positon).setIsDefault("是");
        adapter.notifyDataSetChanged();

    }

    /*
     *  删除item(non-Javadoc)
     * @see com.ztmg.cicmorgan.integral.adapter.AddressManagerAdapter.OnUpdateAddring#onDeleteItem(int)
     */
    @Override
    public void onDeleteItem(int positon) {
        addressTotalList.remove(positon);
        adapter.notifyDataSetChanged();

    }
}
