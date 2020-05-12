package com.ztmg.cicmorgan.more.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;

/**
 * 版本信息
 * @author pc
 *
 */
public class VersionInformationActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_version_information);
		initView();
		initData();
	}
	@Override
	protected void initView() {
		setTitle("版本信息");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		
	}
}
