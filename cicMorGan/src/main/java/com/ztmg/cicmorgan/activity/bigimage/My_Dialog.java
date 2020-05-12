package com.ztmg.cicmorgan.activity.bigimage;

import com.ztmg.cicmorgan.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class My_Dialog extends Dialog {

	Dialog dialog;
	Context context;
	TextView tvMessage;

	public My_Dialog(Context context) {
		this(context, R.style.Custom_Progress);
		this.context = context;
	}

	public My_Dialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * 当窗口焦点改变时调用
	 */
	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);

		// 获取ImageView上的动画背景
		AnimationDrawable spinner = (AnimationDrawable) imageView
				.getBackground();
		// 开始动画
		spinner.start();
	}

	// 请求接口的时候显示dialog
	public void ShowDialog() {
		dialog = new My_Dialog(context);
		dialog.setContentView(R.layout.loading_dialog);
		tvMessage = (TextView) findViewById(R.id.tv_loading_dialog);
		// 按返回键是否取消
		dialog.setCancelable(false);
		// 监听返回键处理
		// dialog.setOnCancelListener(cancelListener);
		// 设置居中
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// 设置背景层透明度
		lp.dimAmount = 0.5f;
		dialog.getWindow().setAttributes(lp);
		// dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
	}

	// 数据请求成功的时候消失dialog
	public void DismissDialog() {
		dialog.dismiss();
	}

	// 判断对话框是否显示
	public boolean isShowing() {
		return dialog != null && dialog.isShowing();
	}

	// 设置文字的内容
	// public void setTextMessage(String text) {
	// tvMessage.setVisibility(View.VISIBLE);
	// tvMessage.setText(text);
	// }
}
