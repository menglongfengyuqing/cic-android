package com.ztmg.cicmorgan.integral.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 积分规则
 *
 * @author pc
 */
public class IntegralRuleActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(IntegralRuleActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_integral_rule);
        initView();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(IntegralRuleActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void initView() {
        setTitle("积分规则");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(IntegralRuleActivity.this, "403001_jfgz_back_click");
                finish();
            }
        });
        //        findViewById(R.id.tv_tip).setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                Intent riskTipFirstIntent = new Intent(IntegralRuleActivity.this, AgreementActivity.class);
        //                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
        //                riskTipFirstIntent.putExtra("title", "风险提示书");
        //                startActivity(riskTipFirstIntent);
        //            }
        //        });
    }

    @Override
    protected void initData() {

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

}
