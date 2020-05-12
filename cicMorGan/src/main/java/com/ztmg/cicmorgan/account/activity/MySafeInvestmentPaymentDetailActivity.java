package com.ztmg.cicmorgan.account.activity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.MyInvestmentDetailPaymentAdapter;
import com.ztmg.cicmorgan.account.entity.MyInvestmentDetailPaymentEntity;
import com.ztmg.cicmorgan.activity.ExpandListView;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailFileActivity;
import com.ztmg.cicmorgan.investment.activity.PaymentPlanActivity;
import com.ztmg.cicmorgan.investment.activity.SafeInvestmentDetailActivity;
import com.ztmg.cicmorgan.investment.adapter.FileGridViewAdapter;
import com.ztmg.cicmorgan.investment.adapter.InvestmentListAdapter;
import com.ztmg.cicmorgan.investment.entity.FileGridViewEntity;
import com.ztmg.cicmorgan.investment.entity.ImgEntity;
import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

public class MySafeInvestmentPaymentDetailActivity extends BaseActivity implements OnClickListener {

    private ListView lv_safe_payment_list;
    private String projectid, bidid;
    private ImgEntity entity;
    private List<ImgEntity> docimgsList, wgimgList, imgList;//借款资质照片，风控照片，项目照片
    private List<MyInvestmentDetailPaymentEntity> paymentList;
    private TextView tv_product_introduction_content;

