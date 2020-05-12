package com.ztmg.cicmorgan.investment.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.SecuritySettingActivity;
import com.ztmg.cicmorgan.base.BasePager;
import com.ztmg.cicmorgan.entity.InvestmentDetailEntity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.CommonProblemActivity;
import com.ztmg.cicmorgan.investment.activity.GradeDescriptionActivity;
import com.ztmg.cicmorgan.investment.activity.PaymentPlanActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.view.StarBarView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;


/**
 * 项目介绍
 */
public class IntroduceFragment extends BasePager implements View.OnClickListener {
    public TextView tv_project_name;
    public TextView tv_loan_party;
    public TextView tv_payment;
    public TextView tv_project_amount;
    public TextView tv_project_span;
    public TextView tv_project_use;
    public TextView tv_loan_rate;
    public TextView tv_payment_style;
    public TextView tv_cost_instructions;
    public TextView tv_loan_rules;
    public TextView tv_loan_people_condition;
    public TextView tv_risk_tip_first;
    public TextView tv_risk_tip_second;
    public TextView tv_product_contents;
    public RelativeLayout rl_protocol, re_payment;
    public View view_line;
    public RelativeLayout rl_repayment_plan, mRelativeLayout_problem, mStarBar_RelativeLayout, rl_also;
    private String projectid;
    private View view;


    private int index;
    private InvestmentDetailEntity.DataBean dataJson;
    @BindView(R.id.tv_StarBarView)
    StarBarView tv_StarBarView;
    @BindView(R.id.mStarBar_textView)
    TextView mStarBar_textView;

    public IntroduceFragment(int index, Context context) {
        super(context);
        this.index = index;
    }

    @Override
    public View initView() {
        LogUtil.e("--------------------------1------------------------");
        View rootView = inflater.inflate(R.layout.fragment_introduce_item, null);
        ButterKnife.bind(this, rootView);
        tv_project_name = (TextView) rootView.findViewById(R.id.tv_project_name);
        tv_loan_party = (TextView) rootView.findViewById(R.id.tv_loan_party);
        tv_payment = (TextView) rootView.findViewById(R.id.tv_payment);
        tv_payment.setOnClickListener(this);
        tv_project_amount = (TextView) rootView.findViewById(R.id.tv_project_amount);
        tv_project_span = (TextView) rootView.findViewById(R.id.tv_project_span);
        tv_project_use = (TextView) rootView.findViewById(R.id.tv_project_use);
        tv_loan_rate = (TextView) rootView.findViewById(R.id.tv_loan_rate);
        tv_payment_style = (TextView) rootView.findViewById(R.id.tv_payment_style);
        tv_cost_instructions = (TextView) rootView.findViewById(R.id.tv_cost_instructions);
        tv_loan_rules = (TextView) rootView.findViewById(R.id.tv_loan_rules);
        tv_loan_people_condition = (TextView) rootView.findViewById(R.id.tv_loan_people_condition);
        tv_risk_tip_first = (TextView) rootView.findViewById(R.id.tv_risk_tip_first);
        tv_risk_tip_first.setOnClickListener(this);
        tv_risk_tip_second = (TextView) rootView.findViewById(R.id.tv_risk_tip_second);
        tv_risk_tip_second.setOnClickListener(this);
        re_payment = (RelativeLayout) rootView.findViewById(R.id.re_payment);//供应链有付款方,安心投没有付款方
        mStarBar_RelativeLayout = (RelativeLayout) rootView.findViewById(R.id.mStarBar_RelativeLayout);
        mStarBar_RelativeLayout.setOnClickListener(this);
        view_line = rootView.findViewById(R.id.view_line);
        rl_protocol = (RelativeLayout) rootView.findViewById(R.id.rl_protocol);
        rl_protocol.setOnClickListener(this);
        view = rootView.findViewById(R.id.view);
        rl_also = (RelativeLayout) rootView.findViewById(R.id.rl_also);
        rl_also.setOnClickListener(this);
        rl_repayment_plan = (RelativeLayout) rootView.findViewById(R.id.rl_repayment_plan);
        rl_repayment_plan.setOnClickListener(this);
        mRelativeLayout_problem = (RelativeLayout) rootView.findViewById(R.id.mRelativeLayout_problem);
        mRelativeLayout_problem.setOnClickListener(this);
        tv_product_contents = (TextView) rootView.findViewById(R.id.tv_product_contents);

        //initWebView(rootView);
        return rootView;
    }

