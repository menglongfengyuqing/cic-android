package com.ztmg.cicmorgan.account.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.entity.InvestmentEntity;
import com.ztmg.cicmorgan.net.Urls;

public class SafeInvestmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvestmentEntity> safeInvestmentList;
    //	private HorizontalProgressBarWithNumber textProgressBar;

    public SafeInvestmentAdapter(Context context, List<InvestmentEntity> list) {
        this.mContext = context;
        this.safeInvestmentList = list;
    }

    @Override
    public int getCount() {
        return safeInvestmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return safeInvestmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder holder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_item_safe_investment, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            holder.tv_term = (TextView) convertView.findViewById(R.id.tv_term);
            holder.iv_sell_out = (TextView) convertView.findViewById(R.id.iv_sell_out);
            holder.iv_new_peopel = (ImageView) convertView.findViewById(R.id.iv_new_peopel);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == safeInvestmentList.size() - 1) {
            holder.tv_tips.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tips.setVisibility(View.GONE);
        }
        holder.tv_tips.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(mContext, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                mContext.startActivity(riskTipFirstIntent);
            }
        });
        InvestmentEntity entity = (InvestmentEntity) getItem(position);
        if (entity.getProjectProductType().equals("1")) {//1安心投2供应链
            holder.tv_name.setText(entity.getName());
        } else if (entity.getProjectProductType().equals("2")) {
            holder.tv_name.setText(entity.getName() + "(" + entity.getSn() + ")");
        }
        holder.tv_num.setText(entity.getRate());
        String span = entity.getSpan();
        holder.tv_term.setText(span + "天");// 项目期限
        //		int spanInt = Integer.parseInt(span);
        //		if(spanInt==15){
        //			holder.tv_term.setText("半个月");// 项目期限
        //		}else{
        //			int mouth = spanInt/30;
        //			holder.tv_term.setText(mouth + "个月");// 项目期限
        //		}
        //状态 0-撤销；1-草稿；2-审核；3-发布；4-上线；5-满标；6-还款中；7-已还完
        if (entity.getState().equals("3")) {
            holder.iv_sell_out.setText("已发布");
        } else if (entity.getState().equals("4")) {
            holder.iv_sell_out.setText("已上线");
        } else if (entity.getState().equals("5")) {
            holder.iv_sell_out.setText("满标");
        } else if (entity.getState().equals("6")) {
            holder.iv_sell_out.setText("还款中");
        } else if (entity.getState().equals("7")) {
            holder.iv_sell_out.setText("已还完");
        }
        //0 - 安心投  1-新手标 2-供应链 3- 推荐标
        //		if(entity.getProjecttype().equals("0")){
        //			holder.iv_new_peopel.setVisibility(View.GONE);
        //		}else if(entity.getProjecttype().equals("1")){
        //			holder.iv_new_peopel.setVisibility(View.VISIBLE);
        //		}
        return convertView;
    }

    public class ViewHolder {
        //底部布局
        private TextView tv_name, tv_num, tv_tips, tv_term;
        private TextView iv_sell_out;
        private ImageView iv_new_peopel;
    }
}
