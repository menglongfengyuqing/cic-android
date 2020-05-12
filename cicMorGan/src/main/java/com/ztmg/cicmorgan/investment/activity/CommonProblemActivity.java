package com.ztmg.cicmorgan.investment.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 常见问题
 * Created by dell on 2018/5/21.
 */

public class CommonProblemActivity extends BaseActivity {

    private TextView tv_problem_title, tv_problem_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activty_common_problem);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(CommonProblemActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        setTitle("常见问题");
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //安心投供应链需区分
        //供应链：供应链金融项目安全么？  扶持优质资产端，锁定核心企业供应链，为企业提供应收/应付账款管理和信用输出、批量为核心企业上下游提供供应链金融综合解决方案，核心企业信用背书项目风险低，贸易真实，透明，回款稳定。
        //安心投：房产抵押项目安全吗？  中投摩根以严谨负责的态度对待每笔借款进行严格筛选，抵押物全部为北京房产，并且为首次足值抵押，支付与海口银行进行合作，保证资金去向透明可追踪，每笔出借信息真实有效。
        tv_problem_title = (TextView) findViewById(R.id.tv_problem_title);//第一个问题的题目
        tv_problem_content = (TextView) findViewById(R.id.tv_problem_content);//第一个问题的答案


        //判断协议范本H5地址
        if (!StringUtils.isBlank(ACache.get(CommonProblemActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key))) {
            String s = ACache.get(CommonProblemActivity.this).getAsString(Constant.Investment_Detail_SupplyChain_Relieved_Key);
            if (s.equals("1")) {
                tv_problem_title.setText("供应链金融项目安全么？");
                tv_problem_content.setText("扶持优质资产端，锁定核心企业供应链，为企业提供应收/应付账款管理和信用输出、批量为核心企业上下游提供供应链金融综合解决方案，核心企业信用背书项目贸易真实透明并按时回款。");
            } else if (s.equals("2")) {
                tv_problem_title.setText("房产抵押项目安全吗？");
                tv_problem_content.setText("中投摩根以严谨负责的态度对待每笔借款进行严格筛选，抵押物全部为北京房产，并且为首次足值抵押，支付与海口银行进行合作，保证资金去向透明可追踪，每笔出借信息真实有效。");
            } else if (s.equals("3")) {
                String chujie = ACache.get(mContext).getAsString(Constant.SupplyChainInvestmentKey_mine_chujie);
                if (chujie.equals("0")) {
                    tv_problem_title.setText("供应链金融项目安全么？");
                    tv_problem_content.setText("扶持优质资产端，锁定核心企业供应链，为企业提供应收/应付账款管理和信用输出、批量为核心企业上下游提供供应链金融综合解决方案，核心企业信用背书项目贸易真实透明并按时回款。");
                } else if (chujie.equals("1")) {
                    tv_problem_title.setText("房产抵押项目安全吗？");
                    tv_problem_content.setText("中投摩根以严谨负责的态度对待每笔借款进行严格筛选，抵押物全部为北京房产，并且为首次足值抵押，支付与海口银行进行合作，保证资金去向透明可追踪，每笔出借信息真实有效。");
                }
            }
        }
    }

    @Override
    protected void initData() {

    }
}
