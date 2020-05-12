package com.ztmg.cicmorgan.investment.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivityCom;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * dong dong
 * 2018年8月2日
 * 评级说明
 */

public class GradeDescriptionActivity extends BaseActivityCom {


    @BindView(R.id.note)
    TextView note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(GradeDescriptionActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_grade_description);
        ButterKnife.bind(this);
        setTitle("评级说明");
        setBack(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(GradeDescriptionActivity.this, "315002_project_pjsm_back_click");
                finish();
            }
        });
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(GradeDescriptionActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

        String str = "注：项目标的" + " <font color='#cbb693'>星级越高</font> " + "，风险" + " <font color='#cbb693'>等级越低</font> " + "。";
        note.setText(Html.fromHtml(str));

    }

    @Override
    protected void initView() {


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void responseData(String stringId, String json) {

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
