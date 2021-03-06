package com.ztmg.cicmorgan.account.fragment;

import java.lang.reflect.Field;

import com.ztmg.cicmorgan.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * 抵用券
 * @author pc
 *
 */
@SuppressLint("ValidFragment")
public class ServeCardFragment extends Fragment implements OnClickListener{
	private CardOutFragment mCardOutFragment;
	private CarduseFragment mCarduseFragment;
	private CardUsedFragment mCardUsedFragment;
	private View v_serve_card_out_line,v_serve_card_use_line,v_serve_card_used_line;
	private FragmentTransaction Ft_out,Ft_use,Ft_used;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_serve_card, null);
		mCardOutFragment = new CardOutFragment("1");//已过期
		mCarduseFragment = new CarduseFragment("1");//可使用
		mCardUsedFragment = new CardUsedFragment("1");//已使用

		Ft_out = getChildFragmentManager().beginTransaction();
		Ft_out.replace(R.id.fl_serve_use, mCarduseFragment);
		Ft_out.commit();

		initView(view);
		initData();
		return view;
	}
	private void initView(View v){
		v.findViewById(R.id.rl_serve_card_out).setOnClickListener(this);
		v.findViewById(R.id.rl_serve_card_use).setOnClickListener(this);
		v.findViewById(R.id.rl_serve_card_used).setOnClickListener(this);

		v_serve_card_out_line = v.findViewById(R.id.v_serve_card_out_line);
		v_serve_card_use_line = v.findViewById(R.id.v_serve_card_use_line);
		v_serve_card_used_line = v.findViewById(R.id.v_serve_card_used_line);
	}
	private void initData(){
		v_serve_card_out_line.setVisibility(View.GONE);
		v_serve_card_use_line.setVisibility(View.VISIBLE);
		v_serve_card_used_line.setVisibility(View.GONE);


	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_serve_card_out:
			v_serve_card_out_line.setVisibility(View.VISIBLE);
			v_serve_card_use_line.setVisibility(View.GONE);
			v_serve_card_used_line.setVisibility(View.GONE);
			Ft_out = getChildFragmentManager().beginTransaction();
			Ft_out.replace(R.id.fl_serve_use, mCardOutFragment);
			Ft_out.commit();
			break;
		case R.id.rl_serve_card_use:
			v_serve_card_out_line.setVisibility(View.GONE);
			v_serve_card_use_line.setVisibility(View.VISIBLE);
			v_serve_card_used_line.setVisibility(View.GONE);
			Ft_use = getChildFragmentManager().beginTransaction();
			Ft_use.replace(R.id.fl_serve_use, mCarduseFragment);
			Ft_use.commit();
			break;
		case R.id.rl_serve_card_used:
			v_serve_card_out_line.setVisibility(View.GONE);
			v_serve_card_use_line.setVisibility(View.GONE);
			v_serve_card_used_line.setVisibility(View.VISIBLE);
			Ft_used = getChildFragmentManager().beginTransaction();
			Ft_used.replace(R.id.fl_serve_use, mCardUsedFragment);
			Ft_used.commit();
			break;

		default:
			break;
		}
	}
	
	@Override
	  public void onDetach() {
	    super.onDetach();
	    try {
	      Field childFragmentManager = Fragment.class
	          .getDeclaredField("mChildFragmentManager");
	      childFragmentManager.setAccessible(true);
	      childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	      throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	      throw new RuntimeException(e);
	    }
	  }
}
