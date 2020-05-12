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
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;

public abstract class BaseActivity extends FragmentActivity {
    public UserInfo mUser;
    private AsyncHttpClient client;
    public static Context mContext;
    private Dialog dialog;
    public ImageView iv_img_right, iv_img_right_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = LoginUserProvider.getUser(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
     * 设置左边按钮
     */
    public void setBack(OnClickListener onClickListener) {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            return;
        }
        rl_back.setVisibility(View.VISIBLE);
        if (onClickListener == null) {
            rl_back.setOnClickListener(new View.OnClickListener() {

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
     * 设置右边按钮
     */
    public void setRight(OnClickListener onClickListener) {
        iv_img_right = (ImageView) findViewById(R.id.iv_img_right);
        if (iv_img_right == null) {
            return;
        }
        iv_img_right.setVisibility(View.VISIBLE);
        if (onClickListener == null) {
            iv_img_right.setOnClickListener(new View.OnClickListener() {

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
     * 设置右边按钮  加号
     */
    public void setRightAdd(OnClickListener onClickListener) {
        iv_img_right_add = (ImageView) findViewById(R.id.iv_img_right_add);

        if (iv_img_right_add == null) {
            return;
        }
        iv_img_right_add.setVisibility(View.VISIBLE);
        if (onClickListener == null) {
            iv_img_right_add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            iv_img_right_add.setOnClickListener(onClickListener);
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


    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                        //Toast.makeText(BaseActivity.this, "小数点后最多输入两位", 0).show();
                        //Toast.makeText(mContext, "小数点后最多输入两位", 0).show();
                        ToastUtils.show(mContext, "小数点后最多输入两位");
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });

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
        Window win = BaseActivity.this.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}
