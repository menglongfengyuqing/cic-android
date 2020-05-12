package com.ztmg.cicmorgan.account.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.SystemMessageAdapter;
import com.ztmg.cicmorgan.account.entity.SystemMessageEntity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 系统消息
 * @author pc
 *
 */
public class SystemMessageListActivity extends BaseActivity{
	private ListView lv_system_message_list;
	private SystemMessageAdapter adapter;
	private List<SystemMessageEntity> systemMessageList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_system_message_list);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(SystemMessageListActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		initView();
		initData();
	}

	@Override
	protected void initView() {
		setTitle("系统消息");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		lv_system_message_list = (ListView) findViewById(R.id.lv_system_message_list);
		findViewById(R.id.tv_tips).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(SystemMessageListActivity.this,AgreementActivity.class);
				riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
				riskTipFirstIntent.putExtra("title", "风险提示书");
				startActivity(riskTipFirstIntent);
			}
		});

	}

	@Override
	protected void initData() {
		systemMessageList = new ArrayList<SystemMessageEntity>();
		for(int i=0;i<10;i++){
			SystemMessageEntity entity = new SystemMessageEntity();
			entity.setText("系统消息"+i);
			systemMessageList.add(entity);
		}
		adapter = new SystemMessageAdapter(SystemMessageListActivity.this, systemMessageList);
		lv_system_message_list.setAdapter(adapter);
	}

}
