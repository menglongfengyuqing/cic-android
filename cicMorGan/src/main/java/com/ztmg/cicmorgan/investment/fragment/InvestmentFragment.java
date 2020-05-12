package com.ztmg.cicmorgan.investment.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseFragment;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

/**
 * 投资
 *
 * @author pc
 */
public class InvestmentFragment extends BaseFragment implements OnClickListener {
    private View view;
    private SupplyChainInvestmentFragment supplyChainInvestmentFragment;
    private SafeInvestmentFragment safeInvestmentFragment;
    private FragmentTransaction Ft_font, Ft_middle;
    private RelativeLayout ll_front, ll_middle;
    private TextView tx_front, tx_middle;
    private TextView text_front_line, text_middle_line;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }


        view = inflater.inflate(R.layout.frgment_investment, null);
        //(0:安心投:1:新手标2:供应链3:推荐标)
        //1：安心投类，2：供应链类
        supplyChainInvestmentFragment = new SupplyChainInvestmentFragment();
        safeInvestmentFragment = new SafeInvestmentFragment();
        Ft_font = getFragmentManager().beginTransaction();
        Ft_font.replace(R.id.rl_investment_container, supplyChainInvestmentFragment);
        Ft_font.commit();
        initView(view);
        inital();
        return view;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initView(View v) {
        ll_front = (RelativeLayout) v.findViewById(R.id.ll_front);
        ll_middle = (RelativeLayout) v.findViewById(R.id.ll_middle);

        tx_front = (TextView) v.findViewById(R.id.tx_front);
        tx_middle = (TextView) v.findViewById(R.id.tx_middle);

        text_front_line = (TextView) v.findViewById(R.id.text_front_line);
        text_middle_line = (TextView) v.findViewById(R.id.text_middle_line);
    }

    private void inital() {

        ll_front.setOnClickListener(this);
        ll_middle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_front:
                text_front_line.setVisibility(View.VISIBLE);
                text_middle_line.setVisibility(View.INVISIBLE);

                tx_front.setTextColor(getResources().getColor(R.color.text_a11c3f));
                tx_middle.setTextColor(getResources().getColor(R.color.text_34393c));

                Ft_font = getFragmentManager().beginTransaction();
                Ft_font.replace(R.id.rl_investment_container, supplyChainInvestmentFragment);
                Ft_font.commit();
                break;
            case R.id.ll_middle:
                text_front_line.setVisibility(View.INVISIBLE);
                text_middle_line.setVisibility(View.VISIBLE);

                tx_front.setTextColor(getResources().getColor(R.color.text_34393c));
                tx_middle.setTextColor(getResources().getColor(R.color.text_a11c3f));

                Ft_middle = getFragmentManager().beginTransaction();
                Ft_middle.replace(R.id.rl_investment_container, safeInvestmentFragment);
                Ft_middle.commit();
                break;
            default:
                break;
        }
    }

}