    private TextView tv_product_introduction, tv_lend_record, tv_common_problem;
    private View v_product_introduction_line, v_lend_record_line, v_common_problem_line;
    private LinearLayout ll_product_introduction_content, ll_question_text;
    private LinearLayout lend_list;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 300;//当前页面的内容数目
    private List<InvestmentListEntity> investmentList;
    private List<InvestmentListEntity> investmentTotalList;
    private InvestmentListAdapter adapter;
    private ExpandListView lv_detail_investment_list;
    private TextView tv_loan_span, tv_year_rate, tv_payment_mode;

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
        super.setContentView(R.layout.activity_my_safe_investment_payment_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(MySafeInvestmentPaymentDetailActivity.this);
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

        tv_loan_span = (TextView) findViewById(R.id.tv_loan_span);
        tv_year_rate = (TextView) findViewById(R.id.tv_year_rate);
        tv_payment_mode = (TextView) findViewById(R.id.tv_payment_mode);
        View headerView = LayoutInflater.from(MySafeInvestmentPaymentDetailActivity.this).inflate(R.layout.activity_my_safe_investment_payment_detail_header, null);
        View footerView = LayoutInflater.from(MySafeInvestmentPaymentDetailActivity.this).inflate(R.layout.activity_my_safe_investment_payment_detail_footer, null);
        lv_safe_payment_list = (ListView) findViewById(R.id.lv_safe_payment_list);
        lv_safe_payment_list.addHeaderView(headerView);
        lv_safe_payment_list.addFooterView(footerView);

        docimgsList = new ArrayList<ImgEntity>();
        wgimgList = new ArrayList<ImgEntity>();
        imgList = new ArrayList<ImgEntity>();

        tv_product_introduction = (TextView) footerView.findViewById(R.id.tv_product_introduction);
        v_product_introduction_line = footerView.findViewById(R.id.v_product_introduction_line);
        footerView.findViewById(R.id.ll_lend_record).setOnClickListener(this);
        tv_lend_record = (TextView) footerView.findViewById(R.id.tv_lend_record);
        v_lend_record_line = footerView.findViewById(R.id.v_lend_record_line);
        footerView.findViewById(R.id.ll_common_problem).setOnClickListener(this);
        tv_common_problem = (TextView) footerView.findViewById(R.id.tv_common_problem);
        v_common_problem_line = footerView.findViewById(R.id.v_common_problem_line);

        //		tv_text = (TextView) findViewById(R.id.tv_text);//产品介绍
        ll_product_introduction_content = (LinearLayout) footerView.findViewById(R.id.ll_product_introduction_content);//产品介绍
        ll_question_text = (LinearLayout) footerView.findViewById(R.id.ll_question_text);//常见问题
        lend_list = (LinearLayout) footerView.findViewById(R.id.lend_list);//借款记录


        tv_product_introduction_content = (TextView) footerView.findViewById(R.id.tv_product_introduction_content);//项目介绍
        footerView.findViewById(R.id.rl_investment_detail_file).setOnClickListener(this);//相关文件
        footerView.findViewById(R.id.ll_product_introduction).setOnClickListener(this);//产品介绍
        footerView.findViewById(R.id.rl_payment_plan).setOnClickListener(this);//还款计划
        footerView.findViewById(R.id.ll_lend_record).setOnClickListener(this);//出借记录

        investmentTotalList = new ArrayList<InvestmentListEntity>();
        lv_detail_investment_list = (ExpandListView) footerView.findViewById(R.id.lv_investment_detail_list);
        adapter = new InvestmentListAdapter(MySafeInvestmentPaymentDetailActivity.this, investmentTotalList);
        lv_detail_investment_list.setAdapter(adapter);

        findViewById(R.id.tv_tip_product).setOnClickListener(this);//出借有风险提示语
        findViewById(R.id.tv_tip_safe_loan_record).setOnClickListener(this);
        findViewById(R.id.tv_tip_question).setOnClickListener(this);

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
            case R.id.rl_investment_detail_file:
                Intent fileIntent = new Intent(MySafeInvestmentPaymentDetailActivity.this,
                        InvestmentDetailFileActivity.class);
                //docimgsList,wgimgList,imgList
                //			if(isNewType.equals("2")||isNewType.equals("3")){//供应链
                //				fileIntent.putExtra("creditInfoId", creditInfoId);
                //			}else{
                fileIntent.putExtra("docimgsList", (Serializable) docimgsList);
                fileIntent.putExtra("wgimgList", (Serializable) wgimgList);
                fileIntent.putExtra("imgList", (Serializable) imgList);
                //			}
                //			fileIntent.putExtra("isNewType", isNewType);
                startActivity(fileIntent);
                break;
            case R.id.rl_payment_plan:
                Intent paymentIntent = new Intent(MySafeInvestmentPaymentDetailActivity.this,
                        PaymentPlanActivity.class);
                paymentIntent.putExtra("projectid", projectid);
                startActivity(paymentIntent);
                break;
            case R.id.ll_product_introduction://产品介绍
                tv_product_introduction.setTextColor(getResources().getColor(R.color.blue));
                tv_lend_record.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_common_problem.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                v_product_introduction_line.setVisibility(View.VISIBLE);
                v_lend_record_line.setVisibility(View.GONE);
                v_common_problem_line.setVisibility(View.GONE);

                ll_product_introduction_content.setVisibility(View.VISIBLE);
                ll_question_text.setVisibility(View.GONE);
                lend_list.setVisibility(View.GONE);

                break;
            case R.id.ll_lend_record://出借记录
                tv_product_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_lend_record.setTextColor(getResources().getColor(R.color.blue));
                tv_common_problem.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                v_product_introduction_line.setVisibility(View.GONE);
                v_lend_record_line.setVisibility(View.VISIBLE);
                v_common_problem_line.setVisibility(View.GONE);

                ll_product_introduction_content.setVisibility(View.GONE);
                ll_question_text.setVisibility(View.GONE);
                lend_list.setVisibility(View.VISIBLE);
                //			initData();
                getListData("3", 1, 300, projectid, REFRESH);
                //			getListData("3",0,0,projectid,REFRESH);
                //			lv_detail_investment_list.setMode(Mode.BOTH);
                break;
            case R.id.ll_common_problem://常见问题
                tv_product_introduction.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_lend_record.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_common_problem.setTextColor(getResources().getColor(R.color.blue));
                v_product_introduction_line.setVisibility(View.GONE);
                v_lend_record_line.setVisibility(View.GONE);
                v_common_problem_line.setVisibility(View.VISIBLE);

                ll_product_introduction_content.setVisibility(View.GONE);
                ll_question_text.setVisibility(View.VISIBLE);
                lend_list.setVisibility(View.GONE);
                break;
            case R.id.tv_tip_product:
            case R.id.tv_tip_safe_loan_record:
            case R.id.tv_tip_question:
                Intent tipIntent = new Intent(MySafeInvestmentPaymentDetailActivity.this, AgreementActivity.class);
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
                getPaymentList("3", LoginUserProvider.getUser(MySafeInvestmentPaymentDetailActivity.this).getToken(), bidid);
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
                            //								tv_use_of_funds.setText(dataObj.getString("purpose"));
                        }
                        if (dataObj.has("sourceOfRepayment")) {//还款来源

                            //								if(TextUtils.isEmpty(dataObj.getString("sourceOfRepayment"))||dataObj.getString("sourceOfRepayment").equals("null")){
                            //									tv_payment_source.setText("");
                            //								}else{
                            //									tv_payment_source.setText(dataObj.getString("sourceOfRepayment"));
                            //								}
                        }
                        if (dataObj.has("borrowerCompanyName")) {//借款方
                            //								tv_loan_party.setText(dataObj.getString("borrowerCompanyName"));//借款方
                            //								tv_loan.setText(dataObj.getString("borrowerCompanyName"));
                        }
                        if (dataObj.has("replaceRepayCompanyName")) {//付款方
                            //								tv_payment_party.setText(dataObj.getString("replaceRepayCompanyName"));//付款方
                            //								tv_payment.setText(dataObj.getString("replaceRepayCompanyName"));//付款方
                        }

