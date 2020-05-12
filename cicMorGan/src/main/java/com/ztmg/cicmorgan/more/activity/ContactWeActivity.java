package com.ztmg.cicmorgan.more.activity;

import java.util.Hashtable;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator.Result;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;

/**
 * 联系我们
 * @author pc
 *
 */
public class ContactWeActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_contact_we_first);
		initView();
		initData();
	}

	@Override
	protected void initView() {
		findViewById(R.id.rl_online_contact_we).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(ContactWeActivity.this,OnlineContactWeActivity.class));
			}
		});
		setTitle("联系我们");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});	

		//		TextView tv_qq = (TextView) findViewById(R.id.tv_qq);		
		//		tv_qq.setTextIsSelectable(true);
		//		TextView tv_mail = (TextView) findViewById(R.id.tv_mail);
		//		tv_mail.setTextIsSelectable(true);
		//		findViewById(R.id.rl_kefu).setOnClickListener(new OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//				
		//			}
		//		});
		findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(ContactWeActivity.this,AgreementActivity.class);
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
