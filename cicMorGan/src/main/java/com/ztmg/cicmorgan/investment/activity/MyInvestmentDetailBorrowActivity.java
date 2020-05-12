package com.ztmg.cicmorgan.investment.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BasePager;
import com.ztmg.cicmorgan.base.recyclerview.BaseRecyclerAdapter;
import com.ztmg.cicmorgan.entity.InvestmentDetailEntity;
import com.ztmg.cicmorgan.entity.RepaymentEntity;
import com.ztmg.cicmorgan.investment.fragment.IntroduceFragment;
import com.ztmg.cicmorgan.investment.fragment.LoanInfoFragment;
import com.ztmg.cicmorgan.investment.fragment.RecordingFragment;
import com.ztmg.cicmorgan.investment.fragment.SituationFragment;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.AndroidUtil;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.TimeUtil;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.MyViewPager;
import com.ztmg.cicmorgan.view.SlideDetailsLayout;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 功能  新版出借详情 (我的出借)
 * 时间  2018年5月18日
 * 作者  dong
 */

public class MyInvestmentDetailBorrowActivity extends BaseNewInvestmentDetailActivity implements View.OnClickListener, SlideDetailsLayout.OnSlideDetailsListener, ViewPager.OnPageChangeListener {

    public LinearLayout ll_pull_up;
    private RelativeLayout mRelativeLayout_back_plan;
    private TextView tv_project_time, tv_once_money, tv_remaining_sum, tv_percent_num;
    private TextView tv_payment_style, tv_interest_time, tv_schedule;
    public MainActivity activity;
    private MyViewPager viewpager;
    private View include_gone;
    private int cursorWidth;
    private int currIndex = 0;
    private List<BasePager> pagers;
    private View cursor;
    //private String projectid;
    private LinearLayout lin_content;

    private LinearLayout addView_lin_repayment;
    private BaseRecyclerAdapter RecyclerAdapter;
    private Button bt_investment;
    private ImageView image_down;
    private boolean isBoolean = true;
    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;

    private TextView[] texts;
    private View[] views;

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
            SystemBarTintManager tintManager = new SystemBarTintManager(MyInvestmentDetailBorrowActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_investment_detail_new);
        initView();
        initListener();
        Intent intent = getIntent();
        projectid = intent.getStringExtra("projectid");
        getData("3", projectid);

        //回款计划接口
        if (!StringUtils.isBlank(ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
            String s = ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
            if (s.equals("3")) {
                String bidid = intent.getStringExtra("bidid");
                getPaymentList("3", LoginUserProvider.getUser(MyInvestmentDetailBorrowActivity.this).getToken(), bidid);
                for (int i = addView_lin_repayment.getChildCount(); i >= 0; i--) {
                    View childAt = addView_lin_repayment.getChildAt(i);
                    addView_lin_repayment.removeView(childAt);
                }
            }
        }
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MyInvestmentDetailBorrowActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
        Constant.isboolean = false;
    }


    private void initListener() {
        ll_pull_up.setOnClickListener(this);
        sv_switch.setOnSlideDetailsListener(this);
    }

