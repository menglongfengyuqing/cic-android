package com.ztmg.cicmorgan.test;

import android.os.Bundle;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivityCom;

/**
 * Created by dongdong on 2018/8/7.
 */

public class TestActivity extends BaseActivityCom {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        SmartRefreshLayout refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(true);
        //refreshLayout.setEnableNestedScroll(true);


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


}
