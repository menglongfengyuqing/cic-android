package com.ztmg.cicmorgan.investment.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BasePager;
import com.ztmg.cicmorgan.base.recyclerview.BaseRecyclerAdapter;
import com.ztmg.cicmorgan.base.recyclerview.RecyclerViewHolder;
import com.ztmg.cicmorgan.entity.InvestmentDetailEntity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.view.FullyGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 出借记录 fragment
 */
public class RecordingFragment extends BasePager {
    public InvestmentDetailBorrowActivity activity;
    private RecyclerView mRecyclerView;
    private BaseRecyclerAdapter RecyclerAdapter;
    private String projectid;
    private List<InvestmentDetailEntity.DataBean.BidListBean> bidList = new ArrayList<>();
    private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearLayout;

    public RecordingFragment(int index, Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("--------------------------7------------------------");

        View view = inflater.inflate(R.layout.fragment_recording_item, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new FullyGridLayoutManager(context, 1, FullyGridLayoutManager.VERTICAL, false));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_no_card);//没有出借记录
        mLinearLayout = (LinearLayout) view.findViewById(R.id.line);
        return view;
    }


    @Override
    public void initData(String string) {
        LogUtil.e("--------------------------8------------------------");

        projectid = string;
        String json = ACache.get(context).getAsString(Constant.InvestmentDetailKey);
        InvestmentDetailEntity investmentDetailEntity = GsonManager.fromJson(json, InvestmentDetailEntity.class);
        InvestmentDetailEntity.DataBean data = investmentDetailEntity.getData();
        //data.getBidList().clear();//测试
        if (!Lists.isEmpty(data.getBidList())) {
            bidList = data.getBidList();
            RecyclerViewAdapter(bidList);
        } else {
            mRelativeLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * RecyclerView 还款计划  需要单独调用接口
     *
     * @param bidList
     */
    private void RecyclerViewAdapter(final List<InvestmentDetailEntity.DataBean.BidListBean> bidList) {
        if (RecyclerAdapter == null) {
            RecyclerAdapter = new BaseRecyclerAdapter<InvestmentDetailEntity.DataBean.BidListBean>(context, bidList) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.activity_item_investment;
                }

                @Override
                public void bindData(RecyclerViewHolder holder, int position, InvestmentDetailEntity.DataBean.BidListBean item) {
                    holder.getTextView(R.id.tv_investment_user).setText(item.getUserPhone());
                    holder.getTextView(R.id.tv_investment_money).setText(item.getInvestAmount() + "元");
                    holder.getTextView(R.id.tv_investment_time).setText(item.getInvestDate());
                    holder.getView(R.id.mView).setVisibility(View.GONE);
                }
            };
            mRecyclerView.setAdapter(RecyclerAdapter);
            // RecyclerAdapter.setOnItemClickListener(this);
        } else {
            if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) {
                RecyclerAdapter.notifyDataSetChanged();
            }
        }
    }
}
