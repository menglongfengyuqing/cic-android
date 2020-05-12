package com.ztmg.cicmorgan.account.activity;

import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.fragment.SafeInvestFragment;
import com.ztmg.cicmorgan.account.fragment.SupplyChainFragment;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 我的出借
 *
 * @author pc
 */
public class MyInvestmentListActivity extends BaseActivity implements OnClickListener {

    private SupplyChainFragment supplyChainFragment;
    private SafeInvestFragment safeInvestFragment;
    private FragmentTransaction Ft_supplychain, Ft_safeinvest;
    private RelativeLayout ll_front, ll_middle;
    private TextView tx_front, tx_middle;
    private TextView text_front_line, text_middle_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(MyInvestmentListActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_my_investment_list);
        ACache.get(this).put(Constant.MyInvestmentKey, "1");//我的出借供应链 1  默认是1
        ACache.get(this).put(Constant.SupplyChainInvestmentKey_mine_chujie, "0");//我的出借安心投 出借供应链默认是显示
        //1：安心投类，2：供应链类
        Bundle supplyChainBundle = new Bundle();
        supplyChainBundle.putString("state", "2");
        supplyChainFragment = new SupplyChainFragment();
        supplyChainFragment.setArguments(supplyChainBundle);

        Bundle safeInvestBundle = new Bundle();
        safeInvestBundle.putString("state", "1");
        safeInvestFragment = new SafeInvestFragment();
        safeInvestFragment.setArguments(safeInvestBundle);

        Ft_supplychain = getFragmentManager().beginTransaction();
        Ft_supplychain.replace(R.id.fl_my_investment, supplyChainFragment);
        Ft_supplychain.commit();
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MyInvestmentListActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
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
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(MyInvestmentListActivity.this, "207001_wdcj_back_click");
                finish();
            }
        });
        //		tv_supply_chain = (TextView) findViewById(R.id.tv_supply_chain);//供应链
        //		tv_safe_investment = (TextView) findViewById(R.id.tv_safe_investment);//安心投
        //		tv_supply_chain.setOnClickListener(this);
        //		tv_safe_investment.setOnClickListener(this);

        ll_front = (RelativeLayout) findViewById(R.id.ll_front);
        ll_middle = (RelativeLayout) findViewById(R.id.ll_middle);
        ll_front.setOnClickListener(this);
        ll_middle.setOnClickListener(this);

        tx_front = (TextView) findViewById(R.id.tx_front);
        tx_middle = (TextView) findViewById(R.id.tx_middle);

        text_front_line = (TextView) findViewById(R.id.text_front_line);
        text_middle_line = (TextView) findViewById(R.id.text_middle_line);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_front://供应链
                //			tv_supply_chain.setBackgroundDrawable(getResources().getDrawable(R.drawable.but_white_no_clicle));
                //			tv_safe_investment.setBackgroundDrawable(getResources().getDrawable(R.drawable.but_transparent));
                text_front_line.setVisibility(View.GONE);
                text_middle_line.setVisibility(View.GONE);

                tx_front.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tx_middle.setTextColor(getResources().getColor(R.color.text_34393c));

                Ft_supplychain = getFragmentManager().beginTransaction();
                Ft_supplychain.replace(R.id.fl_my_investment, supplyChainFragment);
                Ft_supplychain.commit();
                ACache.get(this).put(Constant.MyInvestmentKey, "1");//我的出借供应链 1
                ACache.get(this).put(Constant.SupplyChainInvestmentKey_mine_chujie, "0");//我的出借供应链 0
                break;
            case R.id.ll_middle://安心投
                //tv_supply_chain.setBackgroundDrawable(getResources().getDrawable(R.drawable.but_transparent));
                //tv_safe_investment.setBackgroundDrawable(getResources().getDrawable(R.drawable.but_white_no_clicle));

                text_front_line.setVisibility(View.GONE);
                text_middle_line.setVisibility(View.GONE);

                tx_front.setTextColor(getResources().getColor(R.color.text_34393c));
                tx_middle.setTextColor(getResources().getColor(R.color.text_a11c3f));

                Ft_safeinvest = getFragmentManager().beginTransaction();
                Ft_safeinvest.replace(R.id.fl_my_investment, safeInvestFragment);
                Ft_safeinvest.commit();
                ACache.get(this).put(Constant.MyInvestmentKey, "2");//我的出借安心投 2
                ACache.get(this).put(Constant.SupplyChainInvestmentKey_mine_chujie, "1");//我的出借安心投 1
                break;
            default:
                break;
        }
    }
}
