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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.integral.adapter.GoodsListAdapter;
import com.ztmg.cicmorgan.integral.entity.GoodsListEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.IntegralDetailActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.ClickControlledSpinner;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 奖品列表
 *
 * @author pc
 */
public class GoodsListActivity2 extends BaseActivity implements OnRefreshListener2<ListView>, AdapterView.OnItemClickListener, OnClickListener {
    private PullToRefreshListView lv_goods_list;
    private GoodsListAdapter adapter;
    private GoodsListEntity entity;
    private List<GoodsListEntity> goodsList;
    private List<GoodsListEntity> goodsTotalList = new ArrayList<>();
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 13;//当前页面的内容数目
    private RelativeLayout rl_goods_list, rl_no_goods;

    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;
    private ArrayAdapter mArrayAdapter;
    private static final String[] name = {"全部", "积分兑换", "积分抽奖"};
    private static final String[] name_true = {"全部", "实物奖品", "抵用券"};

    @BindView(R.id.up_down_1)
    ImageView up_down_1;
    @BindView(R.id.up_down_2)
    ImageView up_down_2;
    @BindView(R.id.spinner)
    ClickControlledSpinner spinner;
    @BindView(R.id.spinner_true)
    ClickControlledSpinner spinner_true;
    @BindView(R.id.rl_spinner)
    RelativeLayout rl_spinner;
    private String awardGetType;
    private String isTrue;
    private boolean mBoolean_1;
    private boolean mBoolean_2;

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
            SystemBarTintManager tintManager = new SystemBarTintManager(GoodsListActivity2.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_goods_list);
        ButterKnife.bind(this);
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(GoodsListActivity2.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

    }

