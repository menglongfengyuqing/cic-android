package com.ztmg.cicmorgan.account.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.fragment.AfterThreeMouthFragment;
import com.ztmg.cicmorgan.investment.fragment.FrontThreeMouthFragment;
import com.ztmg.cicmorgan.investment.fragment.MiddleThreeMouthFragment;

/**
 * 我的出借安心投
 *
 * @author pc
 */
public class SafeInvestFragment extends Fragment implements OnClickListener {

    private View view;
    private InPossessionFragment mInPossessionFragment;//募集中
    private DaysDueFragment mDaysDueFragment;//回款中
    private EndedFragment mEndedFragment;//已结束

    private View text_inpossesson_line, text_daysdue_line, text_ended_line;

    private TextView tv_inpossesson, tv_daysdue, tv_ended;
    private RelativeLayout rl_inpossesson, rl_daysdue, rl_ended;
    private FragmentTransaction Ft_inpossesson, Ft_daysdue, Ft_ended;

    private String type;

    //	public SafeInvestFragment(String type){
    //		this.type = type;
    //	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_supply_chain, null);

        Bundle typeBundle = getArguments();
        type = typeBundle.getString("state");

        Bundle inPossessionBundle = new Bundle();
        inPossessionBundle.putString("type", type);
        mInPossessionFragment = new InPossessionFragment();
        mInPossessionFragment.setArguments(inPossessionBundle);

        Bundle daysDueBundle = new Bundle();
        daysDueBundle.putString("type", type);
        mDaysDueFragment = new DaysDueFragment();
        mDaysDueFragment.setArguments(daysDueBundle);

        Bundle EndedBundle = new Bundle();
        EndedBundle.putString("type", type);
        mEndedFragment = new EndedFragment();
        mEndedFragment.setArguments(EndedBundle);

        Ft_inpossesson = getFragmentManager().beginTransaction();
        Ft_inpossesson.replace(R.id.fl_safe_invest, mInPossessionFragment);
        Ft_inpossesson.commit();

        initView(view);
        return view;
    }

    private void initView(View view) {
        rl_inpossesson = (RelativeLayout) view.findViewById(R.id.rl_inpossesson);
        rl_daysdue = (RelativeLayout) view.findViewById(R.id.rl_daysdue);
        rl_ended = (RelativeLayout) view.findViewById(R.id.rl_ended);

        text_inpossesson_line = view.findViewById(R.id.text_inpossesson_line);
        text_daysdue_line = view.findViewById(R.id.text_daysdue_line);
        text_ended_line = view.findViewById(R.id.text_ended_line);

        tv_inpossesson = (TextView) view.findViewById(R.id.tv_inpossesson);
        tv_daysdue = (TextView) view.findViewById(R.id.tv_daysdue);
        tv_ended = (TextView) view.findViewById(R.id.tv_ended);

        rl_inpossesson.setOnClickListener(this);
        rl_daysdue.setOnClickListener(this);
        rl_ended.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_inpossesson:
                tv_inpossesson.setTextColor(getResources().getColor(R.color.blue));
                tv_daysdue.setTextColor(getResources().getColor(R.color.text_666666));
                tv_ended.setTextColor(getResources().getColor(R.color.text_666666));

                text_inpossesson_line.setVisibility(View.VISIBLE);
                text_daysdue_line.setVisibility(View.INVISIBLE);
                text_ended_line.setVisibility(View.INVISIBLE);

                Ft_inpossesson = getFragmentManager().beginTransaction();
                Ft_inpossesson.replace(R.id.fl_safe_invest, mInPossessionFragment);
                Ft_inpossesson.commit();
                break;
            case R.id.rl_daysdue:
                tv_inpossesson.setTextColor(getResources().getColor(R.color.text_666666));
                tv_daysdue.setTextColor(getResources().getColor(R.color.blue));
                tv_ended.setTextColor(getResources().getColor(R.color.text_666666));

                text_inpossesson_line.setVisibility(View.INVISIBLE);
                text_daysdue_line.setVisibility(View.VISIBLE);
                text_ended_line.setVisibility(View.INVISIBLE);

                Ft_daysdue = getFragmentManager().beginTransaction();
                Ft_daysdue.replace(R.id.fl_safe_invest, mDaysDueFragment);
                Ft_daysdue.commit();
                break;
            case R.id.rl_ended:
                tv_inpossesson.setTextColor(getResources().getColor(R.color.text_666666));
                tv_daysdue.setTextColor(getResources().getColor(R.color.text_666666));
                tv_ended.setTextColor(getResources().getColor(R.color.blue));

                text_inpossesson_line.setVisibility(View.INVISIBLE);
                text_daysdue_line.setVisibility(View.INVISIBLE);
                text_ended_line.setVisibility(View.VISIBLE);

                Ft_ended = getFragmentManager().beginTransaction();
                Ft_ended.replace(R.id.fl_safe_invest, mEndedFragment);
                Ft_ended.commit();
                break;

            default:
                break;
        }
    }
}
