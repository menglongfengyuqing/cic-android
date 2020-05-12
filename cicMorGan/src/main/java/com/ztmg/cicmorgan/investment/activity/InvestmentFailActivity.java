package com.ztmg.cicmorgan.investment.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 投资失败
 *
 * @author pc
 */
public class InvestmentFailActivity extends BaseActivity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_investment_fail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(InvestmentFailActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        findViewById(R.id.bt_again_investment).setOnClickListener(this);
        setTitle("出借失败");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //InvestmentDetailActivity.getInstance().finish();
                finish();
            }
        });
        findViewById(R.id.tv_see_my_account).setOnClickListener(this);
        findViewById(R.id.tv_tip).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_again_investment:
                finish();
                break;
            case R.id.tv_see_my_account:
                Intent investmentIntent = new Intent(InvestmentFailActivity.this, MainActivity.class);
                investmentIntent.putExtra("investment", "accountfrom");
                investmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(investmentIntent);
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(InvestmentFailActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            default:
                break;
        }
    }

}
