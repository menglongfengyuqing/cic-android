package com.ztmg.cicmorgan.account.activity;

import java.text.DecimalFormat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CircleScaleView;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 账户总览
 *
 * @author pc
 */
public class AccountSumActivity extends BaseActivity implements OnClickListener {

    private TextView tv_account_sum, tv_available_amount, tv_freeze_amount, tv_regular_total_amount, tv_regular_total_interest, tv_regular_due_principal,
            tv_regular_due_interest, tv_cash_amount, tv_recharge_amount;
    private String isBindBank;

    private CircleScaleView mScaleView;
    private ImageView iv_account_no_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_account_sum);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(AccountSumActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        if (LoginUserProvider.getUser(AccountSumActivity.this) != null) {
            isBindBank = LoginUserProvider.getUser(AccountSumActivity.this).getIsBindBank();//1未绑定2已绑定
        }
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(AccountSumActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
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

    @Override
    protected void initView() {
        tv_account_sum = (TextView) findViewById(R.id.tv_account_sum);
        tv_available_amount = (TextView) findViewById(R.id.tv_available_amount);
        tv_freeze_amount = (TextView) findViewById(R.id.tv_freeze_amount);
        tv_regular_total_amount = (TextView) findViewById(R.id.tv_regular_total_amount);
        tv_regular_total_interest = (TextView) findViewById(R.id.tv_regular_total_interest);
        tv_regular_due_principal = (TextView) findViewById(R.id.tv_regular_due_principal);
        tv_regular_due_interest = (TextView) findViewById(R.id.tv_regular_due_interest);
        tv_cash_amount = (TextView) findViewById(R.id.tv_cash_amount);
        tv_recharge_amount = (TextView) findViewById(R.id.tv_recharge_amount);
        setTitle("账户总览");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(AccountSumActivity.this, "204001_zichan_back_click");
                finish();
            }
        });

        mScaleView = (CircleScaleView) findViewById(R.id.cs_my_scale);
        iv_account_no_money = (ImageView) findViewById(R.id.iv_account_no_money);
    }

    @Override
    protected void initData() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
        String totalAmountDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getTotalAmount()));
        tv_account_sum.setText(totalAmountDot);
        String availableAmountDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getAvailableAmount()));
        tv_available_amount.setText(availableAmountDot);
        String freezeAmountDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getFreezeAmount()));
        tv_freeze_amount.setText(freezeAmountDot);
        String regularTotalAmountDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getRegularTotalAmount()));
        tv_regular_total_amount.setText(regularTotalAmountDot);
        String totalInterestDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getRegularTotalInterest()));
        tv_regular_total_interest.setText(totalInterestDot);
        String regularDuePrincipalDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getRegularDuePrincipal()));
        tv_regular_due_principal.setText(regularDuePrincipalDot);
        String regularDueInterestDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getRegularDueInterest()));
        tv_regular_due_interest.setText(regularDueInterestDot);
        String cashAmountDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getCashAmount()));
        tv_cash_amount.setText(cashAmountDot);
        String rechargeAmountDot = decimalFormat.format(Double.parseDouble(LoginUserProvider.getUserAccountInfo(AccountSumActivity.this).getRechargeAmount()));
        tv_recharge_amount.setText(rechargeAmountDot);

        if (totalAmountDot.equals("0.00")) {
            iv_account_no_money.setVisibility(View.VISIBLE);
            mScaleView.setVisibility(View.GONE);
        } else {
            iv_account_no_money.setVisibility(View.GONE);
            mScaleView.setVisibility(View.VISIBLE);
            //可用余额 冻结金额 待收收益 待收本金
            mScaleView.setCostPercent(Float.parseFloat(availableAmountDot), Float.parseFloat(freezeAmountDot), Float.parseFloat(regularDueInterestDot), Float.parseFloat(regularDuePrincipalDot));
            //		mScaleView.setCostPercent(333333.00f,1000.00f,333333.00f,332334.00f);
        }
    }

    @Override
    public void onClick(View v) {

    }

    // 没有绑定银行卡提示框
    private void dialog() {

        final Dialog mdialog = new Dialog(AccountSumActivity.this, R.style.MyDialog);
        mdialog.setContentView(R.layout.dl_isbindbank);

        TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) mdialog.findViewById(R.id.tv_no);
        ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
        tv_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到绑定银行卡界面
                Intent intent = new Intent(AccountSumActivity.this, BindBankCardActivity.class);
                intent.putExtra("isBackAccount", "1");
                startActivity(intent);
                mdialog.dismiss();
            }
        });
        tv_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //不绑定银行卡，关闭弹框
                mdialog.dismiss();
            }
        });
        iv_dialog_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

}
