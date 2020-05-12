package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 绑卡成功
 * @author pc
 *
 */
public class BindBankSuccessActivity extends BaseActivity{

	private Button bt_back_account;
	private String isBackAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_bind_bank_success);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(BindBankSuccessActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		//0，不显示返回账户中心，1,交易记录，2，安全中心
		Intent intent = getIntent();
		isBackAccount = intent.getStringExtra("isBackAccount");
		initView();
		initData();
	}

	@Override
	protected void initView() {
		bt_back_account = (Button) findViewById(R.id.bt_back_account);
		if(!TextUtils.isEmpty(isBackAccount)&&isBackAccount.equals("0")){
			bt_back_account.setVisibility(View.GONE);
		}
		bt_back_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(isBackAccount)&&isBackAccount.equals("1")){//交易记录
					finish();
					BindBankCardActivity.getInstance().finish();
					if(AnsactionRecordsActivity.getInstance()!=null){
						AnsactionRecordsActivity.getInstance().finish();
					}
				}else if(!TextUtils.isEmpty(isBackAccount)&&isBackAccount.equals("2")){//安全设置
					finish();
					BindBankCardActivity.getInstance().finish();
					if(SecuritySettingActivity.getInstance()!=null){
						SecuritySettingActivity.getInstance().finish();
					}
				}else{
					BindBankCardActivity.getInstance().finish();
					finish();
				}
			}
		});
		setTitle("绑卡成功");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BindBankCardActivity.getInstance().finish();
				finish();
			}
		});
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(BindBankSuccessActivity.this,AgreementActivity.class);
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
