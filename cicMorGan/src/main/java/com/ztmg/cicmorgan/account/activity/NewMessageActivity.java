package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.home.activity.NoticeListActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 消息中心
 *
 * @author pc
 */
public class NewMessageActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(NewMessageActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_new_message);
        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(NewMessageActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
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

    @Override
    protected void initView() {
        setTitle("消息中心");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(NewMessageActivity.this, "201001_news_back_click");
                finish();
            }
        });
        findViewById(R.id.rl_station_message).setOnClickListener(this);//站内消息
        findViewById(R.id.rl_system_message).setOnClickListener(this);
        findViewById(R.id.rl_platform_notice).setOnClickListener(this);//平台公告

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_station_message://站内消息
                onEvent(NewMessageActivity.this, "201002_news_znxx_click");
                //是否登录
                if (LoginUserProvider.getUser(NewMessageActivity.this) != null) {
                    DoCacheUtil util = DoCacheUtil.get(NewMessageActivity.this);
                    String str = util.getAsString("isLogin");
                    if (str != null) {
                        if (str.equals("isLogin")) {//已登录
                            //跳转站内消息
                            Intent mIntent = new Intent(NewMessageActivity.this, MessageActivity.class);
                            startActivity(mIntent);
                        } else {//未登录
                            Intent intent1 = new Intent(NewMessageActivity.this, LoginActivity.class);
                            intent1.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                            startActivity(intent1);
                        }
                    } else {
                        Intent intent2 = new Intent(NewMessageActivity.this, LoginActivity.class);
                        intent2.putExtra("overtime", "6");
                        startActivity(intent2);
                    }
                } else {
                    Intent intent3 = new Intent(NewMessageActivity.this, LoginActivity.class);
                    intent3.putExtra("overtime", "6");
                    startActivity(intent3);
                }

                break;
            case R.id.rl_system_message:
                Intent messageIntent = new Intent(NewMessageActivity.this, SystemMessageListActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.rl_platform_notice://平台公告
                onEvent(NewMessageActivity.this, "201003_news_ptgg_click");
                Intent noticeIntent = new Intent(NewMessageActivity.this, NoticeListActivity.class);
                noticeIntent.putExtra("Url", "/account_announcement.html?app=1");
                startActivity(noticeIntent);
                break;
            default:
                break;
        }
    }
}
