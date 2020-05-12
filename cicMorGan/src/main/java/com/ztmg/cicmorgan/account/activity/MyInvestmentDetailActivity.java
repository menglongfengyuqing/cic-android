package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;

/**
 * 我的投资详情
 * @author pc
 *
 */
public class MyInvestmentDetailActivity extends BaseActivity{
	private TextView tv_name,tv_state,tv_span,tv_investment_money,
	tv_investment_time,tv_year_rate,tv_income,tv_time;
	private String name,state,span,money,time,rate,income,endtime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_my_investment_detail);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		state = intent.getStringExtra("state");
		money = intent.getStringExtra("money");
		time = intent.getStringExtra("time");
		rate = intent.getStringExtra("rate");
		income = intent.getStringExtra("income");
		endtime = intent.getStringExtra("endtime");
		span = intent.getStringExtra("span");
		initView();
		initData();
	}
	@Override
	protected void initView() {
		setTitle("详情记录");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_name = (TextView) findViewById(R.id.tv_name);	
		tv_name.setText(name);
		tv_state = (TextView) findViewById(R.id.tv_state);
		//状态 0-撤销；1-草稿；2-审核；3-发布；4-上线；5-满标；6-还款中；7-已还完
		if(state.equals("0")){
			tv_state.setText("撤销中");
		}else if(state.equals("1")){
			tv_state.setText("草稿中");
		}else if(state.equals("2")){
			tv_state.setText("审核中");
		}else if(state.equals("3")){
			tv_state.setText("发布中");
		}else if(state.equals("4")){
			tv_state.setText("上线中");
		}else if(state.equals("5")){
			tv_state.setText("满标审核中");
		}else if(state.equals("6")){
			tv_state.setText("回款中");
		}else if(state.equals("7")){
			tv_state.setText("已还完");
		}

		tv_span = (TextView) findViewById(R.id.tv_span);
		tv_span.setText(span+"天");
		tv_investment_money = (TextView) findViewById(R.id.tv_investment_money);
		tv_investment_money.setText(money);
		tv_investment_time = (TextView) findViewById(R.id.tv_investment_time);
		tv_investment_time.setText(time);
		tv_year_rate = (TextView) findViewById(R.id.tv_year_rate);
		tv_year_rate.setText(rate+"%");
		tv_income = (TextView) findViewById(R.id.tv_income);
		tv_income.setText(income);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setText(endtime);
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(MyInvestmentDetailActivity.this,AgreementActivity.class);
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
