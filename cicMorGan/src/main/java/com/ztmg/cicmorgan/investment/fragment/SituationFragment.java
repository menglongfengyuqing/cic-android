package com.ztmg.cicmorgan.investment.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.FullScreenImageActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BasePager;
import com.ztmg.cicmorgan.entity.InvestmentDetailEntity;
import com.ztmg.cicmorgan.entity.SituationEntity;
import com.ztmg.cicmorgan.investment.activity.MyGridView;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.investment.adapter.FileGridViewAdapter;
import com.ztmg.cicmorgan.investment.entity.FileGridViewEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * dong
 * 2018年5月30日
 * 风控情况
 */
public class SituationFragment extends BasePager implements View.OnClickListener {
    public InvestmentDetailBorrowActivity activity;
    private int index;
    private SituationEntity situationEntity;
    private List<FileGridViewEntity> loanFileImgList;//借款方
    // private List<FileGridViewEntity> paymentFileImgList = new ArrayList<>();//付款方
    private List<FileGridViewEntity> relevantFileImgList;//相关文件
    private List<FileGridViewEntity> relevantFileImgList_6;//相关文件 先存储六个
    private TextView tv_payment, tv_loan, tv_repayment, tv_wind_control_measures, tv_repayment_source;
    private MyGridView gv_loan, gv_relevant_file;
    private FileGridViewAdapter fileGridViewAdapter;
    private String projectProductType;
    private FileGridViewEntity fileGridViewEntity;
    private LinearLayout ll_see_more;
    private View view1;
    private RelativeLayout rl_payment;
    private int mInt = 1;

    public SituationFragment(int index, Context context) {
        super(context);
        this.index = index;
    }

