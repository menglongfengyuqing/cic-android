package com.ztmg.cicmorgan.account.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.fragment.AnsactionRecordsAllFragment;
import com.ztmg.cicmorgan.account.fragment.BackMoneyFragment;
import com.ztmg.cicmorgan.account.fragment.CashFragment;
import com.ztmg.cicmorgan.account.fragment.InvestmentFragment;
import com.ztmg.cicmorgan.account.fragment.RechargeFragment;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 交易记录
 *
 * @author pc
 */
public class AnsactionRecordsActivity extends BaseActivity implements OnClickListener {

    private TextView tv_all, tv_recharge, tv_cash, tv_investment, tv_back_money;
    private View v_all_line, v_recharge_line, v_cash_line, v_investment_line, v_back_money_line;
    private RelativeLayout rl_all, rl_recharge, rl_cash, rl_investment, rl_back_money;
    private AnsactionRecordsAllFragment mAnsactionRecordsAllFragment;//全部
    private RechargeFragment mRechargeFragment;//充值
    private CashFragment mCashFragment;//提现
    private InvestmentFragment mInvestmentFragment;//出借
    private BackMoneyFragment mBackMoneyFragment;//还款
    private FragmentTransaction Ft_all, Ft_recharge, Ft_cash, Ft_investment, Ft_backmoney;
    private static AnsactionRecordsActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_ansaction_records);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(AnsactionRecordsActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        mContext = this;
        mAnsactionRecordsAllFragment = new AnsactionRecordsAllFragment();
        mRechargeFragment = new RechargeFragment();
        mCashFragment = new CashFragment();
        mInvestmentFragment = new InvestmentFragment();
        mBackMoneyFragment = new BackMoneyFragment();

        Ft_all = getSupportFragmentManager().beginTransaction();
        Ft_all.replace(R.id.fl_content, mAnsactionRecordsAllFragment);
        Ft_all.commit();
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(AnsactionRecordsActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    public static AnsactionRecordsActivity getInstance() {
        return mContext;
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
        rl_all = (RelativeLayout) findViewById(R.id.rl_all);
        rl_recharge = (RelativeLayout) findViewById(R.id.rl_recharge);
        rl_cash = (RelativeLayout) findViewById(R.id.rl_cash);
        rl_investment = (RelativeLayout) findViewById(R.id.rl_investment);
        rl_back_money = (RelativeLayout) findViewById(R.id.rl_back_money);

        tv_all = (TextView) findViewById(R.id.tv_all);
        tv_recharge = (TextView) findViewById(R.id.tv_recharge);
        tv_cash = (TextView) findViewById(R.id.tv_cash);
        tv_investment = (TextView) findViewById(R.id.tv_investment);
        tv_back_money = (TextView) findViewById(R.id.tv_back_money);

        v_all_line = findViewById(R.id.v_all_line);
        v_recharge_line = findViewById(R.id.v_recharge_line);
        v_cash_line = findViewById(R.id.v_cash_line);
        v_investment_line = findViewById(R.id.v_investment_line);
        v_back_money_line = findViewById(R.id.v_back_money_line);

        setTitle("交易记录");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(AnsactionRecordsActivity.this, "210001_jyjl_back_click");
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        v_all_line.setVisibility(View.VISIBLE);
        v_recharge_line.setVisibility(View.GONE);
        v_cash_line.setVisibility(View.GONE);
        v_investment_line.setVisibility(View.GONE);
        v_back_money_line.setVisibility(View.GONE);

        rl_all.setOnClickListener(this);
        rl_recharge.setOnClickListener(this);
        rl_cash.setOnClickListener(this);
        rl_investment.setOnClickListener(this);
        rl_back_money.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_all:
                v_all_line.setVisibility(View.VISIBLE);
                v_recharge_line.setVisibility(View.GONE);
                v_cash_line.setVisibility(View.GONE);
                v_investment_line.setVisibility(View.GONE);
                v_back_money_line.setVisibility(View.GONE);

                tv_all.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tv_recharge.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_cash.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_investment.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_back_money.setTextColor(getResources().getColor(R.color.text_34393c));

                Ft_all = getSupportFragmentManager().beginTransaction();
                Ft_all.replace(R.id.fl_content, mAnsactionRecordsAllFragment);
                Ft_all.commit();
                break;
            case R.id.rl_recharge:
                v_all_line.setVisibility(View.GONE);
                v_recharge_line.setVisibility(View.VISIBLE);
                v_cash_line.setVisibility(View.GONE);
                v_investment_line.setVisibility(View.GONE);
                v_back_money_line.setVisibility(View.GONE);

                tv_all.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_recharge.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tv_cash.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_investment.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_back_money.setTextColor(getResources().getColor(R.color.text_34393c));

                Ft_recharge = getSupportFragmentManager().beginTransaction();
                Ft_recharge.replace(R.id.fl_content, mRechargeFragment);
                Ft_recharge.commit();
                break;
            case R.id.rl_cash:
                v_all_line.setVisibility(View.GONE);
                v_recharge_line.setVisibility(View.GONE);
                v_cash_line.setVisibility(View.VISIBLE);
                v_investment_line.setVisibility(View.GONE);
                v_back_money_line.setVisibility(View.GONE);

                tv_all.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_recharge.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_cash.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tv_investment.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_back_money.setTextColor(getResources().getColor(R.color.text_34393c));

                Ft_cash = getSupportFragmentManager().beginTransaction();
                Ft_cash.replace(R.id.fl_content, mCashFragment);
                Ft_cash.commit();
                break;
            case R.id.rl_investment:
                v_all_line.setVisibility(View.GONE);
                v_recharge_line.setVisibility(View.GONE);
                v_cash_line.setVisibility(View.GONE);
                v_investment_line.setVisibility(View.VISIBLE);
                v_back_money_line.setVisibility(View.GONE);

                tv_all.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_recharge.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_cash.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_investment.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tv_back_money.setTextColor(getResources().getColor(R.color.text_34393c));

                Ft_investment = getSupportFragmentManager().beginTransaction();
                Ft_investment.replace(R.id.fl_content, mInvestmentFragment);
                Ft_investment.commit();
                break;
            case R.id.rl_back_money:
                v_all_line.setVisibility(View.GONE);
                v_recharge_line.setVisibility(View.GONE);
                v_cash_line.setVisibility(View.GONE);
                v_investment_line.setVisibility(View.GONE);
                v_back_money_line.setVisibility(View.VISIBLE);

                tv_all.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_recharge.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_cash.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_investment.setTextColor(getResources().getColor(R.color.text_34393c));
                tv_back_money.setTextColor(getResources().getColor(R.color.text_a11c3f));

                Ft_backmoney = getSupportFragmentManager().beginTransaction();
                Ft_backmoney.replace(R.id.fl_content, mBackMoneyFragment);
                Ft_backmoney.commit();
                break;

            default:
                break;
        }
    }

}
