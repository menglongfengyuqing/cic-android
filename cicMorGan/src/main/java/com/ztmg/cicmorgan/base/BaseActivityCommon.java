package com.ztmg.cicmorgan.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.net.NetManger;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 根据网络请求重新写 base基类  慢慢替换
 * dong dong
 * 2018年7月18日
 * <p>
 * BaseActivityCommon  基类
 */

public abstract class BaseActivityCommon extends FragmentActivity implements OkUtil.HttpCallback {
    public UserInfo mUser;
    private AsyncHttpClient client;
    public static Context mContext;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mUser = LoginUserProvider.getUser(this);
        setContentView(R.layout.activity_base);
        client = NetManger.getAsyncHttpClient();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
        mContext = this;
        //友盟数据统计
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

        //清除用户者的数据
        //LoginUserProvider.cleanData(UnlockGesturePasswordActivity.this);
        //LoginUserProvider.cleanDetailData(UnlockGesturePasswordActivity.this);
        //DoCacheUtil util = DoCacheUtil.get(UnlockGesturePasswordActivity.this);
        //util.put("isLogin", "");

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    abstract protected void initView();

    abstract protected void initData();

    /**
     * 设置标题题目
     */
    public void setTitle(String title) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        if (tv_title == null) {
            return;
        }
        tv_title.setText(title);
    }

    /**
     * 设置左边按钮
     */
    public void setBack(OnClickListener onClickListener) {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            return;
        }
        rl_back.setVisibility(View.VISIBLE);
        if (onClickListener == null) {
            rl_back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            rl_back.setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置右边按钮
     */
    public void setRight(OnClickListener onClickListener) {
        ImageView iv_img_right = (ImageView) findViewById(R.id.iv_img_right);
        if (iv_img_right == null) {
            return;
        }
        iv_img_right.setVisibility(View.VISIBLE);
        if (onClickListener == null) {
            iv_img_right.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            iv_img_right.setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置右边文字
     */
    public void setRightText(String text, OnClickListener onClickListener) {
        TextView tv_right_text = (TextView) findViewById(R.id.tv_right_text);
        if (tv_right_text == null) {
            return;
        }
        tv_right_text.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            tv_right_text.setText(text);
        } else {
            tv_right_text.setText("");
        }
        tv_right_text.setOnClickListener(onClickListener);
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /**
     * 保存文件到sd卡
     *
     * @param str
     */
    public static void saveStr2Sd(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream fos = null;
            try {
                File dir = new File(Environment.getExternalStorageDirectory(), "result_Member");
                dir.mkdirs();
                File f = new File(dir, "pathNam.txt");
                if (!f.exists()) {
                    f.createNewFile();
                }
                fos = new FileOutputStream(f);
                Log.e("result", f.getAbsolutePath());
                fos.write(str.toString().getBytes());
            } catch (Exception e) {
                Log.e("result", "write log error" + e.getMessage());
                e.printStackTrace();

            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = BaseActivityCommon.this.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void success(String result, String json) {
        responseData(result, json);
    }

    /**
     * 请求返回数据
     */
    protected abstract void responseData(String stringId, String json);

    //判断输入的金额是否是数字
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