    @Override
    public View initView() {
        LogUtil.e("--------------------------5------------------------");
        View view = inflater.inflate(R.layout.fragment_situation_item, null);
        tv_payment = (TextView) view.findViewById(R.id.tv_payment);
        rl_payment = (RelativeLayout) view.findViewById(R.id.rl_payment);
        tv_loan = (TextView) view.findViewById(R.id.tv_loan);
        view1 = view.findViewById(R.id.view0);

        tv_wind_control_measures = (TextView) view.findViewById(R.id.tv_wind_control_measures);
        tv_repayment = (TextView) view.findViewById(R.id.tv_repayment);
        tv_repayment_source = (TextView) view.findViewById(R.id.tv_repayment_source);
        ll_see_more = (LinearLayout) view.findViewById(R.id.ll_see_more);
        ll_see_more.setOnClickListener(this);


        //gv_loan = (MyGridView) view.findViewById(R.id.gv_loan);
        //gv_payment = (MyGridView) view.findViewById(R.id.gv_payment);
        //gv_relevant_file = (MyGridView) view.findViewById(R.id.gv_relevant_file);

        //借款方
        gv_loan = (MyGridView) view.findViewById(R.id.gv_loan);
        gv_loan.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                List<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < loanFileImgList.size(); i++) {
                    imageUrls.add(loanFileImgList.get(i).getUrl());
                }
                intent.putExtra("currentPosition", position);
                intent.putStringArrayListExtra("urls", (ArrayList<String>) imageUrls);
                intent.setClass(context, FullScreenImageActivity.class);
                context.startActivity(intent);
            }
        });

        //        //付款方
        //        gv_payment = (MyGridView) view.findViewById(R.id.gv_payment);
        //        gv_payment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //
        //            @Override
        //            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
        //                                    long arg3) {
        //                Intent intent = new Intent(context, FullScreenImageActivity.class);
        //                List<String> imageUrls = new ArrayList<String>();
        //                for (int i = 0; i < paymentFileImgList.size(); i++) {
        //                    imageUrls.add(paymentFileImgList.get(i).getUrl());
        //                }
        //                intent.putExtra("currentPosition", position);
        //                intent.putStringArrayListExtra("urls", (ArrayList<String>) imageUrls);
        //                intent.setClass(context, FullScreenImageActivity.class);
        //                context.startActivity(intent);
        //            }
        //        });

        //相关文件
        gv_relevant_file = (MyGridView) view.findViewById(R.id.gv_relevant_file);
        gv_relevant_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                List<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < relevantFileImgList.size(); i++) {
                    imageUrls.add(relevantFileImgList.get(i).getUrl());
                }
                intent.putExtra("currentPosition", position);
                intent.putStringArrayListExtra("urls", (ArrayList<String>) imageUrls);
                intent.setClass(context, FullScreenImageActivity.class);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData(String string) {
        LogUtil.e("--------------------------6------------------------");
        if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.InvestmentDetailKey))) {
            String json = ACache.get(context).getAsString(Constant.InvestmentDetailKey);
            InvestmentDetailEntity investmentDetailEntity = GsonManager.fromJson(json, InvestmentDetailEntity.class);
            InvestmentDetailEntity.DataBean dataBean = investmentDetailEntity.getData();
            projectProductType = dataBean.getProjectProductType();
            tv_repayment_source.setText(dataBean.getSourceOfRepayment());
            tv_repayment.setText(dataBean.getRepaymentGuaranteeMeasures());

        }
        loanFileImgList = new ArrayList<>();
        relevantFileImgList = new ArrayList<>();
        relevantFileImgList_6 = new ArrayList<>();

        loanFileImgList.clear();
        relevantFileImgList.clear();
        relevantFileImgList_6.clear();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETPROJECTINFOANNEX;
        RequestParams params = new RequestParams();
        params.put("from", 3);
        //params.put("projectid", "2bc809ed5c9f411aa5752245e6879857");
        params.put("projectid", string);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //rl_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                LogUtil.e("=============风控返回 ================" + "  \n  " + result);
                situationEntity = GsonManager.fromJson(result, SituationEntity.class);
                if (situationEntity.getState().equals("4")) {//系统超时
                    String mGesture = LoginUserProvider.getUser(context).getGesturePwd();
                    if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                        //设置手势密码
                        Intent intent = new Intent(context, UnlockGesturePasswordActivity.class);
                        intent.putExtra("overtime", "0");
                        context.startActivity(intent);
                    } else {
                        //未设置手势密码
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("overtime", "0");
                        context.startActivity(intent);
                    }
                    DoCacheUtil util = DoCacheUtil.get(context);
                    util.put("isLogin", "");
                } else if (situationEntity.getState().equals("0")) {
                    SituationEntity.DataBean data = situationEntity.getData();
                    tv_wind_control_measures.setText(data.getGuaranteescheme());
                    tv_payment.setText(data.getCreditName());//付款方
                    //判断是否显示付款方
                    if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                        String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                        if (s.equals("1")) {
                            rl_payment.setVisibility(View.GONE);
                            view1.setVisibility(View.GONE);
                        } else if (s.equals("3")) {
                            if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                                String chujie = ACache.get(context).getAsString(Constant.SupplyChainInvestmentKey_mine_chujie);
                                if (chujie.equals("1")) {
                                    rl_payment.setVisibility(View.GONE);
                                    view1.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    tv_loan.setText(data.getBorrowerCompanyName());//借款方
                    //                    //三证合一
                    //                    if (Lists.notEmpty(data.getBusinessLicenses())) {
                    //                        for (int i = 0; i < data.getBusinessLicenses().size(); i++) {
                    //                            SituationEntity.DataBean.BusinessLicensesBean businessLicensesBean = data.getBusinessLicenses().get(i);
                    //                            FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                    //                            fileGridViewEntity.setUrl(businessLicensesBean.getUrl());
                    //                            fileGridViewEntity.setName("三证合一");
                    //                            loanFileImgList.add(fileGridViewEntity);
                    //                        }
                    //                    }
                    //
                    //                    if (Lists.notEmpty(data.getBankPermitCerts())) {//开户许可证
                    //                        for (int i = 0; i < data.getBankPermitCerts().size(); i++) {
                    //                            SituationEntity.DataBean.BankPermitCertsBean bankPermitCertsBean = data.getBankPermitCerts().get(i);
                    //                            FileGridViewEntity fileGridViewEntity = new FileGridViewEntity();
                    //                            fileGridViewEntity.setUrl(bankPermitCertsBean.getUrl());
                    //                            fileGridViewEntity.setName("开户许可证");
                    //                            loanFileImgList.add(fileGridViewEntity);
                    //                        }
                    //                        fileGridViewAdapter = new FileGridViewAdapter(context, loanFileImgList);
                    //                        gv_loan.setAdapter(fileGridViewAdapter);
                    //                    }
                    //                    if (Lists.isEmpty(loanFileImgList))//如果为空就不显示 gv_loan 这个布局
                    //                        gv_loan.setVisibility(View.GONE);

                    if (projectProductType.equals("2")) {//供应链
                        if (Lists.notEmpty(data.getCommitments())) {
                            for (int i = 0; i < data.getCommitments().size(); i++) {
                                SituationEntity.DataBean.CommitmentsBean commitmentsBean = data.getCommitments().get(i);
                                fileGridViewEntity = new FileGridViewEntity();
                                if (commitmentsBean.getType().equals("5")) {
                                    fileGridViewEntity.setUrl(commitmentsBean.getUrl());
                                    fileGridViewEntity.setName("项目文件");
                                    relevantFileImgList.add(fileGridViewEntity);
                                } else if (commitmentsBean.getType().equals("7")) {
                                    fileGridViewEntity.setUrl(commitmentsBean.getUrl());
                                    fileGridViewEntity.setName("风控文件");
                                    relevantFileImgList.add(fileGridViewEntity);
                                } else {
                                    fileGridViewEntity.setUrl(commitmentsBean.getUrl());
                                    fileGridViewEntity.setName("贸易背景");
                                    relevantFileImgList.add(fileGridViewEntity);
                                }
                            }
                            RelevantFileImgList_6();
                        }
                    } else if (projectProductType.equals("1")) {//安心投
                        //relevantFileImgList = new ArrayList<>();
                        if (Lists.notEmpty(data.getProimg())) {
                            for (int i = 0; i < data.getProimg().size(); i++) {
                                fileGridViewEntity = new FileGridViewEntity();
                                String s = data.getProimg().get(i);
                                fileGridViewEntity.setUrl(s);
                                fileGridViewEntity.setName("项目文件");
                                relevantFileImgList.add(fileGridViewEntity);
                            }
                        }
                        if (Lists.notEmpty(data.getWgimglist())) {
                            for (int i = 0; i < data.getWgimglist().size(); i++) {
                                fileGridViewEntity = new FileGridViewEntity();
                                String s = data.getWgimglist().get(i);
                                fileGridViewEntity.setUrl(s);
                                //fileGridViewEntity = new FileGridViewEntity();
                                fileGridViewEntity.setName("贸易背景");
                                relevantFileImgList.add(fileGridViewEntity);
                            }
                        }
                        if (Lists.notEmpty(data.getDocimgs())) {
                            for (int i = 0; i < data.getDocimgs().size(); i++) {
                                fileGridViewEntity = new FileGridViewEntity();
                                String s = data.getDocimgs().get(i);
                                fileGridViewEntity.setUrl(s);
                                fileGridViewEntity.setName("风控文件");
                                relevantFileImgList.add(fileGridViewEntity);
                            }
                        }
                        RelevantFileImgList_6();
                    }
                } else {
                    ToastUtils.show(context, situationEntity.getMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(NewInvestmentDetailActivity.this, "请检查网络", 0).show();
                ToastUtils.show(context, "请检查网络");
                CustomProgress.CustomDismis();
            }
        });
    }

    //判断当前图片的集合是否是六张
    private void RelevantFileImgList_6() {
        if (Lists.notEmpty(relevantFileImgList)) {
            if (relevantFileImgList.size() <= 5) {
                fileGridViewAdapter = new FileGridViewAdapter(context, relevantFileImgList);
                ll_see_more.setVisibility(View.GONE);
            } else if (relevantFileImgList.size() > 5) {
                relevantFileImgList_6.addAll(relevantFileImgList.subList(0, 6));//可以先按下标截取，再添加
                fileGridViewAdapter = new FileGridViewAdapter(context, relevantFileImgList_6);
                ll_see_more.setVisibility(View.VISIBLE);
            }
            gv_relevant_file.setAdapter(fileGridViewAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_see_more://查看更多
                if (Lists.notEmpty(relevantFileImgList)) {
                    ++mInt;//点击的次数
                    if (relevantFileImgList.size() > 6 * mInt) {
                        relevantFileImgList_6.clear();
                        relevantFileImgList_6.addAll(relevantFileImgList.subList(0, (6 * mInt)));//可以先按下标截取，再添加
                        //fileGridViewAdapter = new FileGridViewAdapter(context, relevantFileImgList_6);
                        fileGridViewAdapter.upData(relevantFileImgList_6);
                        ll_see_more.setVisibility(View.VISIBLE);
                    } else {
                        fileGridViewAdapter.upData(relevantFileImgList);
                        ll_see_more.setVisibility(View.GONE);
                        relevantFileImgList_6.clear();
                    }
                }
                break;
        }
    }
}