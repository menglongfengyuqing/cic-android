package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.MyInvestmentDetailPaymentAdapter;
import com.ztmg.cicmorgan.account.entity.MyInvestmentDetailPaymentEntity;
import com.ztmg.cicmorgan.activity.ExpandListView;
import com.ztmg.cicmorgan.activity.FullScreenImageActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.MyGridView;
import com.ztmg.cicmorgan.investment.activity.PaymentPlanActivity;
import com.ztmg.cicmorgan.investment.adapter.FileGridViewAdapter;
import com.ztmg.cicmorgan.investment.adapter.InvestmentListAdapter;
import com.ztmg.cicmorgan.investment.entity.FileGridViewEntity;
import com.ztmg.cicmorgan.investment.entity.ImgEntity;
import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.SafetyActivity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * 我的投资详情添加回款列表
 */
public class MyInvestmentPaymentDetailActivity extends BaseActivity implements OnClickListener {
    private String projectid, bidid;
    private ListView lv_payment_list;
    private List<MyInvestmentDetailPaymentEntity> paymentList;
    private TextView tv_product_introduction, tv_wind_control_introduction, tv_lend_record;
    private View v_product_introduction_line, v_wind_control_line, v_lend_record_line;
    private LinearLayout ll_product_introduction_content, ll_wind_control_content, lend_list;
    private MyGridView gv_loan, gv_payment, gv_relevant_file;
    private LinearLayout ll_see_more;
    private List<FileGridViewEntity> loanFileImgList;//借款方
    private List<FileGridViewEntity> paymentFileImgList;//付款方
    private List<FileGridViewEntity> relevantFileImgList;//相关文件
    private FileGridViewAdapter fileGridViewAdapter;
    private List<InvestmentListEntity> investmentList;
    private InvestmentListAdapter investmentListAdapter;
    private ExpandListView lv_detail_investment_list;

    private ImgEntity entity;
    private List<ImgEntity> docimgsList, wgimgList, imgList;//借款资质照片，风控照片，项目照片
    private String creditInfoId;

    private TextView tv_loan_span, tv_year_rate, tv_payment_mode;
    private TextView tv_project_name, tv_loan_party, tv_payment_party, tv_project_amount, tv_project_span, tv_loan_rate, tv_payment_style, tv_interest_time;
    private RelativeLayout rl_repayment_plan;//还款计划
    private ImageView iv_down_enter, iv_introduction_down_enter;
    private LinearLayout ll_question_list;
    private TextView tv_product_contents;
    private String name, sn;
    private boolean questionIsOpen = true;
    private boolean introductionIsOpen = true;
    private String creditName, creditUrl;
    private TextView tv_risk_tip_first, tv_risk_tip_second;
    private TextView tv_use_of_funds, tv_payment_source, tv_loan;
    private TextView tv_payment;

