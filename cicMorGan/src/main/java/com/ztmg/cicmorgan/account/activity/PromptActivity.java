package com.ztmg.cicmorgan.account.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.PromptEntity;
import com.ztmg.cicmorgan.entity.PromptEntity.DataBean;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;

import java.text.DecimalFormat;

/**
 * 出借完成之后提示界面
 * 2018年6月28日
 * dong
 */

public class PromptActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back, mRelativeLayout_bg;
    private TextView tv_title;
    private RelativeLayout mRelativeLayout_1;
    private TextView tv_status;
    private TextView text_ok, text_no;
    private TextView project_name;
    private TextView project_money;
    private TextView back_home;
    private String orderId;
    private View status;
    private int i = 0;//状态判断
    private TimeTask timeTask;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
        orderId = getIntent().getStringExtra("orderId");
        //构造方法的字符格式这里如果小数不足2位,会以0补足
        decimalFormat = new DecimalFormat("0.00");
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //            setTranslucentStatus(true);
        //            SystemBarTintManager tintManager = new SystemBarTintManager(PromptActivity.this);
        //            tintManager.setStatusBarTintEnabled(true);
        //            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        //        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        status = findViewById(R.id.status);
        mRelativeLayout_bg = (RelativeLayout) findViewById(R.id.mRelativeLayout_bg);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提示");
        mRelativeLayout_1 = (RelativeLayout) findViewById(R.id.mRelativeLayout_1);
        tv_status = (TextView) findViewById(R.id.tv_status);
        text_ok = (TextView) findViewById(R.id.text_ok);
        text_no = (TextView) findViewById(R.id.text_no);
        project_name = (TextView) findViewById(R.id.project_name);
        project_money = (TextView) findViewById(R.id.project_money);
        back_home = (TextView) findViewById(R.id.back_home);
        back_home.setOnClickListener(this);
        i = 0;
    }

    @Override
    protected void initData() {
        getData(orderId);
    }

    private void getData(String orderId) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.SEACHINVESTRESULT;
        RequestParams params = new RequestParams();
        params.put("orderId", orderId);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String json = new String(responseBody);
                LogUtil.e("------------json-----" + json);

                try {
                    PromptEntity PromptEntity = GsonManager.fromJson(json, PromptEntity.class);
                    if (PromptEntity.getState().equals("0")) {
                        DataBean data = PromptEntity.getData();
                        String state = data.getState();
                        LogUtil.e("------------state-----" + state);
                        //state = "0";
                        //if (i == 1)
                        //    state = "2";
                        if (i > 0) {
                            if (state.equals("0")) {
                                finish();
                                //timeTask = new TimeTask(5 * 1000);
                                //timeTask.start();
                                return;
                            }
                        }
                        LogUtil.e("------------i-----" + i);
                        if (state.equals("1")) {
                            LogUtil.e("------------1-----" + state);
                            //成功
                            tv_status.setText("出借成功！");
                            text_ok.setText("恭喜你出借成功！");
                            project_name.setText(data.getProjectName() + "(" + data.getProjectSn() + ")");
                            project_money.setText((decimalFormat.format(Double.parseDouble(data.getAmount())) + "元"));
                            status.setBackgroundResource(R.drawable.shape_gradient_yes);
                            mRelativeLayout_bg.setBackgroundResource(R.drawable.shape_gradient_yes);
                            mRelativeLayout_1.setBackgroundResource(R.drawable.icon_succeed);


                        } else if (state.equals("2")) {
                            LogUtil.e("------------2-----" + state);
                            //失败
                            status.setBackgroundResource(R.drawable.shape_gradient_no);
                            mRelativeLayout_bg.setBackgroundResource(R.drawable.shape_gradient_no);
                            mRelativeLayout_1.setBackgroundResource(R.drawable.icon_failed);
                            tv_status.setText("出借失败！");
                            text_ok.setText("出借失败！");
                            text_no.setText("(" + data.getRemark() + ")");
                            text_no.setVisibility(View.VISIBLE);
                            project_name.setText(data.getProjectName() + "(" + data.getProjectSn() + ")");
                            project_money.setText((decimalFormat.format(Double.parseDouble(data.getAmount()))) + "元");
                        } else if (state.equals("0")) {
                            LogUtil.e("------------0-----" + state);
                            //处理中
                            Processing(data);
                        }
                    } else if (PromptEntity.getState().equals("4")) {
                        //                        String mGesture = LoginUserProvider.getUser(PaymentPlanActivity.this).getGesturePwd();
                        //                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                        //                            //设置手势密码
                        //                            Intent intent = new Intent(PaymentPlanActivity.this, UnlockGesturePasswordActivity.class);
                        //                            intent.putExtra("overtime", "0");
                        //                            startActivity(intent);
                        //                        } else {
                        //                            //未设置手势密码
                        //                            Intent intent = new Intent(PaymentPlanActivity.this, LoginActivity.class);
                        //                            intent.putExtra("overtime", "0");
                        //                            startActivity(intent);
                        //                        }
                        //                        //LoginUserProvider.cleanData(PaymentPlanActivity.this);
                        //                        //LoginUserProvider.cleanDetailData(PaymentPlanActivity.this);
                        //                        DoCacheUtil util = DoCacheUtil.get(PaymentPlanActivity.this);
                        //                        util.put("isLogin", "");
                    } else {
                        ToastUtils.show(PromptActivity.this, PromptEntity.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(PromptActivity.this, "解析异常");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ToastUtils.show(PromptActivity.this, "请检查网络");
            }
        });
    }

    //处理中
    private void Processing(DataBean data) {
        i = 0;
        i++;
        status.setBackgroundResource(R.drawable.shape_gradient_processing);
        mRelativeLayout_bg.setBackgroundResource(R.drawable.shape_gradient_processing);
        mRelativeLayout_1.setBackgroundResource(R.drawable.icon_inprocess);
        tv_status.setText("处理中...");
        //text_no.setText("(" + data.getRemark() + ")");
        //text_no.setVisibility(View.VISIBLE);
        project_name.setText(data.getProjectName() + "(" + data.getProjectSn() + ")");
        project_money.setText((decimalFormat.format(Double.parseDouble(data.getAmount()))) + "元");
        timeTask = new TimeTask(15 * 1000);
        timeTask.start();
    }


    // 计时器
    private class TimeTask extends CountDownTimer {

        public TimeTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public TimeTask(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished / 1000 == 5) {
                getData(orderId);
                timeTask.cancel();
                LogUtil.e("---------倒计时网络请求--------");
            } else {
                text_ok.setText("项目出借处理中" + "(" + millisUntilFinished / 1000 + "s)");
            }
        }

        @Override
        public void onFinish() {
            text_ok.setText("项目出借处理中");
            timeTask.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_home) {
            Intent intent = new Intent(PromptActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
