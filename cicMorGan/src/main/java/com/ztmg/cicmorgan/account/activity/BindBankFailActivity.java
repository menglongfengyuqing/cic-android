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
 * 绑卡失败
 * @author pc
 *
 */
public class BindBankFailActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_bind_bank_fail);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(BindBankFailActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		initView();
		initData();
	}

	@Override
	protected void initView() {
		findViewById(R.id.bt_again).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setTitle("绑卡失败");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(BindBankCardActivity.getInstance()!=null){
					BindBankCardActivity.getInstance().finish();
					finish();
				}
			}
		});
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(BindBankFailActivity.this,AgreementActivity.class);
				riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
				riskTipFirstIntent.putExtra("title", "风险提示书");
				startActivity(riskTipFirstIntent);
			}
		});
	}

	@Override
	protected void initData() {

	}

}
