package com.ztmg.cicmorgan.account.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.fragment.MessageFragment;
import com.ztmg.cicmorgan.account.fragment.NoticeFragment;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.DoCacheUtil;

/**
 * 账户消息和公告
 * @author pc
 *
 */
public class MessageNoticeActivity extends FragmentActivity implements OnClickListener{
	private RelativeLayout rl_back,rl_message,rl_notice;
	private TextView tv_readed;
	private View v_message_line,v_notice_line;
	private MessageFragment mMessageFragment;
	private NoticeFragment mNoticeFragment;
	private FragmentTransaction Ft_message,Ft_notice;
	private int index;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		index=1;
		mMessageFragment = new MessageFragment();
		mNoticeFragment = new NoticeFragment();
		
		Ft_message = getSupportFragmentManager().beginTransaction();
		Ft_message.replace(R.id.fl_message, mMessageFragment);
		Ft_message.commit();
		
		initView();
		initData();
	}
	private void initView(){
		
		findViewById(R.id.rl_back).setOnClickListener(this);
		findViewById(R.id.rl_message).setOnClickListener(this);
		findViewById(R.id.rl_notice).setOnClickListener(this);
		findViewById(R.id.tv_readed).setOnClickListener(this);
		v_message_line = findViewById(R.id.v_message_line);
		v_notice_line = findViewById(R.id.v_notice_line);
	}
	
	private void initData(){
		v_message_line.setVisibility(View.VISIBLE);
		v_notice_line.setVisibility(View.GONE);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;
		case R.id.rl_message:
			index=1;
			v_message_line.setVisibility(View.VISIBLE);
			v_notice_line.setVisibility(View.GONE);
			
			Ft_message = getSupportFragmentManager().beginTransaction();
			Ft_message.replace(R.id.fl_message, mMessageFragment);
			Ft_message.commit();
			break;
		case R.id.rl_notice:
			index=2;
			v_message_line.setVisibility(View.GONE);
			v_notice_line.setVisibility(View.VISIBLE);
			
			Ft_notice = getSupportFragmentManager().beginTransaction();
			Ft_notice.replace(R.id.fl_message, mNoticeFragment);
			Ft_notice.commit();
			break;
		case R.id.tv_readed://一键已读
			if(index==1){
				mMessageFragment.changeColor(true);
			}else if(index==2){
				mNoticeFragment.changeColor(true);
			}
			break;

		default:
			break;
		}
	}
	
}