    private LinearLayout ll_content;
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
        super.setContentView(R.layout.activity_my_investment_payment_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(MyInvestmentPaymentDetailActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        Intent intent = getIntent();
        projectid = intent.getStringExtra("projectid");
        bidid = intent.getStringExtra("bidid");
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData("3", projectid);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
    }

    @Override
    protected void initView() {
        setTitle("账户信息");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        View headerView = LayoutInflater.from(MyInvestmentPaymentDetailActivity.this).inflate(R.layout.activity_my_investment_payment_detail_header, null);
        View footerView = LayoutInflater.from(MyInvestmentPaymentDetailActivity.this).inflate(R.layout.activity_my_investment_payment_detail_footer, null);
        lv_payment_list = (ListView) findViewById(R.id.lv_payment_list);
        lv_payment_list.addHeaderView(headerView);
        lv_payment_list.addFooterView(footerView);

        //		paymentList = new ArrayList<String>();
        //		for(int i = 0;i<5;i++){
        //			paymentList.add("回款计划"+i);
        //		}
        //		MyInvestmentDetailPaymentAdapter paymentAdapter = new MyInvestmentDetailPaymentAdapter(MyInvestmentPaymentDetailActivity.this, paymentList);
        //		lv_payment_list.setAdapter(paymentAdapter);

        tv_loan_span = (TextView) findViewById(R.id.tv_loan_span);//项目期限
        tv_year_rate = (TextView) findViewById(R.id.tv_year_rate);//预期年化利率
        tv_payment_mode = (TextView) findViewById(R.id.tv_payment_mode);//还款方式

        tv_project_name = (TextView) footerView.findViewById(R.id.tv_project_name);//项目名称
        tv_loan_party = (TextView) footerView.findViewById(R.id.tv_loan_party);//借款方
        tv_payment_party = (TextView) footerView.findViewById(R.id.tv_payment_party);//付款方
        tv_payment_party.setOnClickListener(this);
        tv_project_amount = (TextView) footerView.findViewById(R.id.tv_project_amount);//项目金额
        tv_project_span = (TextView) footerView.findViewById(R.id.tv_project_span);//项目期限
        tv_loan_rate = (TextView) footerView.findViewById(R.id.tv_loan_rate);//预期出借利率
        tv_payment_style = (TextView) footerView.findViewById(R.id.tv_payment_style);//还款方式
        tv_interest_time = (TextView) footerView.findViewById(R.id.tv_interest_time);//起息时间
        rl_repayment_plan = (RelativeLayout) footerView.findViewById(R.id.rl_repayment_plan);//还款计划
        rl_repayment_plan.setOnClickListener(this);
        iv_down_enter = (ImageView) footerView.findViewById(R.id.iv_down_enter);//常见问题
        iv_down_enter.setOnClickListener(this);
        ll_question_list = (LinearLayout) footerView.findViewById(R.id.ll_question_list);
        iv_introduction_down_enter = (ImageView) footerView.findViewById(R.id.iv_introduction_down_enter);//项目简介
        iv_introduction_down_enter.setOnClickListener(this);
        tv_risk_tip_first = (TextView) footerView.findViewById(R.id.tv_risk_tip_first);//风险提示1
        tv_risk_tip_first.setOnClickListener(this);
        tv_risk_tip_second = (TextView) footerView.findViewById(R.id.tv_risk_tip_second);//风险提示2
        tv_risk_tip_second.setOnClickListener(this);
        tv_use_of_funds = (TextView) footerView.findViewById(R.id.tv_use_of_funds);//资金用途
        tv_payment_source = (TextView) footerView.findViewById(R.id.tv_payment_source);//还款来源
        tv_loan = (TextView) footerView.findViewById(R.id.tv_loan);//借款方
        tv_payment = (TextView) footerView.findViewById(R.id.tv_payment);//付款方
        tv_payment.setOnClickListener(this);
        tv_product_contents = (TextView) footerView.findViewById(R.id.tv_product_contents);
        footerView.findViewById(R.id.ll_product_introduction).setOnClickListener(this);
        tv_product_introduction = (TextView) footerView.findViewById(R.id.tv_product_introduction);
        v_product_introduction_line = footerView.findViewById(R.id.v_product_introduction_line);
        footerView.findViewById(R.id.ll_wind_control_introduction).setOnClickListener(this);//风控情况
        tv_wind_control_introduction = (TextView) footerView.findViewById(R.id.tv_wind_control_introduction);
        v_wind_control_line = footerView.findViewById(R.id.v_wind_control_line);
        footerView.findViewById(R.id.ll_lend_record).setOnClickListener(this);
        tv_lend_record = (TextView) footerView.findViewById(R.id.tv_lend_record);
        v_lend_record_line = footerView.findViewById(R.id.v_lend_record_line);

        ll_product_introduction_content = (LinearLayout) footerView.findViewById(R.id.ll_product_introduction_content);//产品介绍
        ll_wind_control_content = (LinearLayout) footerView.findViewById(R.id.ll_wind_control_content);
        lend_list = (LinearLayout) footerView.findViewById(R.id.lend_list);//借款记录

        //借款方
        gv_loan = (MyGridView) footerView.findViewById(R.id.gv_loan);
        gv_loan.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, FullScreenImageActivity.class);
                List<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < loanFileImgList.size(); i++) {
                    imageUrls.add(loanFileImgList.get(i).getUrl());
                }
                intent.putExtra("currentPosition", position);
                intent.putStringArrayListExtra("urls",
                        (ArrayList<String>) imageUrls);
                intent.setClass(MyInvestmentPaymentDetailActivity.this, FullScreenImageActivity.class);
                startActivity(intent);
            }
        });

        //付款方
        gv_payment = (MyGridView) footerView.findViewById(R.id.gv_payment);
        gv_payment.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, FullScreenImageActivity.class);
                List<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < paymentFileImgList.size(); i++) {
                    imageUrls.add(paymentFileImgList.get(i).getUrl());
                }
                intent.putExtra("currentPosition", position);
                intent.putStringArrayListExtra("urls",
                        (ArrayList<String>) imageUrls);
                intent.setClass(MyInvestmentPaymentDetailActivity.this, FullScreenImageActivity.class);
                startActivity(intent);
            }
        });

        //相关文件
        gv_relevant_file = (MyGridView) footerView.findViewById(R.id.gv_relevant_file);
        gv_relevant_file.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, FullScreenImageActivity.class);
                List<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < relevantFileImgList.size(); i++) {
                    imageUrls.add(relevantFileImgList.get(i).getUrl());
                }
                intent.putExtra("currentPosition", position);
                intent.putStringArrayListExtra("urls",
                        (ArrayList<String>) imageUrls);
                intent.setClass(MyInvestmentPaymentDetailActivity.this, FullScreenImageActivity.class);
                startActivity(intent);
            }
        });

        ll_see_more = (LinearLayout) footerView.findViewById(R.id.ll_see_more);
        ll_see_more.setOnClickListener(this);

        docimgsList = new ArrayList<ImgEntity>();
        wgimgList = new ArrayList<ImgEntity>();
        imgList = new ArrayList<ImgEntity>();

        lv_detail_investment_list = (ExpandListView) footerView.findViewById(R.id.lv_investment_detail_list);
        findViewById(R.id.tv_tip_product).setOnClickListener(this);
        findViewById(R.id.tv_tip_wind_control).setOnClickListener(this);
        findViewById(R.id.tv_tip_loan_record).setOnClickListener(this);

        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_product_introduction://产品介绍
                tv_product_introduction.setTextColor(getResources().getColor(R.color.blue));
                tv_wind_control_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_lend_record.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                v_product_introduction_line.setVisibility(View.VISIBLE);
                v_wind_control_line.setVisibility(View.GONE);
                v_lend_record_line.setVisibility(View.GONE);
                ll_product_introduction_content.setVisibility(View.VISIBLE);
                ll_wind_control_content.setVisibility(View.GONE);
                lend_list.setVisibility(View.GONE);

                break;

            case R.id.ll_wind_control_introduction://风控情况

                tv_product_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_wind_control_introduction.setTextColor(getResources().getColor(R.color.blue));
                tv_lend_record.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                v_product_introduction_line.setVisibility(View.GONE);
                v_wind_control_line.setVisibility(View.VISIBLE);
                v_lend_record_line.setVisibility(View.GONE);
                ll_product_introduction_content.setVisibility(View.GONE);
                ll_wind_control_content.setVisibility(View.VISIBLE);
                lend_list.setVisibility(View.GONE);
                //获取相关文件
                getPicDate2("2", creditInfoId);// 2-项目文件,1-贸易背景 ,3-风控文件


                break;
            case R.id.ll_lend_record://出借记录
                tv_product_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_wind_control_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_lend_record.setTextColor(getResources().getColor(R.color.blue));
                v_product_introduction_line.setVisibility(View.GONE);
                v_wind_control_line.setVisibility(View.GONE);
                v_lend_record_line.setVisibility(View.VISIBLE);
                ll_product_introduction_content.setVisibility(View.GONE);
                ll_wind_control_content.setVisibility(View.GONE);
                lend_list.setVisibility(View.VISIBLE);
                getListData("3", "1", "500", projectid);

                break;
            //安全保障
            case R.id.rl_safe_guarantee:
                Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, SafetyActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_see_more:
                fileGridViewAdapter = new FileGridViewAdapter(MyInvestmentPaymentDetailActivity.this, relevantFileImgList);
                gv_relevant_file.setAdapter(fileGridViewAdapter);
                ll_see_more.setVisibility(View.GONE);
                break;
            case R.id.rl_repayment_plan://还款计划
                Intent repaymentIntent = new Intent(MyInvestmentPaymentDetailActivity.this,
                        PaymentPlanActivity.class);
                repaymentIntent.putExtra("projectid", projectid);
                startActivity(repaymentIntent);
                break;
            case R.id.iv_down_enter://常见问题下拉
                if (questionIsOpen) {
                    ll_question_list.setVisibility(View.VISIBLE);
                    questionIsOpen = false;
                    iv_down_enter.setBackgroundResource(R.drawable.pic_up);
                } else {
                    ll_question_list.setVisibility(View.GONE);
                    questionIsOpen = true;
                    iv_down_enter.setBackgroundResource(R.drawable.pic_down);
                }
                break;
            case R.id.iv_introduction_down_enter:
                if (introductionIsOpen) {
                    tv_product_contents.setVisibility(View.VISIBLE);
                    introductionIsOpen = false;
                    iv_introduction_down_enter.setBackgroundResource(R.drawable.pic_up);
                } else {
                    tv_product_contents.setVisibility(View.GONE);
                    introductionIsOpen = true;
                    iv_introduction_down_enter.setBackgroundResource(R.drawable.pic_down);
                }
                break;
            case R.id.tv_payment_party://付款方
                if (!TextUtils.isEmpty(creditUrl) && !creditUrl.equals("null") && creditUrl != null) {
                    Intent mthLogoIntent = new Intent(MyInvestmentPaymentDetailActivity.this, AgreementActivity.class);
                    mthLogoIntent.putExtra("path", creditUrl);
                    mthLogoIntent.putExtra("title", creditName);
                    startActivity(mthLogoIntent);
                }
                break;
            case R.id.tv_risk_tip_first:
                Intent riskTipFirstIntent = new Intent(MyInvestmentPaymentDetailActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            case R.id.tv_risk_tip_second:
                Intent riskTipSecondIntent = new Intent(MyInvestmentPaymentDetailActivity.this, AgreementActivity.class);
                riskTipSecondIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipSecondIntent.putExtra("title", "网络借贷平台禁止性行为提示书");
                startActivity(riskTipSecondIntent);
                break;
            case R.id.tv_payment://付款方
                if (!TextUtils.isEmpty(creditUrl) && !creditUrl.equals("null") && creditUrl != null) {
                    Intent paymentIntent = new Intent(MyInvestmentPaymentDetailActivity.this, AgreementActivity.class);
                    paymentIntent.putExtra("path", creditUrl);
                    paymentIntent.putExtra("title", creditName);
                    startActivity(paymentIntent);
                }
                break;
            case R.id.tv_tip_product://市场有风险，出借需谨慎提示语
            case R.id.tv_tip_wind_control:
            case R.id.tv_tip_loan_record:
                Intent tipIntent = new Intent(MyInvestmentPaymentDetailActivity.this, AgreementActivity.class);
                tipIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                tipIntent.putExtra("title", "风险提示书");
                startActivity(tipIntent);
                break;
            default:
                break;
        }
    }


    // 获取项目详情
    private void getData(String from, String projectid) {
        //		CustomProgress.show(this);
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
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                //				CustomProgress.CustomDismis();
                //我的投资详情添加回款计划获取回款计划列表
                ll_content.setVisibility(View.VISIBLE);
                getPaymentList("3", LoginUserProvider.getUser(MyInvestmentPaymentDetailActivity.this).getToken(), bidid);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {

                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足

                        if (dataObj.has("proimg")) {
                            JSONArray proimgArray = dataObj.getJSONArray("proimg");// 项目照片
                            if (proimgArray.length() > 0) {
                                for (int i = 0; i < proimgArray.length(); i++) {
                                    entity = new ImgEntity();
                                    String obj = proimgArray.getString(i);
                                    entity.setImg(obj);
                                    imgList.add(entity);
                                }
                            }
                        }

                        if (dataObj.has("purpose")) {// 资金用途
                            tv_use_of_funds.setText(dataObj.getString("purpose"));
                        }
                        if (dataObj.has("sourceOfRepayment")) {//还款来源

                            if (TextUtils.isEmpty(dataObj.getString("sourceOfRepayment")) || dataObj.getString("sourceOfRepayment").equals("null")) {
                                tv_payment_source.setText("");
                            } else {
                                tv_payment_source.setText(dataObj.getString("sourceOfRepayment"));
                            }
                        }
                        if (dataObj.has("borrowerCompanyName")) {//借款方
                            tv_loan_party.setText(dataObj.getString("borrowerCompanyName"));//借款方
                            tv_loan.setText(dataObj.getString("borrowerCompanyName"));
                        }
                        if (dataObj.has("replaceRepayCompanyName")) {//付款方
                            tv_payment_party.setText(dataObj.getString("replaceRepayCompanyName"));//付款方
                            tv_payment.setText(dataObj.getString("replaceRepayCompanyName"));//付款方
                        }

                        //三证合一
                        if (dataObj.has("businessLicenses")) {
                            JSONArray businessLicensesArray = dataObj.getJSONArray("businessLicenses");
                            loanFileImgList = new ArrayList<FileGridViewEntity>();
                            if (businessLicensesArray.length() > 0) {
                                for (int i = 0; i < businessLicensesArray.length(); i++) {
                                    JSONObject obj = businessLicensesArray.getJSONObject(i);
                                    FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                    fileGridViewEntity.setUrl(obj.getString("url"));
                                    fileGridViewEntity.setName("三证合一");
                                    loanFileImgList.add(fileGridViewEntity);
                                }
                            }
                        }

                        //开户许可证
                        if (dataObj.has("bankPermitCerts")) {
                            JSONArray bankPermitCertsArray = dataObj.getJSONArray("bankPermitCerts");
                            if (bankPermitCertsArray.length() > 0) {
                                for (int i = 0; i < bankPermitCertsArray.length(); i++) {
                                    JSONObject obj = bankPermitCertsArray.getJSONObject(i);
                                    FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                    fileGridViewEntity.setUrl(obj.getString("url"));
                                    fileGridViewEntity.setName("开户许可证");
                                    loanFileImgList.add(fileGridViewEntity);
                                }
                            }
                            fileGridViewAdapter = new FileGridViewAdapter(MyInvestmentPaymentDetailActivity.this, loanFileImgList);
                            gv_loan.setAdapter(fileGridViewAdapter);
                        }

                        //						//承诺函
                        if (dataObj.has("commitments")) {
                            JSONArray commitmentsArray = dataObj.getJSONArray("commitments");
                            paymentFileImgList = new ArrayList<FileGridViewEntity>();
                            if (commitmentsArray.length() > 0) {
                                for (int i = 0; i < commitmentsArray.length(); i++) {
                                    JSONObject obj = commitmentsArray.getJSONObject(i);
                                    FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                    fileGridViewEntity.setUrl(obj.getString("url"));
                                    fileGridViewEntity.setName("承诺函");
                                    paymentFileImgList.add(fileGridViewEntity);
                                }
                                fileGridViewAdapter = new FileGridViewAdapter(MyInvestmentPaymentDetailActivity.this, paymentFileImgList);
                                gv_payment.setAdapter(fileGridViewAdapter);
                            }
                        }


                        if (dataObj.has("docimgs")) {
                            JSONArray docimgsArray = dataObj
                                    .getJSONArray("docimgs");// 借款资质图片信息
                            if (docimgsArray.length() > 0) {
                                for (int i = 0; i < docimgsArray.length(); i++) {
                                    entity = new ImgEntity();
                                    String obj = docimgsArray.getString(i);
                                    entity.setImg(obj);
                                    docimgsList.add(entity);
                                }
                            }
                        }
                        if (dataObj.has("countdowndate")) {
                            String countdowndate = dataObj
                                    .getString("countdowndate");
                        }
                        if (dataObj.has("wgcompany")) {// 担保公司
                            String wgcompany = dataObj.getString("wgcompany");
                            //							tv_safe_company.setText(wgcompany);
                        }
                        if (dataObj.has("amount")) {// 项目金额
                            //							amount = dataObj.getString("amount");
                        }
                        if (dataObj.has("rate")) {// 年化收益
                            tv_year_rate.setText(dataObj.getString("rate") + "%");
                            tv_loan_rate.setText(dataObj.getString("rate") + "%");
                        }
                        if (dataObj.has("repaytype")) {
                            tv_payment_mode.setText(dataObj.getString("repaytype"));
                            tv_payment_style.setText(dataObj.getString("repaytype"));
                        }
                        if (dataObj.has("name")) {
                            //							name = dataObj.getString("name");// 项目名字
                            //							setTitle(name);
                        }
                        if (dataObj.has("currentamount")) {
                            String currentamount = dataObj
                                    .getString("currentamount");// 当前投资金额
                        }
                        if (dataObj.has("locus")) {// 项目所在地
                        }
                        if (dataObj.has("guaranteecase")) {// 担保情况
                        }
                        //						tv_profession.setText(dataObj.getString("industry"));// 所属行业
                        if (dataObj.has("wgimglist")) {
                            JSONArray wgimglistArray = dataObj
                                    .getJSONArray("wgimglist");// 风控文件图片信息
                            if (wgimglistArray.length() > 0) {
                                for (int i = 0; i < wgimglistArray.length(); i++) {
                                    entity = new ImgEntity();
                                    String wgimglistObj = wgimglistArray
                                            .getString(i);
                                    entity.setImg(wgimglistObj);
                                    wgimgList.add(entity);
                                    //									wgimgList.add(wgimglistObj);
                                }
                            }
                        }
                        if (dataObj.has("bidtotal")) {
                            String bidtotal = dataObj.getString("bidtotal");// 项目投资总条数
                        }
                        if (dataObj.has("projectcase")) {// 项目情况
                            tv_product_contents.setText(dataObj.getString("projectcase"));
                        }
                        if (dataObj.has("span")) {
                            tv_loan_span.setText(dataObj.getString("span") + "天");// 项目期限
                            tv_project_span.setText(dataObj.getString("span") + "天");// 项目期限
                        }
                        if (dataObj.has("creditInfoId")) {
                            creditInfoId = dataObj.getString("creditInfoId");//项目图片需要的id
                        }
                        if (dataObj.has("isNewType")) {
                            //							isNewType = dataObj.getString("isNewType");// isNewType,2供应链
                        }
                        if (dataObj.has("creditName")) {
                            creditName = dataObj.getString("creditName");
                        }
                        if (dataObj.has("creditUrl")) {
                            creditUrl = dataObj.getString("creditUrl");
                        }
                        if (dataObj.has("name")) {
                            name = dataObj.getString("name");// 项目名字
                        }
                        if (dataObj.has("sn")) {
                            sn = dataObj.getString("sn");
                        }
                        tv_project_name.setText(name + "(" + sn + ")");//项目名称


                        if (dataObj.has("amount")) {//项目金额
                            tv_project_amount.setText(decimalFormat.format(Double.parseDouble(dataObj.getString("amount"))) + "元");//项目金额
                        }
                        //						tv_project_span.setText(dataObj.getString("span")+"天");//项目期限
                        //						tv_loan_rate.setText(rate+"%");//预借年化利率
                        //						tv_payment_style.setText(dataObj.getString("repaytype"));//还款方式
                        if (dataObj.has("loandate")) {
                            tv_interest_time.setText(dataObj.getString("loandate"));//起息时间
                        }

                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(
                                MyInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("")
                                && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(
                                    MyInvestmentPaymentDetailActivity.this,
                                    UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(
                                    MyInvestmentPaymentDetailActivity.this,
                                    LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil
                                .get(MyInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(MyInvestmentPaymentDetailActivity.this,
                                jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(MyInvestmentPaymentDetailActivity.this, "解析异常", 0)
                            .show();
                }

            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MyInvestmentPaymentDetailActivity.this, "请检查网络", 0)
                        .show();
                CustomProgress.CustomDismis();
            }
        });
    }

    ;


    //获取图片信息
    private void getPicDate2(String type, final String creditInfoId) {//1-贸易背景  2-项目资质  3-风控资质
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
        String url = Urls.GETINVENTORY;
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("creditInfoId", creditInfoId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONArray imgArray = dataObj.getJSONArray("imgList");
                        relevantFileImgList = new ArrayList<FileGridViewEntity>();
                        if (imgArray.length() > 0) {
                            for (int i = 0; i < imgArray.length(); i++) {
                                String obj = imgArray.getString(i);
                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                fileGridViewEntity.setUrl(obj);
                                fileGridViewEntity.setName("项目文件");
                                relevantFileImgList.add(fileGridViewEntity);
                            }
                        }
                        getPicDate1("1", creditInfoId, relevantFileImgList);//贸易背景
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(MyInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(MyInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(MyInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取图片信息
    private void getPicDate1(String type, final String creditInfoId, final List<FileGridViewEntity> list) {//1-贸易背景  2-项目资质  3-风控资质
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
        String url = Urls.GETINVENTORY;
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("creditInfoId", creditInfoId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONArray imgArray = dataObj.getJSONArray("imgList");
                        if (imgArray.length() > 0) {
                            for (int i = 0; i < imgArray.length(); i++) {
                                String obj = imgArray.getString(i);
                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                fileGridViewEntity.setUrl(obj);
                                fileGridViewEntity.setName("贸易背景");
                                list.add(fileGridViewEntity);
                            }
                        }
                        getPicDate3("3", creditInfoId, list);//风控文件
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(MyInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(MyInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(MyInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取图片信息
    private void getPicDate3(String type, String creditInfoId, final List<FileGridViewEntity> list) {//1-贸易背景  2-项目资质  3-风控资质
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
        String url = Urls.GETINVENTORY;
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("creditInfoId", creditInfoId);
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
                        JSONArray imgArray = dataObj.getJSONArray("imgList");
                        if (imgArray.length() > 0) {
                            for (int i = 0; i < imgArray.length(); i++) {
                                String obj = imgArray.getString(i);
                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                fileGridViewEntity.setUrl(obj);
                                fileGridViewEntity.setName("风控文件");
                                list.add(fileGridViewEntity);
                            }
                        }
                        if (list.size() > 0) {
                            ll_see_more.setVisibility(View.VISIBLE);
                        } else {
                            ll_see_more.setVisibility(View.GONE);
                        }
                        List<FileGridViewEntity> threeRelevantFileImgList = new ArrayList<FileGridViewEntity>();
                        if (list.size() > 0) {
                            for (int i = 0; i < 3; i++) {
                                FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                                fileGridViewEntity.setUrl(list.get(i).getUrl());
                                fileGridViewEntity.setName(list.get(i).getName());
                                threeRelevantFileImgList.add(fileGridViewEntity);
                            }
                            fileGridViewAdapter = new FileGridViewAdapter(MyInvestmentPaymentDetailActivity.this, threeRelevantFileImgList);
                            gv_relevant_file.setAdapter(fileGridViewAdapter);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(MyInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取出借列表
    private void getListData(String from, String pageNo, String pageSize, String projectid) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
        String url = Urls.GETPROJECTBIDLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("projectid", projectid);
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
                        int last = dataObj.getInt("last");//总页数
                        investmentList = new ArrayList<InvestmentListEntity>();
                        JSONArray bidlistArray = dataObj.getJSONArray("bidlist");
                        if (bidlistArray.length() > 0) {
                            for (int i = 0; i < bidlistArray.length(); i++) {
                                JSONObject obj = bidlistArray.getJSONObject(i);
                                InvestmentListEntity entity = new InvestmentListEntity();
                                entity.setName(obj.getString("name"));
                                entity.setAmount(obj.getString("amount"));
                                entity.setCreatedate(obj.getString("createdate"));
                                investmentList.add(entity);
                            }

                            investmentListAdapter = new InvestmentListAdapter(MyInvestmentPaymentDetailActivity.this, investmentList);
                            lv_detail_investment_list.setAdapter(investmentListAdapter);

                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyInvestmentPaymentDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MyInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    // TODO 回款计划接口
    //获取回款列表
    private void getPaymentList(String from, String token, String investId) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailActivity.this));
        String url = Urls.GETUSERINTERESTLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("investId", investId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                //				CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        paymentList = new ArrayList<MyInvestmentDetailPaymentEntity>();
                        //									for(int i = 0;i<5;i++){
                        //										paymentList.add("回款计划"+i);
                        //									}
                        //									MyInvestmentDetailPaymentAdapter paymentAdapter = new MyInvestmentDetailPaymentAdapter(MyInvestmentPaymentDetailActivity.this, paymentList);
                        //									lv_payment_list.setAdapter(paymentAdapter);
                        JSONArray userPlanListArray = dataObj.getJSONArray("userPlanList");
                        if (userPlanListArray.length() > 0) {
                            for (int i = 0; i < userPlanListArray.length(); i++) {
                                JSONObject obj = userPlanListArray.getJSONObject(i);
                                MyInvestmentDetailPaymentEntity myInvestmentDetailPaymentEntity = new MyInvestmentDetailPaymentEntity();
                                myInvestmentDetailPaymentEntity.setAmount(obj.getString("amount"));
                                myInvestmentDetailPaymentEntity.setPrincipal(obj.getString("principal"));
                                myInvestmentDetailPaymentEntity.setRepaymentDate(obj.getString("repaymentDate"));
                                myInvestmentDetailPaymentEntity.setState(obj.getString("state"));
                                paymentList.add(myInvestmentDetailPaymentEntity);
                            }
                            MyInvestmentDetailPaymentAdapter paymentAdapter = new MyInvestmentDetailPaymentAdapter(MyInvestmentPaymentDetailActivity.this, paymentList);
                            lv_payment_list.setAdapter(paymentAdapter);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyInvestmentPaymentDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MyInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
                //				CustomProgress.CustomDismis();
            }
        });
    }
}
