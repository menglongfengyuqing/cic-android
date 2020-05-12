package com.ztmg.cicmorgan.view;


import com.ztmg.cicmorgan.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;


public class CustomProgress extends Dialog {
    private static CustomProgress dialog;

    public CustomProgress(Context context) {
        this(context, R.style.Custom_Progress);
    }

    public CustomProgress(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        // 获取ImageView上的动画背景
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        // 开始动画
        spinner.start();
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context        上下文
     * @param message        提示
     * @param cancelable     是否按返回键取消
     * @param cancelListener 按下返回键监听
     * @return
     */
    public static CustomProgress show(Context context) {
        dialog = null;
        dialog = new CustomProgress(context);
        dialog.setContentView(R.layout.loading_dialog);
        // 按返回键是否取消
        dialog.setCancelable(true);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        return dialog;
    }

    public static void CustomDismis() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