                        //三证合一
                        //							if(dataObj.has("businessLicenses")){
                        //								JSONArray businessLicensesArray = dataObj.getJSONArray("businessLicenses");
                        //								loanFileImgList = new ArrayList<FileGridViewEntity>();
                        //								if(businessLicensesArray.length()>0){
                        //									for(int i = 0;i<businessLicensesArray.length();i++){
                        //										JSONObject obj = businessLicensesArray.getJSONObject(i);
                        //										FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                        //										fileGridViewEntity.setUrl(obj.getString("url"));
                        //										fileGridViewEntity.setName("三证合一");
                        //										loanFileImgList.add(fileGridViewEntity);
                        //									}
                        //								}
                        //							}

                        //开户许可证
                        //							if(dataObj.has("bankPermitCerts")){
                        //								JSONArray bankPermitCertsArray = dataObj.getJSONArray("bankPermitCerts");
                        //								if(bankPermitCertsArray.length()>0){
                        //									for(int i = 0;i<bankPermitCertsArray.length();i++){
                        //										JSONObject obj = bankPermitCertsArray.getJSONObject(i);
                        //										FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                        //										fileGridViewEntity.setUrl(obj.getString("url"));
                        //										fileGridViewEntity.setName("开户许可证");
                        //										loanFileImgList.add(fileGridViewEntity);
                        //									}
                        //								}
                        //								fileGridViewAdapter = new FileGridViewAdapter(MyInvestmentPaymentDetailActivity.this, loanFileImgList);
                        //								gv_loan.setAdapter(fileGridViewAdapter);
                        //							}

