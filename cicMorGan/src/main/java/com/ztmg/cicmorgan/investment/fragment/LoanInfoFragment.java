package com.ztmg.cicmorgan.investment.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BasePager;
import com.ztmg.cicmorgan.entity.InvestmentDetailEntity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.investment.activity.ShareholderInformationActivity;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.DateUtils;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.ToastUtils;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 借款方信息
 */
public class LoanInfoFragment extends BasePager implements View.OnClickListener {
    public InvestmentDetailBorrowActivity activity;
    private String projectid;
    public TextView tv_company_name;
    public TextView tv_registered_capital;
    public TextView tv_real_capital;
    public TextView tv_registered_address;
    public TextView tv_establish_time;
    public TextView tv_legal_people;
    public RelativeLayout rl_shareholder_message;
    public TextView tv_industry;
    public TextView tv_office_location;
    public TextView tv_business_area;
    public TextView tv_income_liabilities;
    public TextView tv_finance;
    public TextView tv_also_money;
    public TextView tv_if_involve;
    public TextView tv_if_punishment;
    public TextView tv_if_loan;
    public TextView tv_other;
    public InvestmentDetailEntity.DataBean data;
    public InvestmentDetailEntity.DataBean.ZtmgLoanBasicInfoEntityBean ztmgLoanBasicInfoEntity;

    public LoanInfoFragment(int index, Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("--------------------------3------------------------");
        View view = inflater.inflate(R.layout.fragment_ioaninfo_item, null);
        tv_company_name = (TextView) view.findViewById(R.id.tv_company_name);
        tv_registered_capital = (TextView) view.findViewById(R.id.tv_registered_capital);
        tv_real_capital = (TextView) view.findViewById(R.id.tv_real_capital);
        tv_registered_address = (TextView) view.findViewById(R.id.tv_registered_address);
        tv_establish_time = (TextView) view.findViewById(R.id.tv_establish_time);
        tv_legal_people = (TextView) view.findViewById(R.id.tv_legal_people);
        rl_shareholder_message = (RelativeLayout) view.findViewById(R.id.rl_shareholder_message);
        rl_shareholder_message.setOnClickListener(this);
        tv_industry = (TextView) view.findViewById(R.id.tv_industry);
        tv_office_location = (TextView) view.findViewById(R.id.tv_office_location);
        tv_business_area = (TextView) view.findViewById(R.id.tv_business_area);
        tv_income_liabilities = (TextView) view.findViewById(R.id.tv_income_liabilities);
        tv_finance = (TextView) view.findViewById(R.id.tv_finance);
        tv_also_money = (TextView) view.findViewById(R.id.tv_also_money);
        tv_if_involve = (TextView) view.findViewById(R.id.tv_if_involve);
        tv_if_punishment = (TextView) view.findViewById(R.id.tv_if_punishment);
        tv_if_loan = (TextView) view.findViewById(R.id.tv_if_loan);
        tv_other = (TextView) view.findViewById(R.id.tv_other);
        return view;
    }


    @Override
    public void initData(String string) {
        LogUtil.e("--------------------------4------------------------");
        projectid = string;
        String json = ACache.get(context).getAsString(Constant.InvestmentDetailKey);
        InvestmentDetailEntity investmentDetailEntity = GsonManager.fromJson(json, InvestmentDetailEntity.class);

        if (investmentDetailEntity.getState().equals("4")) {//系统超时
           /* String mGesture = LoginUserProvider.getUser(context).getGesturePwd();
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
            util.put("isLogin", "");*/

        } else if (investmentDetailEntity.getState().equals("0")) {
            data = investmentDetailEntity.getData();
            if (null != data.getZtmgLoanBasicInfoEntity()) {
                ztmgLoanBasicInfoEntity = data.getZtmgLoanBasicInfoEntity();
            }
            tv_company_name.setText(data.getBorrowerCompanyName() != null ? data.getBorrowerCompanyName() : "---");
            tv_registered_capital.setText(data.getZtmgLoanBasicInfoEntity().getRegisteredCapital() != null ? data.getZtmgLoanBasicInfoEntity().getRegisteredCapital() + "元" : "---");
            tv_registered_address.setText(data.getZtmgLoanBasicInfoEntity().getRegisteredAddress() != null ? data.getZtmgLoanBasicInfoEntity().getRegisteredAddress() : "---");

            tv_establish_time.setText(data.getZtmgLoanBasicInfoEntity().getSetUpTime() != null ? DateUtils.getDate2String3(DateUtils.getString2Date3(data.getZtmgLoanBasicInfoEntity().getSetUpTime())) : "---");
            tv_legal_people.setText(data.getZtmgLoanBasicInfoEntity().getOperName() != null ? data.getZtmgLoanBasicInfoEntity().getOperName() : "---");
            tv_office_location.setText(data.getAddress() != null ? data.getAddress() : "---");
            tv_finance.setText(data.getBusinessFinancialSituation() != null ? data.getBusinessFinancialSituation() : "---");
            tv_also_money.setText(data.getAbilityToRepaySituation() != null ? data.getAbilityToRepaySituation() : "---");
            tv_if_involve.setText(data.getLitigationSituation() != null ? data.getLitigationSituation() : "---");
            tv_if_punishment.setText(data.getAdministrativePunishmentSituation() != null ? data.getAdministrativePunishmentSituation() : "---");
            tv_if_loan.setText(data.getPlatformOverdueSituation() != null ? data.getPlatformOverdueSituation() : "---");
            tv_other.setText(data.getZtmgLoanBasicInfoEntity() != null ? data.getZtmgLoanBasicInfoEntity().getOtherCreditInformation() + "元" : "---");

            if (null == ztmgLoanBasicInfoEntity) {
                tv_real_capital.setText("---");
                tv_industry.setText("---");
                tv_business_area.setText("---");
                tv_income_liabilities.setText("---");
            } else {
                tv_real_capital.setText(ztmgLoanBasicInfoEntity.getContributedCapital() != null ? ztmgLoanBasicInfoEntity.getContributedCapital() + "元" : "---");
                tv_industry.setText(ztmgLoanBasicInfoEntity.getIndustry() != null ? ztmgLoanBasicInfoEntity.getIndustry() : "---");
                //String city = ztmgLoanBasicInfoEntity.getProvince() + ztmgLoanBasicInfoEntity.getCity();
                tv_business_area.setText(ztmgLoanBasicInfoEntity.getScope() != null ? ztmgLoanBasicInfoEntity.getScope() : "---");
                String s = "年收入：" + ztmgLoanBasicInfoEntity.getAnnualRevenue() + "元；" + "负债：" + ztmgLoanBasicInfoEntity.getLiabilities() + "元";
                tv_income_liabilities.setText(s);
            }
        } else {
            ToastUtils.show(context, investmentDetailEntity.getMessage());
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_shareholder_message://股东信息
                //埋点判断
                if (!StringUtils.isBlank(ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
                    String s = ACache.get(context).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
                    if (s.equals("1") || s.equals("2")) {
                        onEvent(context, "202006_project_gdxx_click");
                    } else if (s.equals("3")) {
                        onEvent(context, "202014_project_gdxx_cj_click");
                    }
                }
                Intent intent = new Intent(context, ShareholderInformationActivity.class);
                intent.putExtra("projectid", projectid);
                //intent.putExtra("loanDate", totalSupplyChainList.get(position - 1).getLoanDate());
                context.startActivity(intent);
                break;
        }

    }
}
