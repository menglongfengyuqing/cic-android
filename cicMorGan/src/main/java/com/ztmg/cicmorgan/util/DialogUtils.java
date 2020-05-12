package com.ztmg.cicmorgan.util;


import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.login.LoginActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DialogUtils {

	private static TextView tv_content;
	private static TextView confirm;

	public static void showDialog(Context ctx,String str){
		AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
		View view=View.inflate(ctx, R.layout.dialog_no_image, null);
		tv_content = (TextView) view.findViewById(R.id.content);
		confirm = (TextView) view.findViewById(R.id.confirm);
		tv_content.setText(str);
		final AlertDialog dialog=adb.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		
	}
	public static void showDialogLogin(final Context ctx,String str){
		AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
		View view=View.inflate(ctx, R.layout.dialog_no_image, null);
		tv_content = (TextView) view.findViewById(R.id.content);
		confirm = (TextView) view.findViewById(R.id.confirm);
		tv_content.setText(str);
		final AlertDialog dialog=adb.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ctx.startActivity(new Intent(ctx,
						LoginActivity.class));
			}
		});
		
	}
}
