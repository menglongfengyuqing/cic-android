package com.ztmg.cicmorgan.account.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.calendar.ncalendar.calendar.NCalendar;
import com.ztmg.cicmorgan.account.calendar.ncalendar.entity.PaymentEntity;
import com.ztmg.cicmorgan.account.calendar.ncalendar.listener.OnCalendarChangedListener;
import com.ztmg.cicmorgan.base.BaseActivityCom;
import com.ztmg.cicmorgan.base.recyclerview.BaseRecyclerAdapter;
import com.ztmg.cicmorgan.base.recyclerview.RecyclerViewHolder;
import com.ztmg.cicmorgan.calendar.models.BeanDate;
import com.ztmg.cicmorgan.net.Common;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.FullyGridLayoutManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 回款日历
 * dong
 * 2018年8月13日 18:46:55
 */
public class BackPaymentPlanActivity extends BaseActivityCom implements OnCalendarChangedListener, OnRefreshLoadMoreListener {

    private NCalendar ncalendar;
    private TextView tv_month;
    private List<BeanDate> paymentList = new ArrayList<>();
    private TextView tv_today_money, tv_money_money;
    private List<PaymentEntity.DataBean.PlansBean> plansList = new ArrayList<>();
    private List<PaymentEntity> backPaymentList;
    private List<PaymentEntity> backPaymentTotalList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private String clickDate;//点击当前日期
    private String currentMonth;//当前月份
    private String currentYear;//当前年
    private TextView tv_no_payment;
    private RelativeLayout rl_payment_plan_title;
    private View v_line;
    private RecyclerView rl;
    RelativeLayout rl_left_date;
    int mMonth;
    int mYear;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BackPaymentPlanActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_new_back_payment_plan);
        ButterKnife.bind(this);
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(BackPaymentPlanActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
        //ncalendar.setDateInterval("2017-04-02", "2018-01-01");
        Calendar c = Calendar.getInstance();
        //系统获取当前年份
        mYear = c.get(Calendar.YEAR);
        //系统获取当前月份
        mMonth = c.get(Calendar.MONTH) + 1;
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

    @Override
    protected void initView() {
        setTitle("回款日历");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(BackPaymentPlanActivity.this, "209001_hkrl_back_click");
                finish();
            }
        });
        mSmartRefreshLayout.setEnableRefresh(false);//是否启用下拉刷新功能
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(this);
        mSmartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4

        mRecyclerView.setLayoutManager(new FullyGridLayoutManager(this, 1, FullyGridLayoutManager.VERTICAL, false));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);


        ncalendar = (NCalendar) findViewById(R.id.mNCalendar);
        rl = (RecyclerView) findViewById(R.id.rl);
        rl.setLayoutManager(new LinearLayoutManager(BackPaymentPlanActivity.this));
        backPaymentTotalList = new ArrayList<>();

        tv_month = (TextView) findViewById(R.id.tv_month);
        //tv_date = (TextView) findViewById(R.id.tv_date);
        tv_today_money = (TextView) findViewById(R.id.tv_today_money);//当日回款本息
        tv_money_money = (TextView) findViewById(R.id.tv_money_money);//当月回款本息
        tv_no_payment = (TextView) findViewById(R.id.tv_no_payment);//没有回款
        rl_payment_plan_title = (RelativeLayout) findViewById(R.id.rl_payment_plan_title);//回款列表标题
        v_line = findViewById(R.id.v_line);//分界线
        rl_left_date = (RelativeLayout) findViewById(R.id.rl_left_date);
        rl_left_date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ncalendar.toLastPager();
            }
        });
        findViewById(R.id.rl_right_date).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ncalendar.toNextPager();
            }
        });
        ncalendar.setOnCalendarChangedListener(this);
    }

    @Override
    protected void initData() {
        paymentList.clear();
        Common.dateList.clear();
        CustomProgress.show(this);
        HashMap<String, String> params = new HashMap();
        params.put("token", LoginUserProvider.getUser(BackPaymentPlanActivity.this).getToken());
        params.put("from", "3");
        OkUtil.post("GETNEWUSERINTERESTCOUNT", Urls.GETNEWUSERINTERESTCOUNT, params, BackPaymentPlanActivity.this, this);
    }


    //滑动
    @Override
    public void onCalendarChanged(DateTime dateTime) {
        currentYear = dateTime.getYear() + "";
        currentMonth = dateTime.getMonthOfYear() + "";

        //是不是可以查看上个月
        //判断年
        if (mYear == Integer.parseInt(currentYear)) {
            //判断月
            if (Integer.parseInt(currentMonth) > mMonth) {
                ncalendar.NotoLastPager(true);
                rl_left_date.setVisibility(View.VISIBLE);
                // return;
            } else if ((Integer.parseInt(currentMonth) == mMonth)) {
                ncalendar.NotoLastPager(false);
                rl_left_date.setVisibility(View.GONE);
            }
        } else {
            ncalendar.NotoLastPager(true);
            rl_left_date.setVisibility(View.VISIBLE);
        }


        if (currentMonth.length() < 2) {
            currentMonth = "0" + currentMonth;
        } else {
            currentMonth = currentMonth;
        }
        BigDecimal bd = new BigDecimal(getCurrentMonthMoney(paymentList) + "");
        tv_money_money.setText(bd.setScale(2, BigDecimal.ROUND_UP).doubleValue() + "");
        tv_month.setText(dateTime.getMonthOfYear() + "月");
        //tv_date.setText(dateTime.getYear() + "年" + dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日");
        setRightText(dateTime.getYear() + "", null);
        String clickDay = dateTime.getDayOfMonth() + "";
        String clickMonth = dateTime.getMonthOfYear() + "";
        if (clickMonth.length() < 2) {
            clickMonth = "0" + clickMonth;
        } else {
            clickMonth = clickMonth;
        }
        if (clickDay.length() < 2) {
            clickDay = "0" + clickDay;
        } else {
            clickDay = clickDay;
        }
        clickDate = dateTime.getYear() + "-" + clickMonth + "-" + clickDay;
        //			if(paymentList.size()>0){
        //				for(BeanDate date:paymentList){
        //					if(date.getDate().equals(clickDate)){
        //						recyclerView.setVisibility(View.VISIBLE);
        //						rl_title.setVisibility(View.VISIBLE);
        //						v_line.setVisibility(View.VISIBLE);
        //						tv_no_payment.setVisibility(View.GONE);
        ////						tv_today_money.setText(date.getMoney());
        ////						pageNo=1;
        ////						getData("3",LoginUserProvider.getUser(NewBackPaymentPlanActivity.this).getToken(),pageNo,pageSize,clickDate,REFRESH);
        ////						recyclerView.setMode(Mode.BOTH);
        //						break;
        //					}else{
        //						recyclerView.setVisibility(View.GONE);
        //						rl_title.setVisibility(View.GONE);
        //						v_line.setVisibility(View.GONE);
        //						tv_no_payment.setVisibility(View.VISIBLE);
        //						tv_today_money.setText("0.00");
        //					}
        //				}
        //		}
        rl_payment_plan_title.setVisibility(View.INVISIBLE);
        v_line.setVisibility(View.GONE);
        tv_no_payment.setVisibility(View.VISIBLE);
        RecyclerAdapter = null;
        mRecyclerView.removeAllViews();
        mRecyclerView.setVisibility(View.GONE);
        tv_today_money.setText("0.00");
        mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
        //Toast.makeText(this,"滑动"+dateTime.getYear() + "年" + dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日",Toast.LENGTH_SHORT).show();

    }

    //点击
    @Override
    public void onCalendarClickChanged(DateTime dateTime) {
        currentMonth = dateTime.getMonthOfYear() + "";
        if (currentMonth.length() < 2) {
            currentMonth = "0" + currentMonth;
        } else {
            currentMonth = currentMonth;
        }
        BigDecimal bd = new BigDecimal(getCurrentMonthMoney(paymentList) + "");
        //tv_money_money.setText(bd.setScale(2, BigDecimal.ROUND_UP).doubleValue()+"");
        tv_month.setText(dateTime.getMonthOfYear() + "月");
        //tv_date.setText(dateTime.getYear() + "年" + dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日");
        setRightText(dateTime.getYear() + "", null);
        String clickDay = dateTime.getDayOfMonth() + "";
        String clickMonth = dateTime.getMonthOfYear() + "";
        if (clickMonth.length() < 2) {
            clickMonth = "0" + clickMonth;
        } else {
            clickMonth = clickMonth;
        }
        if (clickDay.length() < 2) {
            clickDay = "0" + clickDay;
        } else {
            clickDay = clickDay;
        }
        clickDate = dateTime.getYear() + "-" + clickMonth + "-" + clickDay;
        if (paymentList.size() > 0) {
            for (BeanDate date : paymentList) {
                if (date.getDate().equals(clickDate)) {
                    RecyclerAdapter = null;
                    mRecyclerView.removeAllViews();
                    mSmartRefreshLayout.setNoMoreData(false);
                    CustomProgress.show(BackPaymentPlanActivity.this);
                    LogUtil.e("------------------------------------------222222222222222222222222222222222222");
                    mRecyclerView.setVisibility(View.VISIBLE);
                    rl_payment_plan_title.setVisibility(View.VISIBLE);
                    v_line.setVisibility(View.VISIBLE);
                    tv_no_payment.setVisibility(View.GONE);
                    tv_today_money.setText("0.00");
                    pageNo = 1;
                    plansList.clear();
                    getData("3", LoginUserProvider.getUser(BackPaymentPlanActivity.this).getToken(), pageNo, pageSize, clickDate);
                    break;
                } else {
                    RecyclerAdapter = null;
                    mRecyclerView.removeAllViews();
                    LogUtil.e("------------------------------------------11111111111111111111111111111111111");
                    mRecyclerView.setVisibility(View.GONE);
                    rl_payment_plan_title.setVisibility(View.INVISIBLE);
                    v_line.setVisibility(View.GONE);
                    tv_no_payment.setVisibility(View.VISIBLE);
                    tv_today_money.setText("0.00");
                    //plansList.clear();
                    mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                }
            }
        }
        //Toast.makeText(this,"点击"+dateTime.getYear() + "年" + dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日",Toast.LENGTH_SHORT).show();

    }

    /**
     * 获取当前个月本息
     *
     * @param paymentList
     * @return
     */
    public double getCurrentMonthMoney(List<BeanDate> paymentList) {
        double currentMonthMoney = 0;
        if (paymentList.size() > 0) {
            for (BeanDate date : paymentList) {
                String[] arr = date.getDate().split("-");
                if (arr[0].equals(currentYear) && arr[1].equals(currentMonth)) {
                    currentMonthMoney += Double.parseDouble(date.getMoney());
                }
            }
        }

        return currentMonthMoney;
    }


    public void setDate(View view) {
        ncalendar.setDate("2017-12-31");
    }

    public void toMonth(View view) {
        ncalendar.toMonth();
    }

    public void toWeek(View view) {
        ncalendar.toWeek();
    }

    public void toToday(View view) {
        ncalendar.toToday();
    }

    public void toNextPager(View view) {
        ncalendar.toNextPager();
    }

    public void toLastPager(View view) {
        ncalendar.toLastPager();
    }

    private void getData(String from, String token, final int pageNo, int pageSize, String nowDate) {
        HashMap<String, String> params = new HashMap();
        params.put("from", from);
        params.put("token", token);
        params.put("pageNo", pageNo + "");
        params.put("pageSize", pageSize + "");
        params.put("nowDate", nowDate);
        OkUtil.post("FINDNEWUSERREPAYPLANSTATISTICAL", Urls.FINDNEWUSERREPAYPLANSTATISTICAL, params, BackPaymentPlanActivity.this, this);
    }

    @Override
    protected void responseData(String stringId, String json) {
        switch (stringId) {
            case "GETNEWUSERINTERESTCOUNT":
                CustomProgress.CustomDismis();
                //BeanDate beanDate = GsonManager.fromJson(json, BeanDate.class);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    paymentList = new ArrayList<BeanDate>();
                    if (dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONArray array = dataArray.getJSONArray(i);
                            BeanDate entity = new BeanDate();
                            entity.setDate(array.getString(0));
                            entity.setMoney(array.getString(1));
                            paymentList.add(entity);
                        }
                        //calendarView.setPaymentList(paymentList);
                    }
                    BigDecimal bd = new BigDecimal(getCurrentMonthMoney(paymentList) + "");
                    tv_money_money.setText(bd.setScale(2, BigDecimal.ROUND_UP).doubleValue() + "");
                    if (Lists.notEmpty(paymentList)) {
                        for (BeanDate bean : paymentList) {
                            Common.dateList.add(bean.getDate());
                        }
                    }
                    ncalendar.post(new Runnable() {
                        @Override
                        public void run() {
                            ncalendar.setPoint(Common.dateList);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "FINDNEWUSERREPAYPLANSTATISTICAL":
                CustomProgress.CustomDismis();
                mSmartRefreshLayout.finishLoadMore();//结束加载
                PaymentEntity paymentEntity = GsonManager.fromJson(json, PaymentEntity.class);
                List<PaymentEntity.DataBean.PlansBean> plans = paymentEntity.getData().getPlans();
                if (paymentEntity.getData().getPageCount().equals(pageNo + "")) {//总页数
                    //mXRefreshView.setPullLoadEnable(false);
                    mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                }
                if (pageNo == 1) {
                    plansList = plans;
                } else if (pageNo > 1) {
                    plansList.addAll(plans);
                }
                //下面这个逻辑，跟请求返回没有任何关系，为了感觉是请求返回来的效果
                double money = 0;
                for (BeanDate plansBean : paymentList) {
                    if (plansBean.getDate().equals(clickDate)) {
                        money += Double.parseDouble(plansBean.getMoney());
                    }
                }
                BigDecimal b = new BigDecimal(money);
                String str = b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                tv_today_money.setText(str);
                RecyclerViewAdapter(plansList);
                break;
        }
    }

    /**
     * RecyclerViewAdapter
     */
    private BaseRecyclerAdapter RecyclerAdapter;

    private void RecyclerViewAdapter(final List<PaymentEntity.DataBean.PlansBean> plansBean) {
        if (RecyclerAdapter == null) {
            RecyclerAdapter = new BaseRecyclerAdapter<PaymentEntity.DataBean.PlansBean>(this, plansBean) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.activity_item_payment_list;
                }

                @Override
                public void bindData(RecyclerViewHolder holder, int position, PaymentEntity.DataBean.PlansBean item) {
                    holder.getTextView(R.id.tv_project_name).setText(item.getProjectName() + "\n" + "(" + item.getProjectSn() + ")");
                    holder.getTextView(R.id.tv_payment_money).setText(item.getNowRepayAmount());
                    holder.getTextView(R.id.tv_surplus_money).setText(item.getRemainingRepayAmount());
                }
            };
            mRecyclerView.setAdapter(RecyclerAdapter);
        } else {
            if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) {
                RecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        ++pageNo;
        getData("3", LoginUserProvider.getUser(BackPaymentPlanActivity.this).getToken(), pageNo, pageSize, clickDate);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}
