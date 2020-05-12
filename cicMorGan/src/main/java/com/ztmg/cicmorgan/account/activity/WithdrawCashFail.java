package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 提现失败
 * @author pc
 *
 */
public class WithdrawCashFail extends BaseActivity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_withdraw_cash_fail);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(WithdrawCashFail.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		initView();
		initData();
	}

	@Override
	protected void initView() {
		findViewById(R.id.bt_again).setOnClickListener(this);
		findViewById(R.id.tv_see_account).setOnClickListener(this);
		setTitle("提现");
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
		case R.id.bt_again:
			finish();
			break;
		case R.id.tv_see_account:
			finish();
			WithdrawCashActivity.getInstance().finish();
			break;
		case R.id.tv_tip:
			Intent riskTipFirstIntent = new Intent(WithdrawCashFail.this,AgreementActivity.class);
			riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
			riskTipFirstIntent.putExtra("title", "风险提示书");
			startActivity(riskTipFirstIntent);
			break;
		default:
			break;
		}		
	}

}
