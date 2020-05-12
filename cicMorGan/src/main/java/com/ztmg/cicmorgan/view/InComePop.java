package com.ztmg.cicmorgan.view;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.util.ToastUtils;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InComePop extends PopupWindow {
	public InComePop(final Context mContext, final View parent) {

		final View view = View.inflate(mContext, R.layout.pop_income, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_in));
		RelativeLayout rl_pop = (RelativeLayout) view.findViewById(R.id.rl_pop);
		rl_pop.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.in_to_top));
		// 设置popupWindow的宽和高
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(false);
		setOutsideTouchable(false);
		setContentView(view);
//		showAtLocation(parent, Gravity.RIGHT, 0, 0);
		showAsDropDown(parent, 100, -230);
		update();
		
		TextView tv_num = (TextView) view.findViewById(R.id.tv_num);

	}
}
