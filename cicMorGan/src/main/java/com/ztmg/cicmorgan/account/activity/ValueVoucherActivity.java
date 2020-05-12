package com.ztmg.cicmorgan.account.activity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.fragment.CardOutFragment;
import com.ztmg.cicmorgan.account.fragment.CardUsedFragment;
import com.ztmg.cicmorgan.account.fragment.CarduseFragment;
import com.ztmg.cicmorgan.account.fragment.RaisingRatesCardFragment;
import com.ztmg.cicmorgan.account.fragment.ServeCardFragment;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 优惠券 dong dong
 * 2018年9月5日
 */
public class ValueVoucherActivity extends FragmentActivity implements OnClickListener {
    private CardOutFragment mCardOutFragment;
    private CarduseFragment mCarduseFragment;
    private CardUsedFragment mCardUsedFragment;
    private FragmentTransaction Ft_out, Ft_use, Ft_used;
    private TextView tv_out, tv_use, tv_used;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(ValueVoucherActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_account_value_voucher);
        mCardOutFragment = new CardOutFragment("1");//已过期
        mCarduseFragment = new CarduseFragment("1");//可使用
        mCardUsedFragment = new CardUsedFragment("1");//已使用

        Ft_out = getSupportFragmentManager().beginTransaction();
        Ft_out.replace(R.id.fl_serve_use, mCarduseFragment);
        Ft_out.commit();
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(ValueVoucherActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = ValueVoucherActivity.this.getWindow();
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    private void initView() {
        findViewById(R.id.rl_back).setOnClickListener(this);
        findViewById(R.id.rl_serve_card_out).setOnClickListener(this);
        findViewById(R.id.rl_serve_card_use).setOnClickListener(this);
        findViewById(R.id.rl_serve_card_used).setOnClickListener(this);
        tv_out = (TextView) findViewById(R.id.tv_out);
        tv_use = (TextView) findViewById(R.id.tv_use);
        tv_used = (TextView) findViewById(R.id.tv_used);
    }

    private void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                onEvent(ValueVoucherActivity.this, "208001_yhq_back_click");
                finish();
                break;
            case R.id.rl_serve_card_out:
                tv_out.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tv_use.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_used.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                Ft_out = getSupportFragmentManager().beginTransaction();
                Ft_out.replace(R.id.fl_serve_use, mCardOutFragment);
                Ft_out.commit();
                break;
            case R.id.rl_serve_card_use:
                tv_out.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_use.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tv_used.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                Ft_use = getSupportFragmentManager().beginTransaction();
                Ft_use.replace(R.id.fl_serve_use, mCarduseFragment);
                Ft_use.commit();
                break;
            case R.id.rl_serve_card_used:
                tv_out.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_use.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
                tv_used.setTextColor(getResources().getColor(R.color.text_a11c3f));
                Ft_used = getSupportFragmentManager().beginTransaction();
                Ft_used.replace(R.id.fl_serve_use, mCardUsedFragment);
                Ft_used.commit();
                break;
            default:
                break;
        }
    }


}
