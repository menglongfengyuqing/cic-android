package com.ztmg.cicmorgan.account.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.OpenBankAccountAdapter;
import com.ztmg.cicmorgan.account.entity.BankAccountEntity;
import com.ztmg.cicmorgan.base.BaseActivity;

/**
 * 添加开户行
 *
 * @author pc
 */
public class OpenBankAccountActivity extends BaseActivity {
    private ListView lv_bank_account;
    private OpenBankAccountAdapter adapter;
    private List<BankAccountEntity> bankAccountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_open_bank_account);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        setTitle("添加开户行");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_bank_account = (ListView) findViewById(R.id.lv_bank_account);
    }

    @Override
    protected void initData() {

    }
}
