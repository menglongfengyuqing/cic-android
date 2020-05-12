package com.ztmg.cicmorgan.account.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.MyInvestmentEntity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 我的投资
 *
 * @author pc
 */
public class MyInvestmentAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyInvestmentEntity> myInvestmentList;
    private String type;
    private String stateType;

    public MyInvestmentAdapter(Context context, List<MyInvestmentEntity> list, String type, String stateType) {
        this.mContext = context;
        this.myInvestmentList = list;
        this.type = type;
        this.stateType = stateType;
    }

    @Override
    public int getCount() {
        return myInvestmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return myInvestmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.fragment_item_my_investment, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_text_money = (TextView) convertView.findViewById(R.id.tv_text_money);
            holder.tv_profit = (TextView) convertView.findViewById(R.id.tv_profit);
            holder.tv_project_state = (TextView) convertView.findViewById(R.id.tv_project_state);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == myInvestmentList.size() - 1) {
            holder.tv_tips.setVisibility(View.GONE);
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

        final MyInvestmentEntity entity = (MyInvestmentEntity) getItem(position);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
        if (type.equals("2")) {//供应链
            holder.tv_name.setText(entity.getProjectName() + "(" + entity.getSn() + ")");
        } else if (type.equals("1")) {//安心投
            holder.tv_name.setText(entity.getProjectName());
        }
        String amountDot = decimalFormat.format(Double.parseDouble(entity.getAmount()));
        holder.tv_text_money.setText(amountDot);
        String profitDot = decimalFormat.format(Double.parseDouble(entity.getInterest()));
        holder.tv_profit.setText(profitDot);
        if (entity.getState().equals("4")) {//募集中
            holder.tv_project_state.setText("募集中");
        } else if (entity.getState().equals("5")) {//回款中
            holder.tv_project_state.setText("回款中");
        } else if (entity.getState().equals("6")) {//回款中
            holder.tv_project_state.setText("回款中");
        } else if (entity.getState().equals("7")) {
            holder.tv_project_state.setText("已结束");
        }
        if (stateType.equals("0")) {//持有中
            //tv_name,tv_text_money,tv_profit
            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
            holder.tv_text_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
            holder.tv_profit.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
            holder.tv_project_state.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
        } else if (stateType.equals("1")) {//30天到期
            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
            holder.tv_text_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
            holder.tv_profit.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
            holder.tv_project_state.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
        } else if (stateType.equals("2")) {//已结束
            holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_989898));
            holder.tv_text_money.setTextColor(mContext.getResources().getColor(R.color.text_989898));
            holder.tv_profit.setTextColor(mContext.getResources().getColor(R.color.text_989898));
            holder.tv_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));
        }
        holder.tv_time.setText(date(entity.getEndDate()) + "到期");
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_name, tv_text_money, tv_profit, tv_time, tv_project_state, tv_tips;
    }

    public String date(String str) {
        if (str.length() > 0)
            str = str.substring(0, str.length() - 9);
        return str;
    }
}
