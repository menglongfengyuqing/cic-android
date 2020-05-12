package com.ztmg.cicmorgan.test.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.test.entity.TestTitleEntity;
import com.ztmg.cicmorgan.test.view.ListViewAdapter.OnChildClickListening;

import java.util.List;

public class TestRollViewPager extends PagerAdapter implements OnChildClickListening {

    private Context ctx;
    private List<TestTitleEntity> titleList;
    private LayoutInflater mInflater;
    private TextView tv_title;
    private RadioGroup rg_test;
    private OnTurnListening mListening;
    private LinearLayout ll_bt_next;

    public OnTurnListening getmListening() {
        return mListening;
    }

    public void setmListening(OnTurnListening mListening) {
        this.mListening = mListening;
    }

    public TestRollViewPager(Context ctx, List<TestTitleEntity> titleList) {
        this.ctx = ctx;
        this.titleList = titleList;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mInflater.inflate(R.layout.activity_test_first, null);
        LinearLayout back_on = (LinearLayout) view.findViewById(R.id.back_on);
        TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
        TextView tv_sum = (TextView) view.findViewById(R.id.tv_sum);
        ListView listView = (ListView) view.findViewById(R.id.lv);
        ListViewAdapter mAdapter = new ListViewAdapter(ctx, position, titleList.get(position).getTestOption());
        mAdapter.setmOnChildClickListening(this);
        listView.setAdapter(mAdapter);
        container.addView(view);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(titleList.get(position).getName());
        tv_number.setText(position + 1 + "");
        tv_sum.setText(titleList.size() + "");
        ll_bt_next = (LinearLayout) view.findViewById(R.id.ll_bt_next);
        if (position == titleList.size() - 1) {
            ll_bt_next.setVisibility(View.VISIBLE);
        } else {
            ll_bt_next.setVisibility(View.GONE);
        }
        if (position == 0||position == titleList.size()-1) {
            back_on.setVisibility(View.INVISIBLE);
        } else {
            back_on.setVisibility(View.VISIBLE);
        }
        back_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListening.onUp(position);
            }
        });
        ll_bt_next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListening.onNextQuestion();
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return titleList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void onChildClick(int parentPos, int childPos, boolean flag) {
        //if (parentPos == 9) {
        //    if (flag) {
        //        bnt_next.setBackgroundResource(R.drawable.bt_risk_ok_2);
        //        bnt_next.setEnabled(true);
        //    }
        //}
        mListening.onRefresh(parentPos, childPos, flag);
        mListening.onDown(parentPos);
    }

    public interface OnTurnListening {
        void onDown(int position);

        void onUp(int position);

        void onRefresh(int parentPos, int childPos, boolean flag);

        void onNextQuestion();
    }
}