                        //						//承诺函
                        //							if(dataObj.has("commitments")){
                        //								JSONArray commitmentsArray = dataObj.getJSONArray("commitments");
                        //								paymentFileImgList = new ArrayList<FileGridViewEntity>();
                        //								if(commitmentsArray.length()>0){
                        //									for(int i = 0;i<commitmentsArray.length();i++){
                        //										JSONObject obj = commitmentsArray.getJSONObject(i);
                        //										FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                        //										fileGridViewEntity.setUrl(obj.getString("url"));
                        //										fileGridViewEntity.setName("承诺函");
                        //										paymentFileImgList.add(fileGridViewEntity);
                        //									}
                        //									fileGridViewAdapter = new FileGridViewAdapter(MyInvestmentPaymentDetailActivity.this, paymentFileImgList);
                        //									gv_payment.setAdapter(fileGridViewAdapter);
                        //								}
                        //							}


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
                            //								tv_loan_rate.setText(dataObj.getString("rate")+"%");
                        }
                        if (dataObj.has("repaytype")) {
                            tv_payment_mode.setText(dataObj.getString("repaytype"));
                            //								tv_payment_style.setText(dataObj.getString("repaytype"));
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
                            tv_product_introduction_content.setText(dataObj.getString("projectcase"));
                        }
                        if (dataObj.has("span")) {
                            tv_loan_span.setText(dataObj.getString("span") + "天");// 项目期限
                            //								tv_project_span.setText(dataObj.getString("span") + "天");// 项目期限
                        }
                        if (dataObj.has("creditInfoId")) {
                            //								creditInfoId = dataObj.getString("creditInfoId");//项目图片需要的id
                        }
                        if (dataObj.has("isNewType")) {
                            //							isNewType = dataObj.getString("isNewType");// isNewType,2供应链
                        }
                        //							if(dataObj.has("creditName")){
                        //								creditName = dataObj.getString("creditName");
                        //							}
                        //							if(dataObj.has("creditUrl")){
                        //								creditUrl = dataObj.getString("creditUrl");
                        //							}
                        //							if(dataObj.has("name")){
                        //								name = dataObj.getString("name");// 项目名字
                        //							}
                        //							if(dataObj.has("sn")){
                        //								sn = dataObj.getString("sn");
                        //							}
                        //							tv_project_name.setText(name+"("+sn+")");//项目名称


                        if (dataObj.has("amount")) {//项目金额
                            //								tv_project_amount.setText(decimalFormat.format(Double.parseDouble(dataObj.getString("amount")))+"元");//项目金额
                        }
                        //						tv_project_span.setText(dataObj.getString("span")+"天");//项目期限
                        //						tv_loan_rate.setText(rate+"%");//预借年化利率
                        //						tv_payment_style.setText(dataObj.getString("repaytype"));//还款方式
                        if (dataObj.has("loandate")) {
                            //								tv_interest_time.setText(dataObj.getString("loandate"));//起息时间
                        }

                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(
                                MySafeInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("")
                                && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(
                                    MySafeInvestmentPaymentDetailActivity.this,
                                    UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(
                                    MySafeInvestmentPaymentDetailActivity.this,
                                    LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil
                                .get(MySafeInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(MySafeInvestmentPaymentDetailActivity.this,
                                jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, "解析异常", 0)
                            .show();
                }

            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, "请检查网络", 0)
                        .show();
                CustomProgress.CustomDismis();
            }
        });
    }


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
                            MyInvestmentDetailPaymentAdapter paymentAdapter = new MyInvestmentDetailPaymentAdapter(MySafeInvestmentPaymentDetailActivity.this, paymentList);
                            lv_safe_payment_list.setAdapter(paymentAdapter);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MySafeInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MySafeInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MySafeInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MySafeInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
                //				CustomProgress.CustomDismis();
            }
        });
    }

    //获取数据
    private void getListData(String from, final int pageNo, int pageSize, String projectid, final int RequestCode) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(SafeInvestmentDetailActivity.this));
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

                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        CustomProgress.CustomDismis();
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
                            //							ListViewUtils util = new ListViewUtils();
                            //							util.setListViewHeightBasedOnChildren(lv_detail_investment_list);
                            setView(investmentList, RequestCode);
                            //							if(last == pageNo){
                            //								lv_detail_investment_list.onRefreshComplete();
                            //								lv_detail_investment_list.setMode(Mode.PULL_FROM_START);
                            //							}else{
                            //								lv_detail_investment_list.setMode(Mode.BOTH);
                            //							}
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MySafeInvestmentPaymentDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MySafeInvestmentPaymentDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MySafeInvestmentPaymentDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //									LoginUserProvider.cleanData(InvestmentListActivity.this);
                        //									LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(MySafeInvestmentPaymentDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }
                    //					lv_detail_investment_list.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //					lv_detail_investment_list.onRefreshComplete();
                    Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				lv_detail_investment_list.onRefreshComplete();
                Toast.makeText(MySafeInvestmentPaymentDetailActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    protected void setView(List<InvestmentListEntity> investmentList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            investmentTotalList.clear();
            if (investmentList != null && investmentList.size() > 0) {
                investmentTotalList.addAll(investmentList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            investmentTotalList.addAll(investmentList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();

    }
}