    @Override
    public void initData(String string) {
        LogUtil.e("--------------------------2------------------------");
        projectid = string;
        String json = ACache.get(context).getAsString(Constant.InvestmentDetailKey);
        InvestmentDetailEntity investmentDetailEntity = GsonManager.fromJson(json, InvestmentDetailEntity.class);
        dataJson = investmentDetailEntity.getData();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
        // tv_percent_num.setText(decimalFormat.format(Double.parseDouble(dataJson.getRate())));
        tv_project_name.setText(dataJson.getProjectName());
        tv_loan_party.setText(dataJson.getBorrowerCompanyName());
        tv_payment.setText(dataJson.getReplaceRepayCompanyName());
        tv_project_amount.setText(decimalFormat.format(Double.parseDouble(dataJson.getAmount())) + "元");
        tv_project_span.setText(dataJson.getSpan() + "天");
        tv_project_use.setText(dataJson.getPurpose());

        if (!decimalFormat.format(Double.parseDouble(dataJson.getInterestRateIncrease())).equals("0.00")) {
            //得到基本利率 =  总利率-加息利率
            Double basic = Double.parseDouble(dataJson.getRate()) - Double.parseDouble(dataJson.getInterestRateIncrease());
            String rateDot = decimalFormat.format(basic);
            String interestRateIncrease = decimalFormat.format(Double.parseDouble(dataJson.getInterestRateIncrease()));
            tv_loan_rate.setText(rateDot + "%+" + interestRateIncrease + "%");
        } else {
            tv_loan_rate.setText(decimalFormat.format(Double.parseDouble(dataJson.getRate())) + "%");
        }
        tv_payment_style.setText(dataJson.getRepaytype());
        tv_product_contents.setText(dataJson.getProjectcase());
        StarBar();
        //TextView tv=(TextView)findViewById(R.id.tv); 风险承受能力“稳健型”及以上
        String str = "风险承受能力<font color='#A11C3F'>“稳健型”</font>及以上";
        tv_loan_people_condition.setText(Html.fromHtml(str));


        LogUtil.e("=====================" + ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key));
        //判断是否显示付款方
        if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
            String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
            if (s.equals("1")) {
                re_payment.setVisibility(View.GONE);
                view_line.setVisibility(View.GONE);
            } else if (s.equals("3")) {
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String chujie = ACache.get(context).getAsString(Constant.SupplyChainInvestmentKey_mine_chujie);
                    if (chujie.equals("1")) {
                        re_payment.setVisibility(View.GONE);
                        view_line.setVisibility(View.GONE);
                    }
                }
            }
        }

    }

    /**
     * 判断星星和还款计划
     */
    private void StarBar() {
        //根据出借供应链、安心投
        if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
            //只能在我的出借里面才能查看是否显示还款计划
            String proState = dataJson.getProState();
            if (proState.equals("3")) {
                rl_repayment_plan.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            } else if (proState.equals("4")) {
                rl_repayment_plan.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            } else if (proState.equals("5")) {
                rl_repayment_plan.setVisibility(View.VISIBLE);
            } else if (proState.equals("6")) {
                rl_repayment_plan.setVisibility(View.VISIBLE);
            } else if (proState.equals("7")) {
                rl_repayment_plan.setVisibility(View.VISIBLE);
            }
            String str = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
            if (str.equals("2")) {//供应链
                setStarBar();
            } else if (str.equals("1")) {
                //星星的显示
                tv_StarBarView.setStarMark(4);
                tv_StarBarView.setIndicator(true);
                mStarBar_textView.setText("较低风险" + "(内部评级，仅供参考)");
            } else if (str.equals("3")) {
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.SupplyChainInvestmentKey_mine_chujie))) {
                    String chujie = ACache.get(context).getAsString(Constant.SupplyChainInvestmentKey_mine_chujie);
                    if (chujie.equals("1")) {
                        //星星的显示
                        tv_StarBarView.setStarMark(4);
                        tv_StarBarView.setIndicator(true);
                        mStarBar_textView.setText("较低风险" + "(内部评级，仅供参考)");
                    } else if (chujie.equals("0")) {
                        setStarBar();
                    }
                }
            }
        }
    }

    //设置星星是几个
    private void setStarBar() {
        //星标的状态
        String level = dataJson.getLevel();
        if (Double.parseDouble(level) >= 5) {
            mStarBar_textView.setText("极低风险" + "(内部评级，仅供参考)");
        } else if (Double.parseDouble(level) == 4.5) {
            mStarBar_textView.setText("低风险" + "(内部评级，仅供参考)");
        } else if (Double.parseDouble(level) == 4) {
            mStarBar_textView.setText("较低风险" + "(内部评级，仅供参考)");
        } else if (Double.parseDouble(level) == 3.5) {
            mStarBar_textView.setText("中等风险" + "(内部评级，仅供参考)");
        } else if (Double.parseDouble(level) >= 3) {
            mStarBar_textView.setText("较高风险" + "(内部评级，仅供参考)");
        } else if (Double.parseDouble(level) <= 2.5) {
            mStarBar_textView.setText("高风险" + "(内部评级，仅供参考)");
        }
        //星星的显示
        tv_StarBarView.setStarMark(Float.parseFloat(dataJson.getLevel()));
        tv_StarBarView.setIndicator(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mRelativeLayout_problem://常见问题
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202004_project_cjwt_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202012_project_cjwt_cj_click");
                    }
                }
                Intent intent = new Intent(context, CommonProblemActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_repayment_plan://还款计划
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202003_project_huankuan_jihua_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202011_project_huankuan_cj_jihua_click");
                    }
                }
                Intent payIntent = new Intent(context, PaymentPlanActivity.class);
                payIntent.putExtra("projectid", projectid);
                context.startActivity(payIntent);
                break;
            case R.id.rl_protocol://协议范本
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202002_project_xyfb_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202010_project_xyfb_cj_click");
                    }
                }
                Intent agreeageIntent = new Intent(context, AgreementActivity.class);
                //path = "http://cicmorgan.com/invest_mine_agreement_scf.html";
                //判断协议范本H5地址
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("2")) {
                        agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENTSCF);//供应链
                    } else if (s.equals("1")) {
                        agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENT);//安心投
                    } else if (s.equals("3")) {
                        if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.MyInvestmentKey))) {
                            String myinvestment = ACache.get(context).getAsString(Constant.MyInvestmentKey);
                            if (myinvestment.equals("1")) {
                                agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENTSCF);//我的出借供应链
                            } else if (myinvestment.equals("2")) {
                                agreeageIntent.putExtra("path", Urls.INVESTMINEAGREEMENT);//我的出借安心投
                            }
                        }
                    }
                }
                context.startActivity(agreeageIntent);
                break;
            case R.id.tv_risk_tip_first://风险提示书
                Intent riskTipFirstIntent = new Intent(context, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "出借人网络借贷风险提示");
                context.startActivity(riskTipFirstIntent);
                break;
            case R.id.tv_risk_tip_second://网络借贷平台禁止性行为提示书
                Intent riskTipSecondIntent = new Intent(context, AgreementActivity.class);
                riskTipSecondIntent.putExtra("path", Urls.ZTPROTOCOLPROHIBIT);
                riskTipSecondIntent.putExtra("title", "出借人网络借贷禁止性行为提示");
                context.startActivity(riskTipSecondIntent);
                break;
            case R.id.tv_payment://付款方  核心企业介绍
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202005_project_hxqy_jieshao_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202013_project_hxqy_jieshao_cj_click");
                    }
                }
                if (!StringUtils.isEmpty(dataJson.getCreditUrl())) {
                    // if (!TextUtils.isEmpty(dataJson.getCreditUrl()) && !dataJson.getCreditUrl().equals("null") && creditUrl != null) {
                    Intent mthLogoIntent = new Intent(context, AgreementActivity.class);
                    mthLogoIntent.putExtra("path", dataJson.getCreditUrl());
                    mthLogoIntent.putExtra("title", dataJson.getCreditName());
                    context.startActivity(mthLogoIntent);
                }
                break;

            case R.id.mStarBar_RelativeLayout:
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202016_project_ljcj_tab_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202015_project_xmpj_cj_click");
                    }
                }
                Intent starbar = new Intent(context, GradeDescriptionActivity.class);
                context.startActivity(starbar);
                break;
            case R.id.rl_also:
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202018_project_hkfs_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202017_project_hkfs_cj_click");
                    }
                }
                dialog();
                break;
            default:
                break;


        }
    }

    // 还款方式
    private void dialog() {
        Dialog mdialog = new Dialog(context, R.style.MyDialog);
        mdialog.setContentView(R.layout.dialog_calculation_manner);
        mdialog.setCanceledOnTouchOutside(true);
        TextView text_manner = (TextView) mdialog.findViewById(R.id.text_manner);
        String content = "计算方式为：<br />借款金额为X，年利率为Y，借款期限为Z月,<br />则<font color='#d40f42'>每月应还利息</font>计算公式为： <font color='#d40f42'>X×Y/12</font>,<br />应还<font color='#d40f42'>总利息</font>计算公式为：<font color='#d40f42'>X×Y/12×Z</font>,<br />应还<font color='#d40f42'>本金</font>为<font color='#d40f42'>X</font>。";

        //String content = "dddddddddddddd\nddddddddddddddddddddddddddddddddddddddddddddddddddddddddd\nddddddddddddddddddddddddddddddddddddddddddd";
        text_manner.setText(Html.fromHtml(content));
        mdialog.show();
    }
}
