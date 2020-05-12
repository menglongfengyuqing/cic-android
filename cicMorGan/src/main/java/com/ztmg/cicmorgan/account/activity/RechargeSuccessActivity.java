package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 充值成功
 *
 * @author pc
 */
public class RechargeSuccessActivity extends BaseActivity implements OnClickListener {
    private TextView tv_recharge_money, tv_recharge_bank_name, tv_recharge_bank_num;
    private String money, bankName, bindBankCardNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_recharge_success);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(RechargeSuccessActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        Intent intent = getIntent();
        money = intent.getStringExtra("money");
        bankName = intent.getStringExtra("bankname");
        bindBankCardNo = intent.getStringExtra("bindBankCardNo");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        tv_recharge_money = (TextView) findViewById(R.id.tv_recharge_money);
        tv_recharge_money.setText(money + "元");
        tv_recharge_bank_name = (TextView) findViewById(R.id.tv_recharge_bank_name);
        tv_recharge_bank_name.setText(bankName + ":");
        tv_recharge_bank_num = (TextView) findViewById(R.id.tv_recharge_bank_num);
        tv_recharge_bank_num.setText(bindBankCardNo);
        findViewById(R.id.bt_see_account).setOnClickListener(this);
        setTitle("充值");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_tip).setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_see_account://查看账户
                finish();
                RechargeActivity.getInstance().finish();
                break;
            case R.id.tv_tip:
                Intent riskTipFirstIntent = new Intent(RechargeSuccessActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            default:
                break;
        }
    }
}
