package com.ztmg.cicmorgan.more.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.BackPaymentPlanActivity;
import com.ztmg.cicmorgan.base.BaseActivityCommon;
import com.ztmg.cicmorgan.base.recyclerview.BaseRecyclerAdapter;
import com.ztmg.cicmorgan.base.recyclerview.RecyclerViewHolder;
import com.ztmg.cicmorgan.integral.activity.IntegralRuleActivity;
import com.ztmg.cicmorgan.integral.activity.MyIntegralActivity;
import com.ztmg.cicmorgan.more.entity.IntegralDetailEntity;
import com.ztmg.cicmorgan.more.entity.ScoreEntity;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.FullyGridLayoutManager;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 积分明细  dong dong
 * on 2018/8/27.
 */

public class IntegralDetailActivity extends BaseActivityCommon implements OnRefreshLoadMoreListener, View.OnClickListener {

    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private BaseRecyclerAdapter RecyclerAdapter;
    private String bounsType = "-1";
    private List<IntegralDetailEntity.DataBean.UserBounsHistoryBean> mIntegralDetailEntities = new ArrayList<>();
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.rl_no_recharge_message)
    LinearLayout rl_no_recharge_message;
    @BindView(R.id.integral)
    TextView integral;
    private SlowlyProgressBar slowlyProgressBar;
    private int mindex;
    private int newProgress = 0;
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
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(IntegralDetailActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_integral_detail);
        ButterKnife.bind(this);
        setTitle("积分明细");
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(IntegralDetailActivity.this, "315001_jfmx_back_click");
                finish();
            }
        });
        mRecyclerView.setLayoutManager(new FullyGridLayoutManager(this, 1, FullyGridLayoutManager.VERTICAL, false));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(this);
        mSmartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(IntegralDetailActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


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

    //初次加载
    @Override
    protected void initData() {
        mRecyclerView.removeAllViews();
        RecyclerAdapter = null;
        pageNo = 1;
        mIntegralDetailEntities.clear();
        getUserBouns();
        getData(pageNo, pageSize, bounsType);
    }

    //二次加载
    private void getData(int pageNo, int pageSize, String bounsType) {
        CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        HashMap<String, String> params = new HashMap();
        params.put("from", "3");
        params.put("token", LoginUserProvider.getUser(IntegralDetailActivity.this).getToken());
        params.put("pageNo", pageNo + "");
        params.put("pageSize", pageSize + "");
        params.put("bounsType", bounsType);
        OkUtil.post("USERBOUNSHISTORY", Urls.USERBOUNSHISTORY, params, IntegralDetailActivity.this, this);
    }

    //我的积分
    private void getUserBouns() {
        HashMap<String, String> params = new HashMap();
        params.put("from", "3");
        params.put("token", LoginUserProvider.getUser(IntegralDetailActivity.this).getToken());
        OkUtil.post("USERBOUNS", Urls.USERBOUNS, params, IntegralDetailActivity.this, this);
    }


    @Override
    protected void responseData(String stringId, String json) {
        CustomProgress.CustomDismis();
        mSmartRefreshLayout.finishRefresh();//结束刷新
        mSmartRefreshLayout.finishLoadMore();//结束加载
        switch (stringId) {
            case "USERBOUNSHISTORY":
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                IntegralDetailEntity numberEntity = GsonManager.fromJson(json, IntegralDetailEntity.class);
                if (!Lists.isEmpty(numberEntity.getData().getUserBounsHistory())) {
                    rl_no_recharge_message.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //筛选
                    for (int i = numberEntity.getData().getUserBounsHistory().size() - 1; i >= 0; i--) {
                        IntegralDetailEntity.DataBean.UserBounsHistoryBean userBounsHistoryBean = numberEntity.getData().getUserBounsHistory().get(i);
                        if (userBounsHistoryBean.getAmount().equals("0.0")) {
                            numberEntity.getData().getUserBounsHistory().remove(i);
                        }
                    }
                    String last = numberEntity.getData().getLast();
                    if (pageNo == 1) {
                        if (last.equals(pageNo + "")) {//总页数
                            mIntegralDetailEntities.addAll(numberEntity.getData().getUserBounsHistory());
                            RecyclerViewAdapter(mIntegralDetailEntities);
                            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                        } else {
                            mIntegralDetailEntities.addAll(numberEntity.getData().getUserBounsHistory());
                            RecyclerViewAdapter(mIntegralDetailEntities);
                            mSmartRefreshLayout.setNoMoreData(false);
                        }
                    } else if (pageNo > 1) {
                        if (last.equals(pageNo + "")) {//总页数
                            mIntegralDetailEntities.addAll(numberEntity.getData().getUserBounsHistory());
                            RecyclerViewAdapter(mIntegralDetailEntities);
                            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                        } else {
                            mIntegralDetailEntities.addAll(numberEntity.getData().getUserBounsHistory());
                            RecyclerViewAdapter(mIntegralDetailEntities);
                            mSmartRefreshLayout.setNoMoreData(false);
                        }
                    }
                } else {
                    rl_no_recharge_message.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mSmartRefreshLayout.setEnableLoadMore(false);//是否启用上拉加载功能
                }
                break;
            case "USERBOUNS":
                ScoreEntity scoreEntity = GsonManager.fromJson(json, ScoreEntity.class);
                integral.setText(scoreEntity.getData().getScore());
                break;
        }
    }

    /**
     * RecyclerView 还款计划  需要单独调用接口
     *
     * @param bidList
     */
    private void RecyclerViewAdapter(final List<IntegralDetailEntity.DataBean.UserBounsHistoryBean> bidList) {
        if (RecyclerAdapter == null) {
            RecyclerAdapter = new BaseRecyclerAdapter<IntegralDetailEntity.DataBean.UserBounsHistoryBean>(this, bidList) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.activity_item_integral_detail;
                }

                @Override
                public void bindData(RecyclerViewHolder holder, int position, IntegralDetailEntity.DataBean.UserBounsHistoryBean item) {
                    holder.getTextView(R.id.tv_name).setText(item.getBounsType());
                    holder.getTextView(R.id.tv_date).setText(item.getCreateDate());
                    if ("-".equals(item.getAmount().substring(0, 1))) {
                        holder.getTextView(R.id.tv_minute).setText(item.getAmount());
                        holder.getTextView(R.id.tv_minute).setTextColor(getResources().getColor(R.color.text_ff0000));
                    } else {
                        holder.getTextView(R.id.tv_minute).setText("+" + item.getAmount());
                    }
                }
            };
            mRecyclerView.setAdapter(RecyclerAdapter);
            // RecyclerAdapter.setOnItemClickListener(this);
        } else {
            if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) {
                RecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    //刷新
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        initData();
    }

    //加载
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        ++pageNo;
        getData(pageNo, pageSize, bounsType);
    }

    @BindView(R.id.ll__integral_rule)
    LinearLayout ll__integral_rule;
    @BindView(R.id.ll__integral_lottery)
    LinearLayout ll__integral_lottery;

    @BindView(R.id.ll_all)
    LinearLayout ll_all;
    @BindView(R.id.ll__integral_get)
    LinearLayout ll__integral_get;
    @BindView(R.id.ll__integral_consumption)
    LinearLayout ll__integral_consumption;


    @BindView(R.id.text_all)
    TextView text_all;
    @BindView(R.id.text_integral_get)
    TextView text_integral_get;
    @BindView(R.id.text_integral_consumption)
    TextView text_integral_consumption;


    @BindView(R.id.im_all)
    ImageView im_all;
    @BindView(R.id.im_integral_get)
    ImageView im_integral_get;
    @BindView(R.id.im_integral_consumption)
    ImageView im_integral_consumption;


    @OnClick({R.id.ll__integral_rule, R.id.ll__integral_lottery, R.id.ll_all, R.id.ll__integral_get, R.id.ll__integral_consumption})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll__integral_rule://积分规则
                onEvent(IntegralDetailActivity.this, "315002_jfmx_jfgz_click");
                Intent intent = new Intent(this, IntegralRuleActivity.class);
                //intent.putExtra("overtime", "0");
                startActivity(intent);
                break;
            case R.id.ll__integral_lottery://积分抽奖
                onEvent(IntegralDetailActivity.this, "315003_jfmx_jfcj_click");
                Intent mIntent = new Intent(this, MyIntegralActivity.class);
                //intent.putExtra("overtime", "0");
                startActivity(mIntent);
                break;
            case R.id.ll_all:
                onEvent(IntegralDetailActivity.this, "315004_jfmx_sxq_click");
                text_all.setTextColor(getResources().getColor(R.color.text_cbb693));
                im_all.setVisibility(View.VISIBLE);

                text_integral_get.setTextColor(getResources().getColor(R.color.text_34393c));
                im_integral_get.setVisibility(View.INVISIBLE);

                text_integral_consumption.setTextColor(getResources().getColor(R.color.text_34393c));
                im_integral_consumption.setVisibility(View.INVISIBLE);
                bounsType = "-1";
                initData();
                break;
            case R.id.ll__integral_get:
                onEvent(IntegralDetailActivity.this, "315004_jfmx_sxq_click");
                text_all.setTextColor(getResources().getColor(R.color.text_34393c));
                im_all.setVisibility(View.INVISIBLE);


                text_integral_get.setTextColor(getResources().getColor(R.color.text_cbb693));
                im_integral_get.setVisibility(View.VISIBLE);


                text_integral_consumption.setTextColor(getResources().getColor(R.color.text_34393c));
                im_integral_consumption.setVisibility(View.INVISIBLE);

                bounsType = "1";
                initData();
                break;
            case R.id.ll__integral_consumption:
                onEvent(IntegralDetailActivity.this, "315004_jfmx_sxq_click");
                text_all.setTextColor(getResources().getColor(R.color.text_34393c));
                im_all.setVisibility(View.INVISIBLE);

                text_integral_get.setTextColor(getResources().getColor(R.color.text_34393c));
                im_integral_get.setVisibility(View.INVISIBLE);

                text_integral_consumption.setTextColor(getResources().getColor(R.color.text_cbb693));
                im_integral_consumption.setVisibility(View.VISIBLE);

                bounsType = "2";
                initData();
                break;
        }
    }
}
