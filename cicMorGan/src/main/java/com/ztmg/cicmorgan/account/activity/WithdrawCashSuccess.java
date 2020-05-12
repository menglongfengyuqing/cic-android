package com.ztmg.cicmorgan.account.activity;

import java.text.DecimalFormat;

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
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 申请提现成功
 * @author pc
 *
 */
public class WithdrawCashSuccess extends BaseActivity{
	private TextView tv_withdraw_money,tv_counter_fee,
	tv_arrival_money,tv_bank_name,tv_bank_num;
	private String feeAmount,amount,bankName,bankCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_withdraw_cash_success);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(WithdrawCashSuccess.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		Intent intent =getIntent();
		feeAmount = intent.getStringExtra("feeAmount");//手续费
		amount = intent.getStringExtra("amount");//输入金额
		bankName = intent.getStringExtra("bankName");//银行卡名字
		bankCode = intent.getStringExtra("bankCode");//银行卡卡号
		initView();
		initData();

	}

	@Override
	protected void initView() {
		tv_withdraw_money = (TextView) findViewById(R.id.tv_withdraw_money);//成功申请
		tv_withdraw_money.setText(amount+"元");
		tv_counter_fee = (TextView) findViewById(R.id.tv_counter_fee);//手续费
		tv_counter_fee.setText(feeAmount+"元");
		tv_arrival_money = (TextView) findViewById(R.id.tv_arrival_money);//到账钱数
		double amountDou = Double.parseDouble(amount);
		double feeAmountDou = Double.parseDouble(feeAmount);
		double arrivalMoney = amountDou-feeAmountDou;
		DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String distanceString = decimalFormat.format(arrivalMoney);//format 返回的是字符串
		tv_arrival_money.setText(distanceString+"元");
		tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);//银行卡名字
		tv_bank_name.setText(bankName);
		tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);//银行卡号
		tv_bank_num.setText(bankCode);
		//查看我的账户
		findViewById(R.id.bt_see_account).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				WithdrawCashActivity.getInstance().finish();
			}
		});
		setTitle("提现");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {//返回账户中心
				finish();
				WithdrawCashActivity.getInstance().finish();
			}
		});
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(WithdrawCashSuccess.this,AgreementActivity.class);
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