    @Override
    protected void initView() {
        setTitle("我的奖品");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(GoodsListActivity2.this, "404001_wdjp_back_click");
                finish();
            }
        });
        lv_goods_list = (PullToRefreshListView) findViewById(R.id.lv_goods_list);
        lv_goods_list.setOnItemClickListener(this);
        lv_goods_list.setMode(Mode.BOTH);
        lv_goods_list.setOnRefreshListener(this);
        adapter = new GoodsListAdapter(GoodsListActivity2.this, goodsTotalList);
        lv_goods_list.setAdapter(adapter);

        rl_goods_list = (RelativeLayout) findViewById(R.id.rl_goods_list);//有数据的时候
        rl_no_goods = (RelativeLayout) findViewById(R.id.rl_no_goods);//没有数据的时候

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progress));
        slowlyProgressBar.onProgressStart();
        SetSpinner();
        //setSpinnerItemSelectedByValue(spinner, "0");
    }

    private void SetSpinner() {
        //将可选内容与ArrayAdapter连接起来
        mArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, name);
        //设置下拉列表的风格
        mArrayAdapter.setDropDownViewResource(R.layout.dropdown_stytle);
        //将adapter2 添加到spinner中
        spinner.setAdapter(mArrayAdapter);

        mArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, name_true);
        mArrayAdapter.setDropDownViewResource(R.layout.dropdown_stytle);
        spinner_true.setAdapter(mArrayAdapter);

        spinner.setSelection(0, true);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
        spinner.setOnClickMyListener(new ClickControlledSpinner.OnClickMyListener() {
            @Override
            public void onClick() {//这是子线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//回到UI线程
                        //ToastUtils.show(GoodsListActivity.this, "11111");
                        spinner.performClick();
                        if (mBoolean_1) {
                            mBoolean_1 = false;
                            up_down_1.setBackgroundResource(R.drawable.pic_mall_myaward_triangle_2);
                        } else {
                            mBoolean_1 = true;
                            up_down_1.setBackgroundResource(R.drawable.pic_mall_myaward_triangle_1);
                        }
                    }
                });
            }
        });
        spinner_true.setSelection(0, true);
        //添加事件Spinner事件监听
        spinner_true.setOnItemSelectedListener(new SpinnerXMLSelectedListenerTrue());

        spinner_true.setOnClickMyListener(new ClickControlledSpinner.OnClickMyListener() {
            @Override
            public void onClick() {//这是子线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//回到UI线程
                        //ToastUtils.show(GoodsListActivity.this, "2222");
                        up_down_2.setBackgroundResource(R.drawable.pic_mall_myaward_triangle_1);
                        spinner_true.performClick();
                    }
                });
            }
        });
    }

    /**
     * Hides a spinner's drop down.
     */
    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        //            switch (v.getId()) {
        //                case R.id.rl_spinner:
        //                    up_down_1.setBackgroundResource(R.drawable.pic_mall_myaward_triangle_1);
        //                    break;
        //
        //            }

    }

    class SpinnerXMLSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            up_down_1.setBackgroundResource(R.drawable.pic_mall_myaward_triangle_2);
            //ToastUtils.show(GoodsListActivity.this, "333333");
            if (position == 0) {
                awardGetType = "";
            } else if (position == 1) {
                awardGetType = "1";
            } else if (position == 2) {
                awardGetType = "0";
            }
            mBoolean_1 = true;
            getData("3", LoginUserProvider.getUser(GoodsListActivity2.this).getToken(), pageNo, pageSize, REFRESH, awardGetType, isTrue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class SpinnerXMLSelectedListenerTrue implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            up_down_2.setBackgroundResource(R.drawable.pic_mall_myaward_triangle_2);
            //ToastUtils.show(GoodsListActivity.this, "333333");
            if (position == 0) {
                isTrue = "";
            } else if (position == 1) {
                isTrue = "0";
            } else if (position == 2) {
                isTrue = "1";
            }
            mBoolean_2 = true;
            getData("3", LoginUserProvider.getUser(GoodsListActivity2.this).getToken(), pageNo, pageSize, REFRESH, awardGetType, isTrue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    /**
     * 根据值, 设置spinner默认选中:
     *
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
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
    }

    //获取数据

    private void getData(String from, String token, final int pageNo, int pageSize, final int RequestCode, String awardGetType, String isTrue) {
        //CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(GoodsListActivity.this));
        String url = Urls.NEWUSERAWARDLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("awardGetType", awardGetType);
        params.put("isTrue", isTrue);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                //ll_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        int last = dataObj.getInt("last");//总页数
                        goodsList = new ArrayList<GoodsListEntity>();
                        JSONArray awardlistArray = dataObj.getJSONArray("awardlist");
                        if (awardlistArray.length() > 0) {
                            rl_goods_list.setVisibility(View.VISIBLE);
                            rl_no_goods.setVisibility(View.GONE);
                            for (int i = 0; i < awardlistArray.length(); i++) {
                                JSONObject obj = awardlistArray.getJSONObject(i);
                                GoodsListEntity entity = new GoodsListEntity();
                                entity.setMyAwardId(obj.getString("myAwardId"));
                                entity.setAwardId(obj.getString("awardId"));
                                entity.setAwardimgWeb(obj.getString("awardimgWap"));
                                entity.setDocs(obj.getString("docs"));
                                entity.setExpressNo(obj.getString("expressNo"));
                                entity.setState(obj.getString("state"));
                                entity.setAwardDate(obj.getString("awardDate"));
                                entity.setIsTrue(obj.getString("isTrue"));
                                entity.setAwardName(obj.getString("awardName"));
                                entity.setAwardNeedAmount(obj.getString("awardNeedAmount"));
                                entity.setDeadline(obj.getString("deadline"));
                                goodsList.add(entity);
                            }
                            setView(goodsList, RequestCode);
                            if (last == pageNo) {
                                lv_goods_list.onRefreshComplete();
                                lv_goods_list.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_goods_list.setMode(Mode.BOTH);
                            }

                        } else {
                            rl_no_goods.setVisibility(View.VISIBLE);
                            rl_goods_list.setVisibility(View.GONE);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(GoodsListActivity2.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(GoodsListActivity2.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(GoodsListActivity2.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(GoodsListActivity2.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(GoodsListActivity2.this, jsonObject.getString("message"), 0).show();
                    }
                    lv_goods_list.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_goods_list.onRefreshComplete();
                    Toast.makeText(GoodsListActivity2.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                lv_goods_list.onRefreshComplete();
                Toast.makeText(GoodsListActivity2.this, "请检查网络", 0).show();
                //				CustomProgress.CustomDismis();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pageNo = 1;
        getData("3", LoginUserProvider.getUser(GoodsListActivity2.this).getToken(), pageNo, pageSize, REFRESH, awardGetType, isTrue);
        lv_goods_list.setMode(Mode.BOTH);
    }

    protected void setView(List<GoodsListEntity> goodsList, int requestCode) {
        if (requestCode == REFRESH) {
            goodsTotalList.clear();
            if (goodsList != null && goodsList.size() > 0) {
                goodsTotalList.addAll(goodsList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            goodsTotalList.addAll(goodsList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getData("3", LoginUserProvider.getUser(GoodsListActivity2.this).getToken(), pageNo, pageSize, REFRESH, awardGetType, isTrue);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData("3", LoginUserProvider.getUser(GoodsListActivity2.this).getToken(), pageNo + 1, pageSize, LOADMORE, awardGetType, isTrue);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onEvent(GoodsListActivity2.this, "404002_wdjp_jplb_click");
        if (Lists.notEmpty(goodsTotalList)) {
            GoodsListEntity goodsListEntity = goodsTotalList.get(position - 1);
            //是否为虚拟奖品 1-是0-否
            if (goodsListEntity.getIsTrue().equals("0")) {
                if (goodsListEntity.getState().equals("0") || goodsListEntity.getState().equals("1") || goodsListEntity.getState().equals("2")) {
                    Intent intent = new Intent(GoodsListActivity2.this, OrderConfirmActivity.class);
                    intent.putExtra("myAwardId", goodsListEntity.getMyAwardId());
                    intent.putExtra("isReceive", "noReceive");
                    startActivity(intent);
                }
            } else if (goodsListEntity.getIsTrue().equals("1")) {
                if (goodsListEntity.getState().equals("1")) {
                    Intent investmentIntent = new Intent(GoodsListActivity2.this, MainActivity.class);
                    investmentIntent.putExtra("investment", "investmentFrom");
                    investmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(investmentIntent);
                }
            }
        }
    }
}