    protected void initView() {
        setTitle("");
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(MyInvestmentDetailBorrowActivity.this, "202001_project_details_back_click");
                    } else if (s.equals("3")) {
                        onEvent(MyInvestmentDetailBorrowActivity.this, "202008_project_details_cj_back_click");
                    }
                }
                finish();
            }
        });

        tv_project_time = (TextView) findViewById(R.id.tv_project_time);// 期限
        tv_once_money = (TextView) findViewById(R.id.tv_once_money);// 起投金额
        tv_remaining_sum = (TextView) findViewById(R.id.tv_remaining_sum);// 可投金额
        tv_percent_num = (TextView) findViewById(R.id.tv_percent_num);//利率
        tv_payment_style = (TextView) findViewById(R.id.tv_payment_style);
        tv_interest_time = (TextView) findViewById(R.id.tv_interest_time);
        tv_schedule = (TextView) findViewById(R.id.tv_schedule);

        bt_investment = (Button) findViewById(R.id.bt_investment);
        bt_investment.setOnClickListener(this);

        mRelativeLayout_back_plan = (RelativeLayout) findViewById(R.id.mRelativeLayout_back_plan);
        mRelativeLayout_back_plan.setOnClickListener(this);

        ll_pull_up = (LinearLayout) findViewById(R.id.ll_pull_up);
        sv_switch = (SlideDetailsLayout) findViewById(R.id.sv_switch);
        //mRecyclerView = (RecyclerView) findViewById(R.id.add_back_plan);
        image_down = (ImageView) findViewById(R.id.image_down);
        image_down.setBackgroundResource(R.drawable.icon_unfold);
        addView_lin_repayment = (LinearLayout) findViewById(R.id.addView_lin_repayment);
        addView_lin_repayment.setOnClickListener(this);

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
        // v_tab_cursor = findViewById(R.id.v_tab_cursor);

        viewpager = (MyViewPager) findViewById(R.id.view_pager);
        viewpager.setScrollable(false);
        //cursor = findViewById(R.id.cursor);
        //mRelativeLayouts = new RelativeLayout[4];
        //mRelativeLayouts[0] = (RelativeLayout) findViewById(R.id.rl0);
        //mRelativeLayouts[1] = (RelativeLayout) findViewById(R.id.rl1);
        //mRelativeLayouts[2] = (RelativeLayout) findViewById(R.id.rl2);
        //mRelativeLayouts[3] = (RelativeLayout) findViewById(R.id.rl3);

        texts = new TextView[4];
        texts[0] = (TextView) findViewById(R.id.tv_goods_introduce);
        texts[1] = (TextView) findViewById(R.id.tv_goods_info);
        texts[2] = (TextView) findViewById(R.id.tv_goods_situation);
        texts[3] = (TextView) findViewById(R.id.tv_goods_recording);

        views = new View[4];
        views[0] = (View) findViewById(R.id.view0);
        views[1] = (View) findViewById(R.id.view1);
        views[2] = (View) findViewById(R.id.view2);
        views[3] = (View) findViewById(R.id.view3);

        for (int i = 0; i < texts.length; i++) {
            texts[i].setOnClickListener(this);
            if (i == 0) {
                texts[0].setTextColor(getResources().getColor(R.color.text_a11c3f));
                //texts[0].setTextSize(14);
                views[0].setVisibility(View.VISIBLE);
            }
        }
        //initCursor();
        pagers = new ArrayList<>();
        pagers.add(new IntroduceFragment(0, this));
        pagers.add(new LoanInfoFragment(1, this));
        pagers.add(new SituationFragment(2, this));
        pagers.add(new RecordingFragment(3, this));

        viewpager.setAdapter(new ViewPagerAdapter());
        viewpager.setOnPageChangeListener(this);
        //pagers.get(0).initData("1");
        //pagers.get(0);
        LogUtil.e("=====================" + ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key));
        //判断是否显示付款方
        if (!StringUtils.isBlank(ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
            String s = ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
            if (s.equals("1") || s.equals("2")) {
                mRelativeLayout_back_plan.setVisibility(View.INVISIBLE);
            } else if (s.equals("3")) {
                bt_investment.setVisibility(View.GONE);
                margin(ll_pull_up, 0, 200, 0, 0);
            }
        }
        include_gone = findViewById(R.id.include_gone);
        category_layout = (LinearLayout) findViewById(R.id.category_layout);
        category_layout.setVisibility(View.INVISIBLE);
        //login();
    }

    // 获取数据
    private void getData(String from, final String projectid) {
        //CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
        String url = Urls.GETPROJECTINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("projectid", projectid);
        client.post(url, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        //rl_content.setVisibility(View.VISIBLE);
                        String result = new String(responseBody);
                        ACache.get(MyInvestmentDetailBorrowActivity.this).put(Constant.InvestmentDetailKey, "");//如果请求网络，先清理本地内存，然后再进行存储
                        ACache.get(MyInvestmentDetailBorrowActivity.this).put(Constant.InvestmentDetailKey, result);

                        //String json = ACache.get(NewInvestmentDetailActivity.this).getAsString(Constant.InvestmentDetailKey);
                        LogUtil.e("=========详情====================" + result);
                        investmentDetailEntity = GsonManager.fromJson(result, InvestmentDetailEntity.class);
                        //接口返回4 token失效 手势密码
                        if (investmentDetailEntity.getState().equals("4")) {//系统超时
                            String mGesture = LoginUserProvider.getUser(MyInvestmentDetailBorrowActivity.this).getGesturePwd();
                            if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                                //设置手势密码
                                Intent intent = new Intent(MyInvestmentDetailBorrowActivity.this, UnlockGesturePasswordActivity.class);
                                intent.putExtra("overtime", "0");
                                startActivity(intent);
                            } else {
                                //未设置手势密码
                                Intent intent = new Intent(MyInvestmentDetailBorrowActivity.this, LoginActivity.class);
                                intent.putExtra("overtime", "0");
                                startActivity(intent);
                            }
                            DoCacheUtil util = DoCacheUtil.get(MyInvestmentDetailBorrowActivity.this);
                            util.put("isLogin", "");
                        } else if (investmentDetailEntity.getState().equals("0")) {
                            mindex = 5;
                            mHandler.sendEmptyMessage(1);
                            InvestmentDetailEntity.DataBean dataJson = investmentDetailEntity.getData();
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                            setTitle(dataJson.getProjectName());// 项目名字
                            tv_percent_num.setText(decimalFormat.format(Double.parseDouble(dataJson.getRate())));
                            tv_remaining_sum.setText(decimalFormat.format(Double.parseDouble(dataJson.getBalanceamount())));// 剩余可投资金额
                            tv_project_time.setText(dataJson.getSpan() + "");// 项目期限
                            tv_once_money.setText(decimalFormat.format(Double.parseDouble(dataJson.getMinamount())));// 起投金额

                            tv_payment_style.setText(dataJson.getRepaytype());//还款方式
                            tv_interest_time.setText(dataJson.getLoandate());//起息时间

                            if (!dataJson.getAmount().equals("0.0")) {
                                double percentage = (Double.parseDouble(dataJson.getCurrentamount()) / Double.parseDouble(dataJson.getAmount())) * 100;
                                BigDecimal bd = new BigDecimal(percentage).setScale(0, BigDecimal.ROUND_HALF_UP);
                                int mPercentageInt = (int) bd.doubleValue();
                                //if (percentageInt1 == 100) {
                                //    if (dataJson.getCurrentamount().equals(dataJson.getAmount())) {
                                //        //holder.sb_progress.setProgress(100);
                                //        tv_schedule.setText("100%");
                                //    } else {
                                //        tv_schedule.setText("99%");
                                //    }
                                //} else if (percentageInt1 == 0) {
                                //    if (dataJson.getCurrentamount().equals("0.0")) {
                                //        tv_schedule.setText("0%");
                                //    } else {
                                //        tv_schedule.setText("1%");
                                //    }
                                //} else {
                                //    tv_schedule.setText("" + percentageInt1 + "%");
                                //}
                                //}
                                if (percentage == 100) {
                                    tv_schedule.setText("100%");
                                } else if (percentage > 99.5 && percentage < 100) {
                                    tv_schedule.setText("99%");
                                } else if (percentage < 0.5 && percentage > 0) {
                                    tv_schedule.setText("1%");
                                } else if (percentage == 0.0) {
                                    tv_schedule.setText("0%");
                                } else {
                                    tv_schedule.setText(mPercentageInt + "%");
                                }
                            } else {
                                tv_schedule.setText("0%");//募集进度
                            }
                            isJiJiang = false;
                            isLiJi = false;
                            String proState = dataJson.getProState();
                            if (proState != null && proState.equals("4")) {
                                // bt_investment.setText("立即出借");
                                // bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.text_a11c3f));
                                // bt_investment.setClickable(true);
                                if (!StringUtils.isEmpty(dataJson.getLoandate()) /*&& !dataJson.getLoandate().equals("null")*/) {
                                    boolean isBidders = TimeUtil.compareInverstmentListNowTime(dataJson.getLoandate());
                                    if (isBidders) {
                                        bt_investment.setText("立即出借");
                                        //category_layout.setVisibility(View.VISIBLE);
                                        upDetail();
                                        //isLiJi = true;
                                        //bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.text_a11c3f));
                                        bt_investment.setBackgroundResource(R.drawable.bt_red);
                                        bt_investment.setTextColor(getResources().getColor(R.color.white));
                                        bt_investment.setClickable(true);
                                    } else {
                                        bt_investment.setText("已到期");
                                        upDetail();
                                        //isLiJi = true;
                                        //bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.gray));
                                        bt_investment.setBackgroundResource(R.drawable.bt_gray);
                                        bt_investment.setTextColor(getResources().getColor(R.color.white));
                                        bt_investment.setClickable(false);
                                    }
                                }
                            } else if (!StringUtils.isEmpty(proState) && proState.equals("3")) {
                                bt_investment.setText("即将上线");
                                //category_layout.setVisibility(View.VISIBLE);
                                upDetail();
                                //isJiJiang = true;
                                //bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.text_a11c3f));
                                bt_investment.setBackgroundResource(R.drawable.button_red);
                                bt_investment.setTextColor(getResources().getColor(R.color.text_d40f42));
                                bt_investment.setClickable(false);
                            } else if (!StringUtils.isEmpty(proState) && proState.equals("6")) {
                                bt_investment.setText("还款中");
                                upDetail();
                                //bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.gray));
                                bt_investment.setBackgroundResource(R.drawable.bt_gray);
                                bt_investment.setTextColor(getResources().getColor(R.color.white));
                                bt_investment.setClickable(false);
                            } else if (!StringUtils.isEmpty(proState) && proState.equals("7")) {
                                bt_investment.setText("已还完");
                                upDetail();
                                //bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.gray));
                                bt_investment.setBackgroundResource(R.drawable.bt_gray);
                                bt_investment.setTextColor(getResources().getColor(R.color.white));
                                bt_investment.setClickable(false);
                            } else if (!StringUtils.isEmpty(proState) && proState.equals("5")) {
                                bt_investment.setText("已满标");//还款中
                                upDetail();
                                //bt_investment.setBackgroundColor(NewInvestmentDetailActivity.this.getResources().getColor(R.color.gray));
                                bt_investment.setBackgroundResource(R.drawable.bt_gray);
                                bt_investment.setTextColor(getResources().getColor(R.color.white));
                                bt_investment.setClickable(false);
                            }
                            pagers.get(0).initData(projectid);
                        } else {
                            ToastUtils.show(MyInvestmentDetailBorrowActivity.this, investmentDetailEntity.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //Toast.makeText(NewInvestmentDetailActivity.this, "请检查网络", 0).show();
                        ToastUtils.show(MyInvestmentDetailBorrowActivity.this, "请检查网络");
                        CustomProgress.CustomDismis();
                    }
                }
        );
    }

    //判断是否是我的出借 显示上拉详情
    private void upDetail() {
        if (!StringUtils.isBlank(ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
            String s = ACache.get(MyInvestmentDetailBorrowActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
            if (s.equals("3")) {
                category_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    //获取回款列表
    private void getPaymentList(String from, String token, String investId) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETUSERINTERESTLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("investId", investId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                mindex = 5;
                //mHandler.sendEmptyMessage(1);
                String result = new String(responseBody);
                try {
                    RepaymentEntity repaymentEntity = GsonManager.fromJson(result, RepaymentEntity.class);
                    if (repaymentEntity.getState().equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyInvestmentDetailBorrowActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyInvestmentDetailBorrowActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyInvestmentDetailBorrowActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyInvestmentDetailBorrowActivity.this);
                        util.put("isLogin", "");
                    } else if (repaymentEntity.getState().equals("0")) {
                        //正常状态返回
                        List<RepaymentEntity.DataEntity.UserPlanListEntity> userPlanList = repaymentEntity.getData().getUserPlanList();

                        for (int i = 0; i < userPlanList.size(); i++) {
                            RepaymentEntity.DataEntity.UserPlanListEntity userPlanListEntity = userPlanList.get(i);
                            final LinearLayout layout = (LinearLayout) LayoutInflater.from(MyInvestmentDetailBorrowActivity.this).inflate(R.layout.activity_my_investment_payment_item, null);
                            TextView tv_payment_state = (TextView) layout.findViewById(R.id.tv_payment_state);//还款状态
                            TextView tv_date = (TextView) layout.findViewById(R.id.tv_date);//还款时间
                            TextView tv_money = (TextView) layout.findViewById(R.id.tv_money);//还款金额
                            TextView tv_investment_detail_payment_type = (TextView) layout.findViewById(R.id.tv_investment_detail_payment_type);//还款类型
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足

                            if (userPlanListEntity.getState().equals("2")) {
                                tv_payment_state.setBackgroundResource(R.drawable.bg_ellipse_gray);
                                tv_payment_state.setText("未还款");// 2 未还款 3 已还款
                                if (userPlanListEntity.getPrincipal().equals("2")) {//2利息  1本息  bg_orange_yellow.xml
                                    tv_investment_detail_payment_type.setText("利息");
                                } else if (userPlanListEntity.getPrincipal().equals("1")) {
                                    tv_investment_detail_payment_type.setText("本金");
                                }
                                tv_investment_detail_payment_type.setBackgroundResource(R.drawable.bg_orange);
                                tv_investment_detail_payment_type.setTextColor(getResources().getColor(R.color.text_cccccc));

                            } else if (userPlanListEntity.getState().equals("3")) {
                                tv_payment_state.setBackgroundResource(R.drawable.bg_ellipse_blue);
                                tv_payment_state.setText("正常还款");// 2 未还款 3 已还款

                                if (userPlanListEntity.getPrincipal().equals("2")) {//2利息  1本息  bg_orange_yellow.xml
                                    tv_investment_detail_payment_type.setText("利息");
                                } else if (userPlanListEntity.getPrincipal().equals("1")) {
                                    tv_investment_detail_payment_type.setText("本金");
                                }
                                tv_investment_detail_payment_type.setBackgroundResource(R.drawable.bg_orange_yellow);
                                tv_investment_detail_payment_type.setTextColor(getResources().getColor(R.color.text_cbb693));
                            }
                            tv_date.setText(userPlanListEntity.getRepaymentDate());
                            tv_money.setText(decimalFormat.format(Double.parseDouble(userPlanListEntity.getAmount())));

                            addView_lin_repayment.addView(layout);
                        }
                    } else {
                        ToastUtils.show(MyInvestmentDetailBorrowActivity.this, repaymentEntity.getMessage());
                    }

                } catch (Exception e) {
                    ToastUtils.show(MyInvestmentDetailBorrowActivity.this, "解析异常");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(NewInvestmentDetailActivity.this, "请检查网络", 0).show();
                //CustomProgress.CustomDismis();
                ToastUtils.show(MyInvestmentDetailBorrowActivity.this, "请检查网络");
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (AndroidUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_pull_up:
                //上拉查看图文详情
                sv_switch.smoothOpen(true);
                break;
            case R.id.tv_goods_introduce:
                viewpager.setScrollable(false);
                viewpager.setCurrentItem(0, false);
                break;
            case R.id.tv_goods_info:
                viewpager.setScrollable(false);
                viewpager.setCurrentItem(1, false);
                break;
            case R.id.tv_goods_situation:
                viewpager.setScrollable(false);
                viewpager.setCurrentItem(2, false);
                break;
            case R.id.tv_goods_recording:
                viewpager.setScrollable(false);
                viewpager.setCurrentItem(3, false);
                break;
            case R.id.mRelativeLayout_back_plan://回款计划
                if (isBoolean) {
                    addView_lin_repayment.setVisibility(View.VISIBLE);
                    image_down.setBackgroundResource(R.drawable.icon_fold);
                    isBoolean = false;
                } else {
                    onEvent(MyInvestmentDetailBorrowActivity.this, "202001_project_details_back_click");
                    addView_lin_repayment.setVisibility(View.GONE);
                    image_down.setBackgroundResource(R.drawable.icon_unfold);
                    isBoolean = true;
                }
                break;
            case R.id.bt_investment://button 出借
                Constant.isboolean = false;
                onEvent(MyInvestmentDetailBorrowActivity.this, "202007_project_ljcj_tab_click");
                if (LoginUserProvider.getUser(this) != null) {
                    DoCacheUtil doCacheUtil = DoCacheUtil.get(this);
                    if (!StringUtils.isEmpty(doCacheUtil.getAsString("isLogin"))) {
                        if ("isLogin".equals(doCacheUtil.getAsString("isLogin"))) {//去判断 验证token
                            Constant.isboolean = true;
                            initAccountDataToken(LoginUserProvider.getUser(MyInvestmentDetailBorrowActivity.this).getToken(), "3");
                        }
                    } else {
                        Constant.isboolean = false;
                        login();
                    }
                } else {
                    Constant.isboolean = false;
                    login();
                }
                break;
            default:
                break;
        }
    }


    //往上拉的时候，
    @Override
    public void onStatucChanged(SlideDetailsLayout.Status status) {
        mStatus = status;
        if (status.name().equals("OPEN")) {
            if (LoginUserProvider.getUser(this) != null) {
                DoCacheUtil doCacheUtil = DoCacheUtil.get(this);
                if (!StringUtils.isEmpty(doCacheUtil.getAsString("isLogin"))) {
                    if ("isLogin".equals(doCacheUtil.getAsString("isLogin"))) {
                        if (bt_investment.getText().toString().equals("立即出借") || bt_investment.getText().toString().equals("即将上线")) {
                            category_layout.setVisibility(View.VISIBLE);
                            //Constant.isboolean = false;
                            //initAccountDataToken(LoginUserProvider.getUser(NewInvestmentDetailActivity.this).getToken(), "3");
                        } else {
                            phone(status);
                        }
                    }
                } else {
                    login();
                    sv_switch.smoothClose(true); //关闭上拉
                }
            } else {
                login();
                sv_switch.smoothClose(true); //关闭上拉
            }

        }
    }
    //    /**
    //     * 滑动游标
    //     */
    //    private void scrollCursor() {
    //        TranslateAnimation anim = new TranslateAnimation(fromX, nowIndex * v_tab_cursor.getWidth(), 0, 0);
    //        anim.setFillAfter(true);//设置动画结束时停在动画结束的位置
    //        anim.setDuration(50);
    //        //保存动画结束时游标的位置,作为下次滑动的起点
    //        fromX = nowIndex * v_tab_cursor.getWidth();
    //        v_tab_cursor.startAnimation(anim);
    //
    //        //设置Tab切换颜色
    //        for (int i = 0; i < tabTextList.size(); i++) {
    //            tabTextList.get(i).setTextColor(i == nowIndex ? getResources().getColor(R.color.text_a11c3f) : getResources().getColor(R.color.text_34393c));
    //        }
    //    }

    //    /**
    //     * 切换Fragment
    //     * <p>(hide、show、add)
    //     *
    //     * @param fromFragment
    //     * @param toFragment
    //     */
    //    private void switchFragment(Fragment fromFragment, Fragment toFragment) {
    //        if (nowFragment != toFragment) {
    //            fragmentTransaction = fragmentManager.beginTransaction();
    //            if (!toFragment.isAdded()) {    // 先判断是否被add过
    //                fragmentTransaction.hide(fromFragment).add(R.id.fl_content, toFragment).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到activity中
    //            } else {
    //                fragmentTransaction.hide(fromFragment).show(toFragment).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
    //            }
    //        }
    //    }


    //    private void initCursor() {
    //        DisplayMetrics dm = new DisplayMetrics();
    //        getWindowManager().getDefaultDisplay().getMetrics(dm);
    //        int screenW = dm.widthPixels;// 获取分辨率宽度
    //        cursorWidth = screenW / texts.length;
    //        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cursorWidth, CommonUtil.dip2px(this, 2));
    //        cursor.setLayoutParams(params);
    //    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //        Animation animation = new TranslateAnimation(currIndex * cursorWidth, position * cursorWidth, 0, 0);
        //        animation.setFillAfter(true);
        //        animation.setDuration(200);
        //        cursor.startAnimation(animation);
        for (int i = 0; i < texts.length; i++) {
            texts[i].setTextColor(getResources().getColor(R.color.text_34393c));
            views[i].setVisibility(View.INVISIBLE);
        }
        texts[position].setTextColor(getResources().getColor(R.color.text_a11c3f));
        texts[position].setTextSize(15);
        //texts[currIndex].setTextColor(Color.BLACK);
        //texts[currIndex].setTextSize(14);
        //currIndex = position;
        pagers.get(position).initData(projectid);
        views[position].setVisibility(View.VISIBLE);
        //pagers.get(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * viewpager的adapter
     */
    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(pagers.get(position).getRootView());
            return pagers.get(position).getRootView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

    }

    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
        mHandler.removeMessages(1);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
