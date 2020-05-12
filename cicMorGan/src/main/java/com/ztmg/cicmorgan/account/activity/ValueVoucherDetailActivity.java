package com.ztmg.cicmorgan.account.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;

/**
 * 优惠券详情
 *
 * @author pc
 */
public class ValueVoucherDetailActivity extends BaseActivity implements OnClickListener {
    private Button bt_no_activation, bt_yes_activation;
    private TextView tv_value_voucher_money, tv_card_type, tv_from, tv_tv_start_time, tv_end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(ValueVoucherDetailActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_valuevoucher_detail);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        setTitle("优惠券详情");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_no_activation = (Button) findViewById(R.id.bt_no_activation);
        bt_no_activation.setOnClickListener(this);
        bt_yes_activation = (Button) findViewById(R.id.bt_yes_activation);
        bt_yes_activation.setOnClickListener(this);

        tv_value_voucher_money = (TextView) findViewById(R.id.tv_value_voucher_money);
        tv_card_type = (TextView) findViewById(R.id.tv_card_type);
        tv_from = (TextView) findViewById(R.id.tv_from);
        tv_tv_start_time = (TextView) findViewById(R.id.tv_tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_no_activation:
                ToastUtils.show(ValueVoucherDetailActivity.this, "未激活");
                break;
            case R.id.bt_yes_activation:
                ToastUtils.show(ValueVoucherDetailActivity.this, "已激活");
                break;

            default:
                break;
        }
    }

}
