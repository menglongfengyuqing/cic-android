package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.LoginUserProvider;

/**
 * 电子邮箱
 * @author pc
 *
 */
public class MailActivity extends BaseActivity{
	private TextView et_person_name;
	private LinearLayout ll_tishi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activty_mail);
		initView();
		initData();
	}

	@Override
	protected void initView() {
		
		et_person_name = (TextView) findViewById(R.id.et_person_name);
		ll_tishi = (LinearLayout) findViewById(R.id.ll_tishi);//提示语言
		String mail = LoginUserProvider.getUser(MailActivity.this).getEmail().toString();
		if(TextUtils.isEmpty(mail)){
			et_person_name.setText("未绑定邮箱");
			setTitle("未绑定邮箱");
		}else{
			et_person_name.setText(mail);
			setTitle("绑定邮箱");
			ll_tishi.setVisibility(View.GONE);
		}
		setBack(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(MailActivity.this,AgreementActivity.class);
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
