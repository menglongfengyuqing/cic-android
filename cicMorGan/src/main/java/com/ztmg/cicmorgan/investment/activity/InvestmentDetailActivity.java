//package com.ztmg.cicmorgan.investment.activity;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.WindowManager.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.d3rich.pulltorefresh.library.PullToRefreshBase;
//import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//import com.ztmg.cicmorgan.R;
//import com.ztmg.cicmorgan.account.activity.BankH5Activity;
//import com.ztmg.cicmorgan.account.activity.BindBankCardActivity;
//import com.ztmg.cicmorgan.account.activity.RechargeActivity;
//import com.ztmg.cicmorgan.activity.ExpandListView;
//import com.ztmg.cicmorgan.activity.FullScreenImageActivity;
//import com.ztmg.cicmorgan.activity.MainActivity;
//import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
//import com.ztmg.cicmorgan.base.BaseActivity;
//import com.ztmg.cicmorgan.entity.UserAccountInfo;
//import com.ztmg.cicmorgan.investment.adapter.FileGridViewAdapter;
//import com.ztmg.cicmorgan.investment.adapter.InvestmentListAdapter;
//import com.ztmg.cicmorgan.investment.entity.FileGridViewEntity;
//import com.ztmg.cicmorgan.investment.entity.ImgEntity;
//import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;
//import com.ztmg.cicmorgan.investment.entity.ValueVoucherEntity;
//import com.ztmg.cicmorgan.login.LoginActivity;
//import com.ztmg.cicmorgan.more.activity.SafetyActivity;
//import com.ztmg.cicmorgan.net.Urls;
//import com.ztmg.cicmorgan.test.TestQuestionFirstActivity;
//import com.ztmg.cicmorgan.util.DoCacheUtil;
//import com.ztmg.cicmorgan.util.LoginUserProvider;
//import com.ztmg.cicmorgan.util.SystemBarTintManager;
//import com.ztmg.cicmorgan.util.TimeUtil;
//import com.ztmg.cicmorgan.util.ToastUtils;
//import com.ztmg.cicmorgan.view.CustomProgress;
//import com.ztmg.cicmorgan.view.SlowlyProgressBar;
//
//import org.apache.http.Header;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///*
// * 投资详情
// */
//public class InvestmentDetailActivity extends BaseActivity implements OnClickListener, OnRefreshListener2<ListView> {
//    private String projectid;
//    private TextView tv_project_time, tv_once_money, tv_remaining_sum, tv_percent_num;
//    private String id;// 项目id
//    private String prostate;
//    private Button bt_once_investment;
//    private String minamount;// 起投金额
//    private String balanceamount;// 剩余可投金额
//    private String loandate;// 项目放款日期
//    private String rate;// 年化收益率
//    private String span;// 项目期限
//    private String isNewType;// 0-其他 1-新手
//    private String bidtotal;
//    private String name;
//    private String stepamount;// 浮动金额
//    private String amount;// 融资金额
//    private String isCanUseCoupon, isCanUsePlusCoupon;// 是否可以使用抵用券或者加息券
//    public static InvestmentDetailActivity mContext;
//    private int valueNum = 0;//优惠券的数量
//
//    private String loanData;
//    private TextView tv_product_introduction, tv_lend_record;
//    private View v_product_introduction_line, v_wind_control_line, v_lend_record_line;
//    private LinearLayout lend_list;
//    private ExpandListView lv_detail_investment_list;
//    private InvestmentListAdapter adapter;
//    private List<InvestmentListEntity> investmentList;
//    private List<InvestmentListEntity> investmentTotalList;
//    private final int REFRESH = 101;
//    private final int LOADMORE = 102;
//    private static int pageNo = 1;//当前页数
//    private static int pageSize = 500;//当前页面的内容数目
//    private String inputMoney;
//    private List<ValueVoucherEntity> valueList;// 优惠券集合；
//    private double inCome1;
//    private TextView tv_available_money;//弹框可用余额
//    private String pid;//优惠券id
//    private String value;//点击优惠券返回
//    private String date;//点击优惠券返回时间
//    private int valueCode = 101;
//    private EditText et_investment_money;
//    private TextView tv_voucher;
//    private TextView tv_voucher_money;
//    private boolean isSelect = false;
//    private Dialog isauthorizeDialog;
//    private Dialog investmentResult;
//    private NoLoginDialog noLoginDialog;
//    private LinearLayout ll_product_introduction_content, ll_wind_control_content;
//    private ImgEntity entity;
//    private List<ImgEntity> docimgsList, wgimgList, imgList;//借款资质照片，风控照片，项目照片
//    private TextView tv_product_contents, tv_wind_control_introduction;
//    private TextView tv_safe_company;
//    private InvestmentDialog investDialog;
//    private String creditInfoId;
//
//    private String path;//协议地址
//    private LinearLayout ll_question_list;
//    private ImageView iv_down_enter, iv_introduction_down_enter;
//    private boolean questionIsOpen = true;
//    private boolean introductionIsOpen = true;
//    private TextView tv_project_name, tv_loan_party, tv_payment_party, tv_project_amount, tv_project_span, tv_loan_rate, tv_payment_style, tv_interest_time;
//    private double doubleIncome;
//
//    private TextView tv_use_of_funds, tv_payment_source, tv_loan, tv_payment, tv_wind_control_measures;
//    private MyGridView gv_loan, gv_payment, gv_relevant_file;
//    private FileGridViewAdapter fileGridViewAdapter;
//    private LinearLayout ll_see_more;
//    private List<FileGridViewEntity> loanFileImgList;//借款方
//    private List<FileGridViewEntity> paymentFileImgList;//付款方
//    private List<FileGridViewEntity> relevantFileImgList;//相关文件
//    private String dialogAvailableAmount;//弹框可用余额
//    private String isToken = "1";
//    private List<ValueVoucherEntity> availableValueVoucheList;//可以使用的优惠券
//    private int totalCoupons;//输入金额可以使用多少钱优惠券
//    private double usableCoupons;//可使用优惠券集合的value和
//    private List<ValueVoucherEntity> usableList;//根据最大优惠金额选出的优惠券集合
//    private double returnUsableCoupons;//优惠券界面返回回来的优惠金额
//    private String name_str;
//    private StringBuilder sb;
//    private String inputMoneyNoSpot;
//    private String creditName;
//    private String creditUrl;
//
//    private Dialog investmentProcessdialog;//出借过程的弹框
//
//    private RelativeLayout rl_content;
//    private SlowlyProgressBar slowlyProgressBar;
//    int mindex;
//    int newProgress = 0;
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            mindex++;
//            if (mindex >= 5) {
//                newProgress += 10;
//                slowlyProgressBar.onProgressChange(newProgress);
//                mHandler.sendEmptyMessage(1);
//
//            } else {
//                newProgress += 5;
//                slowlyProgressBar.onProgressChange(newProgress);
//                mHandler.sendEmptyMessageDelayed(1, 1500);
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        super.setContentView(R.layout.activity_investment_detail);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(InvestmentDetailActivity.this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
//        }
//        mContext = this;
//        Intent intent = getIntent();
//        projectid = intent.getStringExtra("projectid");
//        loanData = intent.getStringExtra("loanDate");
//        noLoginDialog = new NoLoginDialog(InvestmentDetailActivity.this, R.style.MyDialogDeletAddress);
//        initView();
//        getData("3", projectid);
//    }
//
//    public static InvestmentDetailActivity getInstance() {
//        return mContext;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //判断是否登录
//
//        noLoginDialog.setCanceledOnTouchOutside(false);
//        noLoginDialog.setCancelable(false);
//        Window dialogWindow = noLoginDialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = LayoutParams.FILL_PARENT;
//        dialogWindow.setAttributes(lp);
//        //TODO dong 判断是否登录
//        if (LoginUserProvider.getUser(InvestmentDetailActivity.this) != null) {
//            DoCacheUtil util = DoCacheUtil.get(this);
//            String str = util.getAsString("isLogin");
//            if (str != null) {
//                if (str.equals("isLogin")) {//已登录
//                    //弹框消失
//                    noLoginDialog.dismiss();
//                } else {//未登录
//                    //弹框显示
//                    if (!noLoginDialog.isShowing()) {
//                        noLoginDialog.show();
//                    }
//                }
//            } else {
//                //弹框显示
//                if (!noLoginDialog.isShowing()) {
//                    noLoginDialog.show();
//                }
//            }
//        } else {
//            //弹框显示
//            if (!noLoginDialog.isShowing()) {
//                noLoginDialog.show();
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mHandler.removeMessages(1);
//    }
//
//    @Override
//    protected void initView() {
//        setTitle("");
//
//        setBack(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        tv_project_time = (TextView) findViewById(R.id.tv_project_time);// 期限
//        tv_once_money = (TextView) findViewById(R.id.tv_once_money);// 起投金额
//        tv_remaining_sum = (TextView) findViewById(R.id.tv_remaining_sum);// 可投金额
//        tv_percent_num = (TextView) findViewById(R.id.tv_percent_num);
//
//        bt_once_investment = (Button) findViewById(R.id.bt_once_investment);
//        bt_once_investment.setOnClickListener(this);// 立即投资
//
//        findViewById(R.id.ll_product_introduction).setOnClickListener(this);
//        tv_product_introduction = (TextView) findViewById(R.id.tv_product_introduction);
//        v_product_introduction_line = findViewById(R.id.v_product_introduction_line);
//        findViewById(R.id.ll_wind_control_introduction).setOnClickListener(this);//风控情况
//        tv_wind_control_introduction = (TextView) findViewById(R.id.tv_wind_control_introduction);
//        v_wind_control_line = findViewById(R.id.v_wind_control_line);
//        findViewById(R.id.ll_lend_record).setOnClickListener(this);
//        tv_lend_record = (TextView) findViewById(R.id.tv_lend_record);
//        v_lend_record_line = findViewById(R.id.v_lend_record_line);
//        //findViewById(R.id.ll_common_problem).setOnClickListener(this);
//        //tv_common_problem = (TextView) findViewById(R.id.tv_common_problem);
//        //v_common_problem_line = findViewById(R.id.v_common_problem_line);
//
//        ll_product_introduction_content = (LinearLayout) findViewById(R.id.ll_product_introduction_content);//产品介绍
//        ll_wind_control_content = (LinearLayout) findViewById(R.id.ll_wind_control_content);
//        lend_list = (LinearLayout) findViewById(R.id.lend_list);//借款记录
//
//        investmentTotalList = new ArrayList<InvestmentListEntity>();
//        lv_detail_investment_list = (ExpandListView) findViewById(R.id.lv_investment_detail_list);
//
//        //lv_detail_investment_list.setMode(Mode.BOTH);
//        //lv_detail_investment_list.setOnRefreshListener(this);
//        adapter = new InvestmentListAdapter(InvestmentDetailActivity.this, investmentTotalList);
//        lv_detail_investment_list.setAdapter(adapter);
//        findViewById(R.id.rl_safe_guarantee).setOnClickListener(this);
//        docimgsList = new ArrayList<ImgEntity>();
//        wgimgList = new ArrayList<ImgEntity>();
//        imgList = new ArrayList<ImgEntity>();
//        tv_product_contents = (TextView) findViewById(R.id.tv_product_contents);//项目介绍
//        tv_safe_company = (TextView) findViewById(R.id.tv_safe_company);
//
//        findViewById(R.id.tv_risk_tip_first).setOnClickListener(this);//网络借贷风险提示
//        findViewById(R.id.tv_risk_tip_second).setOnClickListener(this);//网络借贷平台禁止性行为
//        findViewById(R.id.rl_repayment_plan).setOnClickListener(this);
//        ll_question_list = (LinearLayout) findViewById(R.id.ll_question_list);//常见问题
//        iv_down_enter = (ImageView) findViewById(R.id.iv_down_enter);
//        iv_down_enter.setOnClickListener(this);//常见问题下拉
//        iv_introduction_down_enter = (ImageView) findViewById(R.id.iv_introduction_down_enter);
//        iv_introduction_down_enter.setOnClickListener(this);//项目简介下拉
//
//        tv_project_name = (TextView) findViewById(R.id.tv_project_name);//项目名称
//        tv_project_name.setOnClickListener(this);
//        tv_loan_party = (TextView) findViewById(R.id.tv_loan_party);//借款方
//        tv_payment_party = (TextView) findViewById(R.id.tv_payment_party);//付款方
//        tv_payment_party.setOnClickListener(this);
//        tv_project_amount = (TextView) findViewById(R.id.tv_project_amount);//项目金额
//        tv_project_span = (TextView) findViewById(R.id.tv_project_span);//项目期限
//        tv_loan_rate = (TextView) findViewById(R.id.tv_loan_rate);//预期出借利息
//        tv_payment_style = (TextView) findViewById(R.id.tv_payment_style);//还款方式
//        tv_interest_time = (TextView) findViewById(R.id.tv_interest_time);//起息时间
//        findViewById(R.id.tv_tip).setOnClickListener(this);
//
//        tv_use_of_funds = (TextView) findViewById(R.id.tv_use_of_funds);//资金用途
//        tv_payment_source = (TextView) findViewById(R.id.tv_payment_source);//还款来源
//        tv_loan = (TextView) findViewById(R.id.tv_loan);//借款方
//        tv_payment = (TextView) findViewById(R.id.tv_payment);//付款方
//        tv_payment.setOnClickListener(this);
//        tv_wind_control_measures = (TextView) findViewById(R.id.tv_wind_control_measures);//风控措施
//
//        //借款方
//        gv_loan = (MyGridView) findViewById(R.id.gv_loan);
//        gv_loan.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//                                    long arg3) {
//                Intent intent = new Intent(InvestmentDetailActivity.this, FullScreenImageActivity.class);
//                List<String> imageUrls = new ArrayList<String>();
//                for (int i = 0; i < loanFileImgList.size(); i++) {
//                    imageUrls.add(loanFileImgList.get(i).getUrl());
//                }
//                intent.putExtra("currentPosition", position);
//                intent.putStringArrayListExtra("urls", (ArrayList<String>) imageUrls);
//                intent.setClass(InvestmentDetailActivity.this, FullScreenImageActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //付款方
//        gv_payment = (MyGridView) findViewById(R.id.gv_payment);
//        gv_payment.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Intent intent = new Intent(InvestmentDetailActivity.this, FullScreenImageActivity.class);
//                List<String> imageUrls = new ArrayList<String>();
//                for (int i = 0; i < paymentFileImgList.size(); i++) {
//                    imageUrls.add(paymentFileImgList.get(i).getUrl());
//                }
//                intent.putExtra("currentPosition", position);
//                intent.putStringArrayListExtra("urls", (ArrayList<String>) imageUrls);
//                intent.setClass(InvestmentDetailActivity.this, FullScreenImageActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //相关文件
//        gv_relevant_file = (MyGridView) findViewById(R.id.gv_relevant_file);
//        gv_relevant_file.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//                                    long arg3) {
//                Intent intent = new Intent(InvestmentDetailActivity.this, FullScreenImageActivity.class);
//                List<String> imageUrls = new ArrayList<String>();
//                for (int i = 0; i < relevantFileImgList.size(); i++) {
//                    imageUrls.add(relevantFileImgList.get(i).getUrl());
//                }
//                intent.putExtra("currentPosition", position);
//                intent.putStringArrayListExtra("urls",
//                        (ArrayList<String>) imageUrls);
//                intent.setClass(InvestmentDetailActivity.this, FullScreenImageActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        ll_see_more = (LinearLayout) findViewById(R.id.ll_see_more);
//        ll_see_more.setOnClickListener(this);
//
//        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
//        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
//        slowlyProgressBar.onProgressStart();
//
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//    public void closeInvestmentDialog() {
//        if (investDialog != null && investDialog.isShowing()) {
//            investDialog.dismiss();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_product_introduction://产品介绍
//                tv_product_introduction.setTextColor(getResources().getColor(R.color.text_a11c3f));
//                tv_wind_control_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                tv_lend_record.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                //tv_common_problem.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                v_product_introduction_line.setVisibility(View.VISIBLE);
//                v_wind_control_line.setVisibility(View.GONE);
//                v_lend_record_line.setVisibility(View.GONE);
//                //v_common_problem_line.setVisibility(View.GONE);
//
//                ll_product_introduction_content.setVisibility(View.VISIBLE);
//                ll_wind_control_content.setVisibility(View.GONE);
//                lend_list.setVisibility(View.GONE);
//
//                break;
//
//            case R.id.ll_wind_control_introduction://风控情况
//
//                tv_product_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                tv_wind_control_introduction.setTextColor(getResources().getColor(R.color.text_a11c3f));
//                tv_lend_record.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                //			tv_common_problem.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                v_product_introduction_line.setVisibility(View.GONE);
//                v_wind_control_line.setVisibility(View.VISIBLE);
//                v_lend_record_line.setVisibility(View.GONE);
//                //			v_common_problem_line.setVisibility(View.GONE);
//
//                ll_product_introduction_content.setVisibility(View.GONE);
//                ll_wind_control_content.setVisibility(View.VISIBLE);
//                lend_list.setVisibility(View.GONE);
//                //获取相关文件
//                getPicDate2("2", creditInfoId);// 2-项目文件,1-贸易背景 ,3-风控文件
//                break;
//            case R.id.ll_lend_record://出借记录
//                tv_product_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                tv_wind_control_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                tv_lend_record.setTextColor(getResources().getColor(R.color.text_a11c3f));
//                //			tv_common_problem.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
//                v_product_introduction_line.setVisibility(View.GONE);
//                v_wind_control_line.setVisibility(View.GONE);
//                v_lend_record_line.setVisibility(View.VISIBLE);
//                //			v_common_problem_line.setVisibility(View.GONE);
//
//                ll_product_introduction_content.setVisibility(View.GONE);
//                ll_wind_control_content.setVisibility(View.GONE);
//                lend_list.setVisibility(View.VISIBLE);
//                pageNo = 1;
//                getListData("3", pageNo, pageSize, projectid, REFRESH);
//                //			lv_detail_investment_list.setMode(Mode.BOTH);
//                break;
//            //安全保障
//            case R.id.rl_safe_guarantee:
//                Intent intent = new Intent(InvestmentDetailActivity.this, SafetyActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.bt_once_investment:// 立即投资
//                if (LoginUserProvider.getUser(InvestmentDetailActivity.this) != null) {
//                    //if(LoginUserProvider.getUser(InvestmentDetailActivity.this).getIsTest().equals("0")){//未测试
//                    //	testDialog();
//                    //}else{
//                    valueNum = 0;
//                    investDialog = new InvestmentDialog(InvestmentDetailActivity.this, R.style.SelectPicDialog);
//                    Window dialogWindow = investDialog.getWindow();
//                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//                    lp.width = LayoutParams.FILL_PARENT;
//                    dialogWindow.setAttributes(lp);
//                    investDialog.show();
//                    //}
//                } else {// 未登录
//                    Intent onceIntent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                    onceIntent.putExtra("overtime", "5");
//                    startActivity(onceIntent);
//
//                }
//                break;
//            case R.id.iv_down_enter:
//                if (questionIsOpen) {
//                    ll_question_list.setVisibility(View.VISIBLE);
//                    questionIsOpen = false;
//                    iv_down_enter.setBackgroundResource(R.drawable.pic_up);
//                } else {
//                    ll_question_list.setVisibility(View.GONE);
//                    questionIsOpen = true;
//                    iv_down_enter.setBackgroundResource(R.drawable.pic_down);
//                }
//                break;
//            case R.id.rl_repayment_plan:
//                Intent repaymentIntent = new Intent(InvestmentDetailActivity.this,
//                        PaymentPlanActivity.class);
//                repaymentIntent.putExtra("projectid", projectid);
//                startActivity(repaymentIntent);
//                break;
//            case R.id.iv_introduction_down_enter:
//                if (introductionIsOpen) {
//                    tv_product_contents.setVisibility(View.VISIBLE);
//                    introductionIsOpen = false;
//                    iv_introduction_down_enter.setBackgroundResource(R.drawable.pic_up);
//                } else {
//                    tv_product_contents.setVisibility(View.GONE);
//                    introductionIsOpen = true;
//                    iv_introduction_down_enter.setBackgroundResource(R.drawable.pic_down);
//                }
//                break;
//            case R.id.tv_risk_tip_first:
//                Intent riskTipFirstIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
//                riskTipFirstIntent.putExtra("title", "风险提示书");
//                startActivity(riskTipFirstIntent);
//                break;
//            case R.id.tv_risk_tip_second:
//                Intent riskTipSecondIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                riskTipSecondIntent.putExtra("path", Urls.ZTPROTOCOLPROHIBIT);
//                riskTipSecondIntent.putExtra("title", "网络借贷平台禁止性行为提示书");
//                startActivity(riskTipSecondIntent);
//                break;
//            //		case R.id.tv_project_name:
//            case R.id.tv_payment_party:
//                if (!TextUtils.isEmpty(creditUrl) && !creditUrl.equals("null") && creditUrl != null) {
//                    Intent mthLogoIntent = new Intent(mContext, AgreementActivity.class);
//                    mthLogoIntent.putExtra("path", creditUrl);
//                    mthLogoIntent.putExtra("title", creditName);
//                    mContext.startActivity(mthLogoIntent);
//                }
//                break;
//            case R.id.tv_tip:
//                Intent tipIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                tipIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
//                tipIntent.putExtra("title", "风险提示书");
//                startActivity(tipIntent);
//                break;
//            case R.id.ll_see_more:
//                fileGridViewAdapter = new FileGridViewAdapter(mContext, relevantFileImgList);
//                gv_relevant_file.setAdapter(fileGridViewAdapter);
//                ll_see_more.setVisibility(View.GONE);
//                break;
//            case R.id.tv_payment:
//                if (!TextUtils.isEmpty(creditUrl) && !creditUrl.equals("null") && creditUrl != null) {
//                    Intent paymentIntent = new Intent(mContext, AgreementActivity.class);
//                    paymentIntent.putExtra("path", creditUrl);
//                    paymentIntent.putExtra("title", creditName);
//                    mContext.startActivity(paymentIntent);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    // 获取数据
//    private void getData(String from, String projectid) {
//        //		CustomProgress.show(this);
//        newProgress = 0;
//        mindex = 0;
//        slowlyProgressBar.setProgress(0);
//        slowlyProgressBar.onProgressStart();
//        mHandler.sendEmptyMessageDelayed(1, 1000);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.GETPROJECTINFO;
//        RequestParams params = new RequestParams();
//        params.put("from", from);
//        params.put("projectid", projectid);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                rl_content.setVisibility(View.VISIBLE);
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        if (LoginUserProvider.getUser(InvestmentDetailActivity.this) != null) {
//                            initAccountData(LoginUserProvider.getUser(InvestmentDetailActivity.this).getToken(), "3");
//                        } else {
//                            mindex = 5;
//                            mHandler.sendEmptyMessage(1);
//                        }
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
//                        if (dataObj.has("isCanUseCoupon")) {
//                            isCanUseCoupon = dataObj.getString("isCanUseCoupon");// 是否可用抵用券（0-是
//                            // 1-否）
//                        }
//                        if (dataObj.has("isCanUsePlusCoupon")) {
//                            isCanUsePlusCoupon = dataObj
//                                    .getString("isCanUsePlusCoupon");// 是否可用加息券（0-是
//                            // 1-否）
//                        }
//                        //						if(dataObj.has("loandate")){//截止投资时间
//                        //							loanData = dataObj.getString("loandate");
//                        //						}
//                        if (dataObj.has("loandate")) {
//                            loandate = dataObj.getString("loandate");// 项目放款日期
//                        }
//                        // TODO dong
//                        if (dataObj.has("proState")) {
//                            prostate = dataObj.getString("proState");// 是否可以投资
//                            if (prostate != null && prostate.equals("4")) {
//                                bt_once_investment.setText("立即出借");
//                                bt_once_investment.setBackgroundColor(InvestmentDetailActivity.this.getResources().getColor(R.color.text_a11c3f));
//                                bt_once_investment.setClickable(true);
//                                if (!TextUtils.isEmpty(loanData) && !loanData.equals("null")) {
//                                    boolean isBidders = TimeUtil.compareInverstmentListNowTime(loanData);
//                                    if (isBidders) {
//                                        bt_once_investment.setText("立即出借");
//                                        bt_once_investment.setBackgroundColor(InvestmentDetailActivity.this.getResources().getColor(R.color.text_a11c3f));
//                                        bt_once_investment.setClickable(true);
//                                    } else {
//                                        bt_once_investment.setText("已到期");
//                                        bt_once_investment
//                                                .setBackgroundColor(InvestmentDetailActivity.this
//                                                        .getResources().getColor(
//                                                                R.color.gray));
//                                        bt_once_investment.setClickable(false);
//                                    }
//                                } else {
//                                    loanData = loandate;
//                                    boolean isBidders = TimeUtil.compareNowTime(loanData);
//                                    if (isBidders) {
//                                        bt_once_investment.setText("立即出借");
//                                        bt_once_investment
//                                                .setBackgroundColor(InvestmentDetailActivity.this
//                                                        .getResources().getColor(
//                                                                R.color.text_a11c3f));
//                                        bt_once_investment.setClickable(true);
//                                    } else {
//                                        bt_once_investment.setText("已到期");
//                                        bt_once_investment
//                                                .setBackgroundColor(InvestmentDetailActivity.this
//                                                        .getResources().getColor(
//                                                                R.color.gray));
//                                        bt_once_investment.setClickable(false);
//                                    }
//
//                                }
//                            } else if (prostate != null && prostate.equals("3")) {
//                                bt_once_investment.setText("即将上线");
//                                bt_once_investment
//                                        .setBackgroundColor(InvestmentDetailActivity.this
//                                                .getResources().getColor(
//                                                        R.color.text_a11c3f));
//                                bt_once_investment.setClickable(false);
//                            } else if (prostate != null && prostate.equals("6")) {
//                                bt_once_investment.setText("还款中");
//                                bt_once_investment
//                                        .setBackgroundColor(InvestmentDetailActivity.this
//                                                .getResources().getColor(
//                                                        R.color.gray));
//                                bt_once_investment.setClickable(false);
//                            } else if (prostate != null && prostate.equals("7")) {
//                                bt_once_investment.setText("已还完");
//                                bt_once_investment
//                                        .setBackgroundColor(InvestmentDetailActivity.this
//                                                .getResources().getColor(
//                                                        R.color.gray));
//                                bt_once_investment.setClickable(false);
//                            } else if (prostate != null && prostate.equals("5")) {
//                                bt_once_investment.setText("还款中");
//                                bt_once_investment
//                                        .setBackgroundColor(InvestmentDetailActivity.this
//                                                .getResources().getColor(
//                                                        R.color.gray));
//                                bt_once_investment.setClickable(false);
//                            }
//                        }
//                        if (dataObj.has("stepamount")) {
//                            stepamount = dataObj.getString("stepamount");
//                        }
//                        if (dataObj.has("bidtotal")) {
//                            bidtotal = dataObj.getString("bidtotal");// 多少条投资记录
//                        }
//                        if (dataObj.has("id")) {
//                            id = dataObj.getString("id");
//                        }
//                        if (dataObj.has("proimg")) {
//                            JSONArray proimgArray = dataObj.getJSONArray("proimg");// 项目照片
//                            if (proimgArray.length() > 0) {
//                                for (int i = 0; i < proimgArray.length(); i++) {
//                                    entity = new ImgEntity();
//                                    String obj = proimgArray.getString(i);
//                                    entity.setImg(obj);
//                                    imgList.add(entity);
//                                }
//                            }
//                        }
//                        if (dataObj.has("briefinfo")) {
//                            String briefinfo = dataObj.getString("briefinfo");
//                        }
//                        if (dataObj.has("percentage")) {
//                            String percentage = dataObj.getString("percentage");
//                        }
//                        if (dataObj.has("minamount")) {
//                            minamount = dataObj.getString("minamount");// 起投金额
//                            String minamountDot = decimalFormat.format(Double.parseDouble(minamount));
//                            tv_once_money.setText(minamountDot + "元");// 起投金额
//                        }
//                        if (dataObj.has("maxamount")) {
//                        }
//                        if (dataObj.has("balanceamount")) {
//                            balanceamount = dataObj.getString("balanceamount");
//                            String balanceamountDot = decimalFormat.format(Double.parseDouble(balanceamount));
//                            tv_remaining_sum.setText(balanceamountDot + "元");// 剩余可投资金额
//                        }
//
//                        if (dataObj.has("guaranteescheme")) {// 风控措施
//                            tv_wind_control_measures.setText(dataObj.getString("guaranteescheme"));
//                        }
//                        if (dataObj.has("purpose")) {// 资金用途
//                            tv_use_of_funds.setText(dataObj.getString("purpose"));
//                        }
//                        if (dataObj.has("sourceOfRepayment")) {//还款来源
//
//                            if (TextUtils.isEmpty(dataObj.getString("sourceOfRepayment")) || dataObj.getString("sourceOfRepayment").equals("null")) {
//                                tv_payment_source.setText("");
//                            } else {
//                                tv_payment_source.setText(dataObj.getString("sourceOfRepayment"));
//                            }
//                        }
//                        if (dataObj.has("borrowerCompanyName")) {//借款方
//                            tv_loan.setText(dataObj.getString("borrowerCompanyName"));
//                        }
//                        if (dataObj.has("replaceRepayCompanyName")) {//付款方
//                            tv_payment.setText(dataObj.getString("replaceRepayCompanyName"));
//                        }
//
//                        //三证合一
//                        if (dataObj.has("businessLicenses")) {
//                            JSONArray businessLicensesArray = dataObj.getJSONArray("businessLicenses");
//                            loanFileImgList = new ArrayList<FileGridViewEntity>();
//                            if (businessLicensesArray.length() > 0) {
//                                for (int i = 0; i < businessLicensesArray.length(); i++) {
//                                    JSONObject obj = businessLicensesArray.getJSONObject(i);
//                                    FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                    fileGridViewEntity.setUrl(obj.getString("url"));
//                                    fileGridViewEntity.setName("三证合一");
//                                    loanFileImgList.add(fileGridViewEntity);
//                                }
//                            }
//                        }
//
//                        //开户许可证
//                        if (dataObj.has("bankPermitCerts")) {
//                            JSONArray bankPermitCertsArray = dataObj.getJSONArray("bankPermitCerts");
//                            if (bankPermitCertsArray.length() > 0) {
//                                for (int i = 0; i < bankPermitCertsArray.length(); i++) {
//                                    JSONObject obj = bankPermitCertsArray.getJSONObject(i);
//                                    FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                    fileGridViewEntity.setUrl(obj.getString("url"));
//                                    fileGridViewEntity.setName("开户许可证");
//                                    loanFileImgList.add(fileGridViewEntity);
//                                }
//                            }
//                            fileGridViewAdapter = new FileGridViewAdapter(InvestmentDetailActivity.this, loanFileImgList);
//                            gv_loan.setAdapter(fileGridViewAdapter);
//                        }
//
//                        //						//承诺函
//                        if (dataObj.has("commitments")) {
//                            JSONArray commitmentsArray = dataObj.getJSONArray("commitments");
//                            paymentFileImgList = new ArrayList<FileGridViewEntity>();
//                            if (commitmentsArray.length() > 0) {
//                                for (int i = 0; i < commitmentsArray.length(); i++) {
//                                    JSONObject obj = commitmentsArray.getJSONObject(i);
//                                    FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                    fileGridViewEntity.setUrl(obj.getString("url"));
//                                    fileGridViewEntity.setName("承诺函");
//                                    paymentFileImgList.add(fileGridViewEntity);
//                                }
//                                fileGridViewAdapter = new FileGridViewAdapter(InvestmentDetailActivity.this, paymentFileImgList);
//                                gv_payment.setAdapter(fileGridViewAdapter);
//                            }
//                        }
//
//
//                        if (dataObj.has("docimgs")) {
//                            JSONArray docimgsArray = dataObj
//                                    .getJSONArray("docimgs");// 借款资质图片信息
//                            if (docimgsArray.length() > 0) {
//                                for (int i = 0; i < docimgsArray.length(); i++) {
//                                    entity = new ImgEntity();
//                                    String obj = docimgsArray.getString(i);
//                                    entity.setImg(obj);
//                                    docimgsList.add(entity);
//                                }
//                            }
//                        }
//                        if (dataObj.has("countdowndate")) {
//                            String countdowndate = dataObj
//                                    .getString("countdowndate");
//                        }
//                        if (dataObj.has("wgcompany")) {// 担保公司
//                            String wgcompany = dataObj.getString("wgcompany");
//                            //							tv_safe_company.setText(wgcompany);
//                        }
//                        if (dataObj.has("amount")) {// 项目金额
//                            amount = dataObj.getString("amount");
//                        }
//                        if (dataObj.has("rate")) {// 年化收益
//                            rate = dataObj.getString("rate");
//                            tv_percent_num.setText(rate);
//                        }
//                        if (dataObj.has("repaytype")) {
//                        }
//                        if (dataObj.has("name")) {
//                            name = dataObj.getString("name");// 项目名字
//                            setTitle(name);
//                        }
//                        if (dataObj.has("currentamount")) {
//                            String currentamount = dataObj
//                                    .getString("currentamount");// 当前投资金额
//                        }
//                        if (dataObj.has("locus")) {// 项目所在地
//                        }
//                        if (dataObj.has("guaranteecase")) {// 担保情况
//                        }
//                        //						tv_profession.setText(dataObj.getString("industry"));// 所属行业
//                        if (dataObj.has("wgimglist")) {
//                            JSONArray wgimglistArray = dataObj
//                                    .getJSONArray("wgimglist");// 风控文件图片信息
//                            if (wgimglistArray.length() > 0) {
//                                for (int i = 0; i < wgimglistArray.length(); i++) {
//                                    entity = new ImgEntity();
//                                    String wgimglistObj = wgimglistArray
//                                            .getString(i);
//                                    entity.setImg(wgimglistObj);
//                                    wgimgList.add(entity);
//                                    //									wgimgList.add(wgimglistObj);
//                                }
//                            }
//                        }
//                        if (dataObj.has("bidtotal")) {
//                            String bidtotal = dataObj.getString("bidtotal");// 项目投资总条数
//                        }
//                        if (dataObj.has("projectcase")) {// 项目情况
//                            String projectcase = dataObj.getString("projectcase");
//                            tv_product_contents.setText(projectcase);
//                        }
//                        if (dataObj.has("span")) {
//                            span = dataObj.getString("span");
//                            tv_project_time.setText(span + "天");// 项目期限
//                        }
//                        if (dataObj.has("creditInfoId")) {
//                            creditInfoId = dataObj.getString("creditInfoId");//项目图片需要的id
//                        }
//                        if (dataObj.has("isNewType")) {
//                            isNewType = dataObj.getString("isNewType");// isNewType,2供应链
//                        }
//                        if (dataObj.has("creditName")) {
//                            creditName = dataObj.getString("creditName");
//                        }
//                        if (dataObj.has("creditUrl")) {
//                            creditUrl = dataObj.getString("creditUrl");
//                        }
//                        tv_project_name.setText(name + "(" + dataObj.getString("sn") + ")");//项目名称
//                        tv_loan_party.setText(dataObj.getString("borrowerCompanyName"));//借款方
//                        tv_payment_party.setText(dataObj.getString("replaceRepayCompanyName"));//付款方
//                        tv_project_amount.setText(decimalFormat.format(Double.parseDouble(dataObj.getString("amount"))) + "元");//项目金额
//                        tv_project_span.setText(dataObj.getString("span") + "天");//项目期限
//                        tv_loan_rate.setText(rate + "%");//预借年化利率
//                        tv_payment_style.setText(dataObj.getString("repaytype"));//还款方式
//                        tv_interest_time.setText(dataObj.getString("loandate"));//起息时间
//
//                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
//                        CustomProgress.CustomDismis();
//                        String mGesture = LoginUserProvider.getUser(
//                                InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("")
//                                && mGesture != null) {// 判断是否设置手势密码
//                            // 设置手势密码
//                            Intent intent = new Intent(
//                                    InvestmentDetailActivity.this,
//                                    UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            // 未设置手势密码
//                            Intent intent = new Intent(
//                                    InvestmentDetailActivity.this,
//                                    LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil
//                                .get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        CustomProgress.CustomDismis();
//                        Toast.makeText(InvestmentDetailActivity.this,
//                                jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    CustomProgress.CustomDismis();
//                    e.printStackTrace();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0)
//                            .show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode,
//                                  org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0)
//                        .show();
//                CustomProgress.CustomDismis();
//            }
//        });
//    }
//
//    //投资弹框
//    public class InvestmentDialog extends Dialog {
//        Context context;
//
//        public InvestmentDialog(Context context) {
//            super(context);
//            this.context = context;
//        }
//
//        public InvestmentDialog(Context context, int theme) {
//            super(context, theme);
//            this.context = context;
//            this.setContentView(R.layout.dialog_once_investment);
//
//            ImageView iv_close = (ImageView) findViewById(R.id.iv_close);//关闭
//            TextView tv_agreement = (TextView) findViewById(R.id.tv_agreement);//协议
//            TextView tv_risk_hint = (TextView) findViewById(R.id.tv_risk_hint);//风险协议
//            TextView tv_sex_hint = (TextView) findViewById(R.id.tv_sex_hint);//禁止性行为协议
//            TextView tv_electronic_signature = (TextView) findViewById(R.id.tv_electronic_signature);//电子签章证书
//
//            et_investment_money = (EditText) findViewById(R.id.et_investment_money);//输入投资金额
//            TextView tv_all_money = (TextView) findViewById(R.id.tv_all_money);//全投
//            final TextView tv_rate = (TextView) findViewById(R.id.tv_rate);//预期收益
//            tv_available_money = (TextView) findViewById(R.id.tv_available_money);//可用余额
//            Button bt_recharge = (Button) findViewById(R.id.bt_recharge);//充值
//            LinearLayout ll_voucher = (LinearLayout) findViewById(R.id.ll_voucher);
//            Button bt_investment = (Button) findViewById(R.id.bt_investment);//确定
//            initAccountData(LoginUserProvider.getUser(InvestmentDetailActivity.this).getToken(), "3");
//            //			DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//            //			if(!TextUtils.isEmpty(dialogAvailableAmount)){
//            //				String availableAmountDot = decimalFormat.format(Double.parseDouble(dialogAvailableAmount));
//            //				tv_available_money.setText(availableAmountDot);//可用余额
//            //			}
//            getValueVoucher(LoginUserProvider.getUser(InvestmentDetailActivity.this).getToken(), "3", "1", projectid);
//            if (isCanUseCoupon.equals("1")) {//是否可用抵用券（0-是   1-否）,是否可用加息券（0-是   1-否）
//                ll_voucher.setVisibility(View.GONE);
//            } else {
//                ll_voucher.setVisibility(View.VISIBLE);
//            }
//            tv_voucher = (TextView) findViewById(R.id.tv_voucher);//可用抵用券
//            tv_voucher_money = (TextView) findViewById(R.id.tv_voucher_money);//可用抵用券抵扣金额
//            final ImageView iv_check_agreement = (ImageView) findViewById(R.id.iv_check_agreement);//同意协议
//            et_investment_money.setHint(minamount + "元起，及100的整数倍");
//            et_investment_money.addTextChangedListener(new TextWatcher() {
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    //					et_investment_money.setCursorVisible(true);
//                }
//
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count,
//                                              int after) {
//
//                }
//
//                private double inputMoneyDou;
//                private double inputMoneySecond;
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    availableValueVoucheList = new ArrayList<ValueVoucherEntity>();
//                    pid = "";
//                    value = "";
//                    inputMoney = s.toString();
//                    if (inputMoney != null && !inputMoney.equals("")) {
//                        inputMoneyDou = Double.parseDouble(inputMoney);//输入的金额double
//                        if (inputMoneySecond > 0 && inputMoneySecond != inputMoneyDou) {
//                            valueNum = 0;
//                            usableCoupons = 0;
//                            for (ValueVoucherEntity entity : valueList) {
//                                String limitAmoun = entity.getLimitAmount();
//                                double limitAmounInt = Double.parseDouble(limitAmoun);//优惠券的起投金额
//                                double minamountDou = Double.parseDouble(minamount);//起投金额
//                                if (inputMoneyDou >= limitAmounInt && inputMoneyDou >= minamountDou) {
//
//                                    inputMoneySecond = inputMoneyDou;
//                                    valueNum = valueNum + 1;
//                                    //可以使用的优惠券筛选出来的集合
//                                    availableValueVoucheList.add(entity);
//                                }
//                            }
//                            if (inputMoney.contains(".")) {
//                                inputMoneyNoSpot = inputMoney.substring(0, inputMoney.indexOf(".")).toString();
//                            } else {
//                                inputMoneyNoSpot = inputMoney;
//                            }
//                            //最终可用优惠券的金额
//                            totalCoupons = (int) Double.parseDouble(inputMoneyNoSpot) / 1000 * 10;
//                            //							totalCoupons = Integer.parseInt(inputMoneyNoSpot)/1000*10;
//                            //							usableCoupons = totalCoupons;//使用可用优惠券的金额
//                            usableList = new ArrayList<ValueVoucherEntity>();
//                            for (int i = 0; i < availableValueVoucheList.size(); i++) {
//                                int value = Integer.parseInt(availableValueVoucheList.get(i).getValue().substring(0, availableValueVoucheList.get(i).getValue().indexOf(".")).toString());
//                                if (totalCoupons >= value) {
//                                    usableList.add(availableValueVoucheList.get(i));
//                                    totalCoupons -= value;
//                                    if (totalCoupons == 0) {
//                                        break;
//                                    }
//                                }
//                            }
//                            if (usableList.size() > 0) {
//                                for (int j = 0; j < usableList.size(); j++) {
//                                    usableCoupons += Double.parseDouble(usableList.get(j).getValue());
//                                }
//                            }
//                        } else {
//                            if (valueList != null) {
//                                usableCoupons = 0;
//                                for (ValueVoucherEntity entity : valueList) {
//                                    String limitAmoun = entity.getLimitAmount();
//                                    double limitAmounInt = Double.parseDouble(limitAmoun);//优惠券的起投金额
//                                    double minamountDou = Double.parseDouble(minamount);//起投金额
//                                    if (inputMoneyDou >= limitAmounInt && inputMoneyDou >= minamountDou) {
//                                        inputMoneySecond = inputMoneyDou;
//                                        valueNum = valueNum + 1;
//                                        //可以使用的优惠券筛选出来的集合
//                                        availableValueVoucheList.add(entity);
//
//                                    }
//                                }
//                                if (inputMoney.contains(".")) {
//                                    inputMoneyNoSpot = inputMoney.substring(0, inputMoney.indexOf(".")).toString();
//                                } else {
//                                    inputMoneyNoSpot = inputMoney;
//                                }
//                                totalCoupons = Integer.parseInt(inputMoneyNoSpot) / 1000 * 10;//使用优惠券的总金额
//                                //								usableCoupons = totalCoupons;//使用可用优惠券的金额
//                                usableList = new ArrayList<ValueVoucherEntity>();
//                                for (int i = 0; i < availableValueVoucheList.size(); i++) {
//                                    int value = Integer.parseInt(availableValueVoucheList.get(i).getValue().substring(0, availableValueVoucheList.get(i).getValue().indexOf(".")).toString());
//                                    if (totalCoupons >= value) {
//                                        usableList.add(availableValueVoucheList.get(i));
//                                        totalCoupons -= value;
//                                        if (totalCoupons == 0) {
//                                            break;
//                                        }
//                                    }
//                                }
//                                if (usableList.size() > 0) {
//                                    for (int j = 0; j < usableList.size(); j++) {
//                                        usableCoupons += Double.parseDouble(usableList.get(j).getValue());
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        valueNum = 0;
//                    }
//                    if (valueNum == 0) {
//                        tv_voucher.setText("您有" + valueNum + "张优惠券可用");
//                        tv_voucher_money.setText("抵扣支付金额" + usableCoupons + "元");
//                    } else {
//                        tv_voucher.setText("已选择" + usableList.size() + "张(最优方案)");
//                        tv_voucher_money.setText("抵扣支付金额" + usableCoupons + "元");
//                        //						tv_voucher.setText("您有"+valueNum+"张优惠券可用");
//                    }
//
//                    double onceInvestMoney = Double.parseDouble(minamount);//起投金额
//                    if (!TextUtils.isEmpty(inputMoney)) {
//                        if (inputMoney != null && !inputMoney.equals("") && inputMoneyDou >= onceInvestMoney) {
//                            //						rl_pop.setVisibility(View.GONE);
//                            //(投资金额*年化收益率)*(项目期限+投资日期至放款日期之间的天数))/(365*100)\,改版之前的算法
//                            double rateMoney = Double.parseDouble(rate);
//                            double projectDate = Double.parseDouble(span);
//                            //loandate
//                            //							String time = "2016-06-17";
//                            //预期收益=出借金额*预期出借利率/365*出借期限
//                            double days = Double.parseDouble(getDays(loandate) + "");
//                            //							inCome1 = ((inputMoneyDou*rateMoney)*(projectDate+days))/(365*100);
//                            doubleIncome = inputMoneyDou * rateMoney / 100 / 365;
//                            BigDecimal b = new BigDecimal(doubleIncome);
//                            String strIncom = b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
//                            doubleIncome = Double.parseDouble(strIncom);
//                            inCome1 = doubleIncome * projectDate;
//                            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//                            String distanceString = decimalFormat.format(inCome1);//format 返回的是字符串
//                            tv_rate.setText(distanceString);
//                        } else {
//                            tv_rate.setText("");
//                        }
//                    } else {
//                        tv_rate.setText("");
//                    }
//                }
//            });
//
//            iv_close.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                }
//            });
//            tv_agreement.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent agreeageIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                    //					path = "http://cicmorgan.com/invest_mine_agreement_scf.html";
//                    agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENTSCF);
//                    startActivity(agreeageIntent);
//                }
//            });
//            tv_risk_hint.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent riskHintFirstIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                    riskHintFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
//                    riskHintFirstIntent.putExtra("title", "风险提示书");
//                    startActivity(riskHintFirstIntent);
//                }
//            });
//            tv_sex_hint.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent sexHintIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                    sexHintIntent.putExtra("path", Urls.ZTPROTOCOLPROHIBIT);
//                    sexHintIntent.putExtra("title", "网络借贷平台禁止性行为提示书");
//                    startActivity(sexHintIntent);
//                }
//            });
//            tv_electronic_signature.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    //电子签章
//                    Intent electronicSignatureIntent = new Intent(InvestmentDetailActivity.this, AgreementActivity.class);
//                    electronicSignatureIntent.putExtra("path", Urls.ZTELECTRONICSIGNATURE);
//                    electronicSignatureIntent.putExtra("title", "电子签章证书");
//                    startActivity(electronicSignatureIntent);
//                }
//            });
//            ll_voucher.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    if (valueNum != 0) {
//                        Intent mIntent = new Intent(InvestmentDetailActivity.this, ValueVoucherActivity.class);
//                        mIntent.putExtra("inputmoney", inputMoney);
//                        mIntent.putExtra("usableList", (Serializable) usableList);
//                        mIntent.putExtra("usableCoupons", usableCoupons);
//                        mIntent.putExtra("projectid", projectid);
//
//                        startActivityForResult(mIntent, valueCode);
//                    }
//                }
//            });
//            tv_all_money.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    //起投金额，可投金额，账户余额
//                    //					usableCoupons = 0;
//                    double minamountDou = Double.parseDouble(minamount);//起投金额
//                    double balanceamountDou = Double.parseDouble(balanceamount);//可投余额
//                    String mStepamount = stepamount.substring(0, stepamount.indexOf(".")).toString();//浮动金额
//                    int stepamountInt = Integer.valueOf(mStepamount);
//                    String availableMoney = tv_available_money.getText().toString().trim();
//                    double availableAmountDot = Double.parseDouble(availableMoney);//账户余额
//                    int availableAmountInt = Integer.valueOf(availableMoney.substring(0, availableMoney.indexOf(".")).toString());
//                    if (balanceamountDou >= minamountDou) {//可投余额大于起投金额
//                        if (availableAmountDot >= minamountDou) {//账户余额大于等于起投金额
//                            if (availableAmountDot > balanceamountDou) {//账户余额大于等于可投金额
//                                et_investment_money.setText(balanceamountDou + "");
//                                //								et_investment_money.setCursorVisible(false);
//                                et_investment_money.setSelection(et_investment_money.getText().length());
//                            } else {
//                                int value = availableAmountInt - availableAmountInt % stepamountInt;
//                                et_investment_money.setText(value + "");
//                                et_investment_money.setSelection(et_investment_money.getText().length());
//                                //								et_investment_money.setCursorVisible(false);
//                            }
//                        } else {
//
//                            ToastUtils.show(InvestmentDetailActivity.this, "您的账户余额不足");
//                        }
//
//                    } else {//可投金额小于起投金额
//                        if (availableAmountDot >= balanceamountDou) {//账户余额大于等于可投金额
//                            et_investment_money.setText(balanceamountDou + "");
//                            et_investment_money.setSelection(et_investment_money.getText().length());
//                            //							et_investment_money.setCursorVisible(false);
//                        } else {
//                            ToastUtils.show(InvestmentDetailActivity.this, "您的账户余额不足");
//                        }
//                    }
//                }
//            });
//            bt_recharge.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    String isBindBank = LoginUserProvider.getUser(InvestmentDetailActivity.this).getIsBindBank();
//                    if (isBindBank.equals("1")) {//未绑卡
//                        dialog();//1充值
//                    } else if (isBindBank.equals("2")) {//已绑定,充值
//                        Intent rIntent = new Intent(InvestmentDetailActivity.this, RechargeActivity.class);
//                        startActivity(rIntent);
//                    }
//                    dismiss();
//                }
//            });
//            iv_check_agreement.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    if (isSelect) {
//                        iv_check_agreement.setBackgroundResource(R.drawable.pic_riskevaluation_checkbox_normal);
//                        isSelect = false;
//                    } else {
//                        iv_check_agreement.setBackgroundResource(R.drawable.pic_riskevaluation_checkbox_seleceed);
//                        isSelect = true;
//                    }
//                }
//            });
//            bt_investment.setOnClickListener(new View.OnClickListener() {
//
//                private String inputMoneyStr;
//
//                @Override
//                public void onClick(View v) {
//                    String isBindBank = LoginUserProvider.getUser(InvestmentDetailActivity.this).getIsBindBank();
//                    if (isBindBank.equals("1")) {//未绑卡
//                        dismiss();
//                        dialog();
//                    } else if (isBindBank.equals("2")) {
//                        if (LoginUserProvider.getUser(InvestmentDetailActivity.this).getIsTest().equals("0")) {//未测试
//                            dismiss();
//                            testDialog();
//                        } else {
//                            String minamountInt = minamount.substring(0, minamount.indexOf(".")).toString();//起投金额
//                            String balanceamountInt = balanceamount.substring(0, balanceamount.indexOf(".")).toString();//可投余额
//                            String mStepamount = stepamount.substring(0, stepamount.indexOf(".")).toString();//浮动金额
//                            //立即投资
//                            String investmentMoney = et_investment_money.getText().toString();
//                            int stepamountInt = Integer.parseInt(mStepamount);
//                            if (!TextUtils.isEmpty(investmentMoney)) {
//                                //if(!inputMoney.equals(tv_available_money.getText().toString().trim())){
//                                if (balanceamountInt.compareTo(minamountInt) > 0) {//可投余额大于起投金额
//                                    //					double availableAmountDou = Double.parseDouble(availableAmount);//账户余额
//                                    double availableAmountDou = Double.parseDouble(tv_available_money.getText().toString().trim());
//                                    //					if(inputMoney.compareTo(availableAmount)>0){//投资金额小于可用余额
//                                    double inputMoneyDou = Double.parseDouble(inputMoney);
//                                    if (inputMoneyDou > availableAmountDou) {//投资金额小于可用余额
//                                        Toast.makeText(InvestmentDetailActivity.this, "您的账户余额不足！", 0).show();
//                                        return;
//                                    }
//                                }
//                                if (inputMoney.contains(".")) {
//                                    inputMoneyStr = inputMoney.substring(0, inputMoney.indexOf(".")).toString();
//                                } else {
//                                    inputMoneyStr = inputMoney;
//                                }
//                                if (balanceamountInt.compareTo(minamountInt) > 0) {//可投余额大于起投金额
//                                    if (!inputMoneyStr.equals(balanceamountInt)) {//输入金额与可投金额不相等时，必须是递增金额的倍数，输入金额与可投金额相等时，不是递增金额的倍数也可以通过
//                                        if ((Integer.valueOf(inputMoney) - Integer.valueOf(minamountInt)) % stepamountInt != 0) {
//                                            //投资金额须为起投金额与递增金额（1000）整数倍之和
//                                            Toast.makeText(InvestmentDetailActivity.this, "出借金额须为出借最低金额与递增金额（" + stepamountInt + "）整数倍之和", 0).show();
//                                            return;
//                                        }
//                                    }
//                                }
//
//                                if (balanceamountInt.compareTo(minamountInt) > 0) {//可投余额大于起投金额
//                                    if (inputMoney.compareTo(minamountInt) < 0) {
//                                        Toast.makeText(InvestmentDetailActivity.this, "请大于出借最低金额" + minamount, 0).show();
//                                        return;
//                                    }
//                                }
//
//                                if (balanceamountInt.compareTo(minamountInt) < 0) {//可投余额小于起投金额
//                                    if (inputMoney != balanceamountInt) {
//                                        Toast.makeText(InvestmentDetailActivity.this, "可投金额小于出借最低金额的时候需要一次性投完", 0).show();
//                                        return;
//                                    }
//                                }
//                                //}
//                                if (!isSelect) {
//                                    ToastUtils.show(InvestmentDetailActivity.this, "请阅读同意协议后再出借");
//                                    return;
//                                }
//                                //								String isBindBank = LoginUserProvider.getUser(InvestmentDetailActivity.this).getIsBindBank();
//
//                                if (isCanUseCoupon.equals("1")) {//是否可用抵用券（0-是   1-否）,是否可用加息券（0-是   1-否）
//                                    usableList.clear();
//                                }
//                                newOnceInvestment(LoginUserProvider.getUser(InvestmentDetailActivity.this).getToken(), investmentMoney, projectid, usableList, "3");
//                            } else {
//                                Toast.makeText(InvestmentDetailActivity.this, "请输入出借金额", 0).show();
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    //计算两个时间之间的天数
//    public long getDays(String time) {
//        long currentTime = System.currentTimeMillis();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        try {
//            date = sdf.parse(time);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long beforeTime = date.getTime();
//        long days;
//        if (currentTime == beforeTime) {
//            days = (beforeTime - currentTime) / 1000 / 3600 / 24;
//        } else {
//            days = (long) (((beforeTime - currentTime) / 1000 / 3600 / 24) + 1.0);
//        }
//        //		long days=(beforeTime-currentTime)/1000/ 3600 / 24;
//        return days;
//    }
//
//    //获取优惠券
//    private void getValueVoucher(String token, String from, String state, String projectid) {
//        CustomProgress.show(this);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.GETUSERAWARDSHISTORYLIST;
//        RequestParams params = new RequestParams();
//        params.put("token", token);
//        params.put("from", from);
//        params.put("state", state);
//        params.put("projectId", projectid);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                CustomProgress.CustomDismis();
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        JSONArray dataArr = dataObj.getJSONArray("awardsList");
//                        valueList = new ArrayList<ValueVoucherEntity>();
//                        if (dataArr.length() > 0) {
//                            ValueVoucherEntity valueEntity;
//                            for (int i = 0; i < dataArr.length(); i++) {
//                                JSONObject valueObj = dataArr.getJSONObject(i);
//                                String limitAmount = valueObj.getString("limitAmount");
//
//                                valueEntity = new ValueVoucherEntity();
//                                valueEntity.setId(valueObj.getString("id"));
//                                valueEntity.setOverdueDate(valueObj.getString("overdueDate"));
//                                valueEntity.setGetDate(valueObj.getString("getDate"));
//                                valueEntity.setLimitAmount(limitAmount);
//                                valueEntity.setValue(valueObj.getString("value"));
//                                valueEntity.setType(valueObj.getString("type"));//1抵用券2加息券
//                                valueEntity.setState(valueObj.getString("state"));
//                                if (valueObj.has("spans")) {
//                                    valueEntity.setSpans(valueObj.getString("spans"));//什么期限可以使用
//                                }
//                                valueList.add(valueEntity);
//
//                            }
//                        }
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        if (investDialog.isShowing()) {
//                            investDialog.dismiss();
//                        }
//
//                        // TODO dong 判断token超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "11");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "11");
//                            startActivity(intent);
//                        }
//                        //						LoginUserProvider.cleanData(OnceInvestmentActivity.this);
//                        //						LoginUserProvider.cleanDetailData(OnceInvestmentActivity.this);
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        if (investDialog.isShowing()) {
//                            investDialog.dismiss();
//                        }
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//
//                } catch (JSONException e) {
//                    if (investDialog.isShowing()) {
//                        investDialog.dismiss();
//                    }
//                    e.printStackTrace();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                if (investDialog.isShowing()) {
//                    investDialog.dismiss();
//                }
//                CustomProgress.CustomDismis();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//
//    //TODO dong 验证token有没有失效
//    //获取可用余额
//    private void initAccountData(final String token, String from) {
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        //		String url = Urls.GETUSERACCOUNT;
//        String url = Urls.GETCGBUSERACCOUNT;
//        RequestParams params = new RequestParams();
//        params.put("token", token);
//        params.put("from", from);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                //				CustomProgress.CustomDismis();
//                mindex = 5;
//                mHandler.sendEmptyMessage(1);
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        UserAccountInfo info = new UserAccountInfo();
//                        info.setTotalAmount(dataObj.getString("totalAmount"));
//                        BigDecimal bd = new BigDecimal(dataObj.getString("availableAmount"));
//                        dialogAvailableAmount = bd.toPlainString();
//                        info.setAvailableAmount(dialogAvailableAmount);
//                        info.setCashAmount(dataObj.getString("cashAmount"));
//                        info.setRechargeAmount(dataObj.getString("rechargeAmount"));
//                        info.setFreezeAmount(dataObj.getString("freezeAmount"));
//                        info.setTotalInterest(dataObj.getString("totalInterest"));
//                        info.setCurrentAmount(dataObj.getString("currentAmount"));
//                        info.setRegularDuePrincipal(dataObj.getString("regularDuePrincipal"));
//                        info.setRegularDueInterest(dataObj.getString("regularDueInterest"));
//                        info.setRegularTotalAmount(dataObj.getString("regularTotalAmount"));
//                        info.setRegularTotalInterest(dataObj.getString("regularTotalInterest"));
//                        info.setCurrentTotalInterest(dataObj.getString("currentTotalInterest"));
//                        info.setCurrentYesterdayInterest(dataObj.getString("currentYesterdayInterest"));
//                        info.setReguarYesterdayInterest(dataObj.getString("reguarYesterdayInterest"));
//
//                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//                        String availableAmountDot = decimalFormat.format(Double.parseDouble(dialogAvailableAmount));
//                        if (tv_available_money != null) {
//                            tv_available_money.setText(availableAmountDot);//可用余额
//                        }
//
//                        LoginUserProvider.setUserDetail(info);
//
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        isToken = "0";
//                        noLoginDialog = new NoLoginDialog(InvestmentDetailActivity.this, R.style.MyDialogDeletAddress);
//
//                        noLoginDialog.setCanceledOnTouchOutside(false);
//                        noLoginDialog.setCancelable(false);
//                        Window dialogWindow = noLoginDialog.getWindow();
//                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//                        lp.width = LayoutParams.FILL_PARENT;
//                        dialogWindow.setAttributes(lp);
//                        //弹框显示
//                        if (!noLoginDialog.isShowing()) {
//                            noLoginDialog.show();
//                        }
//                    } else {
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                CustomProgress.CustomDismis();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == valueCode) {
//            //20.00元（2016-04-09到期）
//            //			value = data.getStringExtra("value");
//            //			date = data.getStringExtra("date");
//            //			pid = data.getStringExtra("pid");
//            //			String investmentMoney = et_investment_money.getText().toString();
//            //			double investmentDou = Double.parseDouble(investmentMoney);
//            //			double onceInvestMoney = Double.parseDouble(minamount);//起投金额
//            //			if(!TextUtils.isEmpty(value) && !TextUtils.isEmpty(date)){
//            //				tv_voucher.setText(value+"元"+"("+date(date)+"到期"+")");
//            //				double valueDou = Double.parseDouble(value);
//            //				double money = investmentDou-valueDou;
//            //			}
//            returnUsableCoupons = 0;
//            List<ValueVoucherEntity> valueVoucherList = (List<ValueVoucherEntity>) data.getSerializableExtra("valueVoucherList");
//            usableList.clear();
//            if (valueVoucherList != null && valueVoucherList.size() > 0) {
//                for (int i = 0; i < valueVoucherList.size(); i++) {
//                    if (valueVoucherList.get(i).isCheck()) {
//                        returnUsableCoupons += Double.parseDouble(valueVoucherList.get(i).getValue());
//                        ValueVoucherEntity entity = new ValueVoucherEntity();
//                        entity.setId(valueVoucherList.get(i).getId());
//                        entity.setCheck(valueVoucherList.get(i).isCheck());
//                        entity.setValue(valueVoucherList.get(i).getValue());
//                        entity.setGetDate(valueVoucherList.get(i).getGetDate());
//                        entity.setLimitAmount(valueVoucherList.get(i).getLimitAmount());
//                        entity.setOverdueDate(valueVoucherList.get(i).getOverdueDate());
//                        entity.setState(valueVoucherList.get(i).getState());
//                        entity.setType(valueVoucherList.get(i).getType());
//                        usableList.add(entity);
//                    }
//                }
//                tv_voucher.setText("已选择" + usableList.size() + "张");
//                tv_voucher_money.setText("抵扣支付金额" + returnUsableCoupons + "元");
//            } else {
//                tv_voucher.setText("已选择0张");
//                tv_voucher_money.setText("抵扣支付金额0元");
//            }
//        }
//    }
//
//    public String date(String str) {
//        //		String str = "weicc-20100107-00001";
//        str = str.substring(0, str.length() - 9);
//        return str;
//    }
//
//    // 没有绑定银行卡提示框
//    private void dialog() {
//
//        final Dialog mdialog = new Dialog(this, R.style.MyDialog);
//        mdialog.setContentView(R.layout.dl_isbindbank);
//
//        TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
//        TextView tv_no = (TextView) mdialog.findViewById(R.id.tv_no);
//        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
//        tv_yes.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //跳转到绑定银行卡界面
//                Intent intent = new Intent(InvestmentDetailActivity.this, BindBankCardActivity.class);
//                intent.putExtra("isBackAccount", "0");
//                startActivity(intent);
//                mdialog.dismiss();
//            }
//        });
//        tv_no.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //不绑定银行卡，关闭弹框
//                mdialog.dismiss();
//            }
//        });
//        iv_dialog_close.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mdialog.dismiss();
//            }
//        });
//        mdialog.show();
//    }
//
//    //立即投资旧接口
//    private void onceInvestment(String token, final String amount, String projectId, List<ValueVoucherEntity> idList, String from) {
//        CustomProgress.show(this);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.NEWINVESTSAVEUSERINVEST;
//        RequestParams params = new RequestParams();
//        params.put("token", token);
//        params.put("amount", amount);
//        params.put("projectId", projectId);
//        params.put("from", from);
//        sb = new StringBuilder();
//        if (idList.size() > 0) {
//            List<String> a = new ArrayList<String>();
//            for (int i = 0; i < idList.size(); i++) {
//                a.add(idList.get(i).getId());
//                sb.append(idList.get(i).getId());//拼接单引号,到数据库后台用in查询.
//                if (i != idList.size() - 1) {//前面的元素后面全拼上",",最后一个元素后不拼
//                    sb.append(",");
//                }
//            }
//        }
//        params.put("vouchers", sb);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                CustomProgress.CustomDismis();
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        InvestmentResultDialog("出借成功");
//                        //						Intent intent = new Intent(InvestmentDetailActivity.this,InvestmentSuccessActivity.class);
//                        //						intent.putExtra("proName", name);//项目名称
//                        //						intent.putExtra("amount", amount);//投资金额
//                        //						intent.putExtra("income", inCome1);//预期收益
//                        //						startActivity(intent);
//                    } else if (jsonObject.getString("state").equals("1")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        String respSubCode = dataObj.getString("respSubCode");
//                        if (respSubCode.equals("200419")) {//未授权
//                            dialogIsauthorize();//是否授权
//                        } else if (respSubCode.equals("200203")) {
//                            if (dataObj.getString("respMsg").equals("null")) {
//                                InvestmentResultDialog("系统错误");//投资失败
//                            } else {
//                                InvestmentResultDialog(dataObj.getString("respMsg"));//投资失败
//                            }
//                        } else {
//                            //							startActivity(new Intent(InvestmentDetailActivity.this,InvestmentFailActivity.class));
//                            InvestmentResultDialog(dataObj.getString("respMsg"));//投资失败
//                        }
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        if (jsonObject.getString("message").equals("null")) {
//                            InvestmentResultDialog("系统错误");//投资失败
//                        } else {
//                            InvestmentResultDialog(jsonObject.getString("message"));//投资失败
//                        }
//                        //						startActivity(new Intent(InvestmentDetailActivity.this,InvestmentFailActivity.class));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                CustomProgress.CustomDismis();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//
//    //立即投资 新接口
//    private void newOnceInvestment(String token, final String amount, String projectId, List<ValueVoucherEntity> idList, String from) {
//        //CustomProgress.show(this);
//        investmentProcessDialog();
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //AsyncHttpClient client = new AsyncHttpClient();
//        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.NEWUSERTOINVEST;
//        RequestParams params = new RequestParams();
//        params.put("token", token);
//        params.put("amount", amount);
//        params.put("projectId", projectId);
//        params.put("from", from);
//        sb = new StringBuilder();
//        if (idList.size() > 0) {
//            List<String> a = new ArrayList<String>();
//            for (int i = 0; i < idList.size(); i++) {
//                a.add(idList.get(i).getId());
//                sb.append(idList.get(i).getId());//拼接单引号,到数据库后台用in查询.
//                if (i != idList.size() - 1) {//前面的元素后面全拼上",",最后一个元素后不拼
//                    sb.append(",");
//                }
//            }
//        }
//        params.put("vouchers", sb);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                //				CustomProgress.CustomDismis();
//                investmentProcessdialog.dismiss();
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        if (investDialog.isShowing()) {
//                            investDialog.dismiss();
//                        }
//                        String tm = dataObj.getString("tm");
//                        String data = dataObj.getString("data");
//                        String merchantId = dataObj.getString("merchantId");
//                        Intent safetyIntent = new Intent(InvestmentDetailActivity.this, BankH5Activity.class);
//                        safetyIntent.putExtra("data", data);
//                        safetyIntent.putExtra("tm", tm);
//                        safetyIntent.putExtra("merchantId", merchantId);
//                        startActivity(safetyIntent);
//
//                        //							InvestmentResultDialog("出借成功");
//                        //						Intent intent = new Intent(InvestmentDetailActivity.this,InvestmentSuccessActivity.class);
//                        //						intent.putExtra("proName", name);//项目名称
//                        //						intent.putExtra("amount", amount);//投资金额
//                        //						intent.putExtra("income", inCome1);//预期收益
//                        //						startActivity(intent);
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        if (jsonObject.getString("message").equals("null")) {
//                            InvestmentResultDialog("系统错误");//投资失败
//                        } else {
//                            InvestmentResultDialog(jsonObject.getString("message"));//投资失败
//                        }
//                        //						startActivity(new Intent(InvestmentDetailActivity.this,InvestmentFailActivity.class));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                //				CustomProgress.CustomDismis();
//                investmentProcessdialog.dismiss();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//
//    //	private void onceInvestment(String token,final String amount,String projectId,String vouid,String from){
//    //		CustomProgress.show(this);
//    //		AsyncHttpClient client = new AsyncHttpClient();
//    //		String url = Urls.SAVEUSERINVEST;
//    //		RequestParams params = new RequestParams();
//    //		params.put("token", token);
//    //		params.put("amount", amount);
//    //		params.put("projectId", projectId);
//    //		params.put("vouid", vouid);
//    //		params.put("from", from);
//    //		client.post(url, params, new AsyncHttpResponseHandler() {
//    //
//    //			@Override
//    //			public void onSuccess(int statusCode, Header[] headers,
//    //					byte[] responseBody) {
//    //				CustomProgress.CustomDismis();
//    //				String result = new String(responseBody);
//    //				try {
//    //					JSONObject jsonObject = new JSONObject(result);
//    //					if (jsonObject.getString("state").equals("0")) {
//    ////						Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//    //						InvestmentResultDialog("出借成功");
//    //					}else if(jsonObject.getString("state").equals("4")){//系统超时
//    //						String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//    //						if(mGesture.equals("1")&&!mGesture.equals("")&&mGesture!=null){// 判断是否设置手势密码
//    //							//设置手势密码
//    //							Intent intent = new Intent(InvestmentDetailActivity.this,UnlockGesturePasswordActivity.class);
//    //							intent.putExtra("overtime", "0");
//    //							startActivity(intent);
//    //						}else{
//    //							//未设置手势密码
//    //							Intent intent = new Intent(InvestmentDetailActivity.this,LoginActivity.class);
//    //							intent.putExtra("overtime", "0");
//    //							startActivity(intent);
//    //						}
//    //						//						LoginUserProvider.cleanData(OnceInvestmentActivity.this);
//    //						//						LoginUserProvider.cleanDetailData(OnceInvestmentActivity.this);
//    //						DoCacheUtil util=DoCacheUtil.get(InvestmentDetailActivity.this);
//    //						util.put("isLogin", "");
//    //
//    //					}else{
//    //						InvestmentResultDialog(jsonObject.getString("message"));//投资失败
//    ////						Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//    //					}
//    //				} catch (JSONException e) {
//    //					e.printStackTrace();
//    //					Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//    //				}
//    //			}
//    //
//    //			@Override
//    //			public void onFailure(int statusCode,org.apache.http.Header[] headers, byte[] responseBody,
//    //					Throwable error) {
//    //				CustomProgress.CustomDismis();
//    //				Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//    //			}
//    //		});
//    //	}
//
//
//    // 授权提示框
//    private void dialogIsauthorize() {
//
//        isauthorizeDialog = new Dialog(InvestmentDetailActivity.this, R.style.MyDialog);
//        isauthorizeDialog.setContentView(R.layout.dl_isauthorize);
//
//        TextView tv_yes = (TextView) isauthorizeDialog.findViewById(R.id.tv_yes);
//        TextView tv_no = (TextView) isauthorizeDialog.findViewById(R.id.tv_no);
//        ImageView iv_dialog_close = (ImageView) isauthorizeDialog.findViewById(R.id.iv_dialog_close);
//        tv_yes.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //点击授权
//                userAuthorizationH5(LoginUserProvider.getUser(InvestmentDetailActivity.this).getToken(), "INVEST", "3");
//            }
//        });
//        tv_no.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //点击否取消授权，关闭弹框
//                isauthorizeDialog.dismiss();
//            }
//        });
//        iv_dialog_close.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                isauthorizeDialog.dismiss();
//            }
//        });
//        isauthorizeDialog.show();
//    }
//
//    //授权
//    private void userAuthorizationH5(String token, String grant, String from) {
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.USERAUTHORIZATIONH5;
//        RequestParams params = new RequestParams();
//        params.put("token", token);
//        params.put("from", from);
//        params.put("grant", grant);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.get("state").equals("0")) {
//                        isauthorizeDialog.dismiss();
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        String tm = dataObj.getString("tm");
//                        String data = dataObj.getString("data");
//                        String merchantId = dataObj.getString("merchantId");
//                        Intent safetyIntent = new Intent(InvestmentDetailActivity.this, BankH5Activity.class);
//                        safetyIntent.putExtra("data", data);
//                        safetyIntent.putExtra("tm", tm);
//                        safetyIntent.putExtra("merchantId", merchantId);
//                        startActivity(safetyIntent);
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//                    } else {
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//    //获取数据
//    private void getListData(String from, final int pageNo, int pageSize, String projectid, final int RequestCode) {
//        CustomProgress.show(this);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.GETPROJECTBIDLIST;
//        RequestParams params = new RequestParams();
//        params.put("from", from);
//        params.put("pageNo", pageNo);
//        params.put("pageSize", pageSize);
//        params.put("projectid", projectid);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                CustomProgress.CustomDismis();
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        int last = dataObj.getInt("last");//总页数
//                        investmentList = new ArrayList<InvestmentListEntity>();
//                        JSONArray bidlistArray = dataObj.getJSONArray("bidlist");
//                        if (bidlistArray.length() > 0) {
//                            for (int i = 0; i < bidlistArray.length(); i++) {
//                                JSONObject obj = bidlistArray.getJSONObject(i);
//                                InvestmentListEntity entity = new InvestmentListEntity();
//                                entity.setName(obj.getString("name"));
//                                entity.setAmount(obj.getString("amount"));
//                                entity.setCreatedate(obj.getString("createdate"));
//                                investmentList.add(entity);
//                            }
//                            //							ListViewUtils util = new ListViewUtils();
//                            //							util.setListViewHeightBasedOnChildren(lv_detail_investment_list);
//                            setView(investmentList, RequestCode);
//                            if (last == pageNo) {
//                                //								lv_detail_investment_list.onRefreshComplete();
//                                //								lv_detail_investment_list.setMode(Mode.PULL_FROM_START);
//                            } else {
//                                //								lv_detail_investment_list.setMode(Mode.BOTH);
//                            }
//                        }
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        //									LoginUserProvider.cleanData(InvestmentListActivity.this);
//                        //									LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//                    //					lv_detail_investment_list.onRefreshComplete();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    //					lv_detail_investment_list.onRefreshComplete();
//                    Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                //				lv_detail_investment_list.onRefreshComplete();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//                CustomProgress.CustomDismis();
//            }
//        });
//    }
//
//    protected void setView(List<InvestmentListEntity> investmentList,
//                           int requestCode) {
//        if (requestCode == REFRESH) {
//            investmentTotalList.clear();
//            if (investmentList != null && investmentList.size() > 0) {
//                investmentTotalList.addAll(investmentList);
//                pageNo = 1;
//            }
//
//        } else if (requestCode == LOADMORE) {
//            investmentTotalList.addAll(investmentList);
//            pageNo += 1;
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        pageNo = 1;
//        getListData("3", pageNo, pageSize, projectid, REFRESH);
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//        getListData("3", pageNo + 1, pageSize, projectid, LOADMORE);
//    }
//
//    // 投资成功或失败
//    private void InvestmentResultDialog(String result) {
//        investmentResult = new Dialog(InvestmentDetailActivity.this, R.style.MyDialog);
//        investmentResult.setContentView(R.layout.dl_investment_result);
//
//        TextView tv_investment_result = (TextView) investmentResult.findViewById(R.id.tv_investment_result);
//        tv_investment_result.setText(result);
//        TextView tv_text = (TextView) investmentResult.findViewById(R.id.tv_text);
//        tv_text.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                investmentResult.dismiss();
//                investDialog.dismiss();
//            }
//        });
//        investmentResult.show();
//    }
//
//
//    //未登录弹出框
//    public class NoLoginDialog extends Dialog {
//        Context context;
//
//        public NoLoginDialog(Context context) {
//            super(context);
//            this.context = context;
//        }
//
//        public NoLoginDialog(Context context, int theme) {
//            super(context, theme);
//            this.context = context;
//            this.setContentView(R.layout.dialog_no_login);
//            initView1();
//        }
//
//        private void initView1() {
//            Button bt_investment_login = (Button) findViewById(R.id.bt_investment_login);
//            TextView tv_back_home = (TextView) findViewById(R.id.tv_back_home);
//            //			tv_sign_days = (TextView) findViewById(R.id.tv_sign_days);//签到天数
//            //			tv_integral = (TextView) findViewById(R.id.tv_integral);//签到的积分
//            //			tv_sign_days.setText("已累计签到"+continuousTime+"天");
//            //			tv_integral.setText("积分+"+integral);
//            bt_investment_login.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    noLoginDialog.dismiss();
//
//                    if (isToken.equals("0")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "11");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "11");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//                    } else {//未登录
//                        Intent onceIntent = new Intent(InvestmentDetailActivity.this,
//                                LoginActivity.class);
//                        onceIntent.putExtra("overtime", "5");
//                        startActivity(onceIntent);
//                    }
//                }
//            });
//            tv_back_home.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    noLoginDialog.dismiss();
//                    Intent homeIntent = new Intent(InvestmentDetailActivity.this,
//                            MainActivity.class);
//                    homeIntent.putExtra("investment", "investmentDetailFrom");
//                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(homeIntent);
//                    //					finish();
//                }
//            });
//        }
//    }
//
//    //获取图片信息
//    private void getPicDate2(String type, final String creditInfoId) {//1-贸易背景  2-项目资质  3-风控资质
//        CustomProgress.show(this);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.GETINVENTORY;
//        RequestParams params = new RequestParams();
//        params.put("type", type);
//        params.put("creditInfoId", creditInfoId);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        JSONArray imgArray = dataObj.getJSONArray("imgList");
//                        relevantFileImgList = new ArrayList<FileGridViewEntity>();
//                        if (imgArray.length() > 0) {
//                            for (int i = 0; i < imgArray.length(); i++) {
//                                String obj = imgArray.getString(i);
//                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                fileGridViewEntity.setUrl(obj);
//                                fileGridViewEntity.setName("项目文件");
//                                relevantFileImgList.add(fileGridViewEntity);
//                            }
//                        }
//                        getPicDate1("1", creditInfoId, relevantFileImgList);//贸易背景
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        CustomProgress.CustomDismis();
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        CustomProgress.CustomDismis();
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    CustomProgress.CustomDismis();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                CustomProgress.CustomDismis();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//    //获取图片信息
//    private void getPicDate1(String type, final String creditInfoId, final List<FileGridViewEntity> list) {//1-贸易背景  2-项目资质  3-风控资质
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.GETINVENTORY;
//        RequestParams params = new RequestParams();
//        params.put("type", type);
//        params.put("creditInfoId", creditInfoId);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        JSONArray imgArray = dataObj.getJSONArray("imgList");
//                        if (imgArray.length() > 0) {
//                            for (int i = 0; i < imgArray.length(); i++) {
//                                String obj = imgArray.getString(i);
//                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                fileGridViewEntity.setUrl(obj);
//                                fileGridViewEntity.setName("贸易背景");
//                                list.add(fileGridViewEntity);
//                            }
//                        }
//                        getPicDate3("3", creditInfoId, list);//风控文件
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        CustomProgress.CustomDismis();
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        CustomProgress.CustomDismis();
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    CustomProgress.CustomDismis();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                CustomProgress.CustomDismis();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//    //获取图片信息
//    private void getPicDate3(String type, String creditInfoId, final List<FileGridViewEntity> list) {//1-贸易背景  2-项目资质  3-风控资质
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //		AsyncHttpClient client = new AsyncHttpClient();
//        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
//        String url = Urls.GETINVENTORY;
//        RequestParams params = new RequestParams();
//        params.put("type", type);
//        params.put("creditInfoId", creditInfoId);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers,
//                                  byte[] responseBody) {
//                CustomProgress.CustomDismis();
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        JSONArray imgArray = dataObj.getJSONArray("imgList");
//                        if (imgArray.length() > 0) {
//                            for (int i = 0; i < imgArray.length(); i++) {
//                                String obj = imgArray.getString(i);
//                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                fileGridViewEntity.setUrl(obj);
//                                fileGridViewEntity.setName("风控文件");
//                                list.add(fileGridViewEntity);
//                            }
//                        }
//                        if (list.size() > 0) {
//                            ll_see_more.setVisibility(View.VISIBLE);
//                        } else {
//                            ll_see_more.setVisibility(View.GONE);
//                        }
//                        List<FileGridViewEntity> threeRelevantFileImgList = new ArrayList<FileGridViewEntity>();
//                        if (list.size() >= 3) {
//                            for (int i = 0; i < 3; i++) {
//                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
//                                fileGridViewEntity.setUrl(list.get(i).getUrl());
//                                fileGridViewEntity.setName(list.get(i).getName());
//                                threeRelevantFileImgList.add(fileGridViewEntity);
//                            }
//                            fileGridViewAdapter = new FileGridViewAdapter(mContext, threeRelevantFileImgList);
//                            gv_relevant_file.setAdapter(fileGridViewAdapter);
//                        }
//
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(InvestmentDetailActivity.this).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(InvestmentDetailActivity.this, LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        DoCacheUtil util = DoCacheUtil.get(InvestmentDetailActivity.this);
//                        util.put("isLogin", "");
//
//                    } else {
//                        Toast.makeText(InvestmentDetailActivity.this, jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
//                                  Throwable error) {
//                CustomProgress.CustomDismis();
//                Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
//            }
//        });
//    }
//
//    // 没有做风险测评提示框
//    private void testDialog() {
//
//        final Dialog testdialog = new Dialog(InvestmentDetailActivity.this, R.style.MyDialog);
//        testdialog.setContentView(R.layout.dl_istest);
//
//        TextView tv_yes = (TextView) testdialog.findViewById(R.id.tv_yes);
//        TextView tv_no = (TextView) testdialog.findViewById(R.id.tv_no);
//        tv_yes.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //跳转到测试题界面
//                Intent testIntent = new Intent(InvestmentDetailActivity.this, TestQuestionFirstActivity.class);
//                testIntent.putExtra("isInvestment", "0");
//                startActivity(testIntent);
//                testdialog.dismiss();
//            }
//        });
//        tv_no.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //不做测试，关闭弹框
//                testdialog.dismiss();
//            }
//        });
//        testdialog.show();
//    }
//
//    // 出借过程的对话框
//    private void investmentProcessDialog() {
//        investmentProcessdialog = new Dialog(InvestmentDetailActivity.this, R.style.MyDialogUpVersion);
//        investmentProcessdialog.setContentView(R.layout.dialog_investment);
//        investmentProcessdialog.setCancelable(false);
//        investmentProcessdialog.show();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mContext = null;
//    }
//}
