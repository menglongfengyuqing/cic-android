package com.ztmg.cicmorgan.account.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.CardEntity;
import com.ztmg.cicmorgan.account.entity.MyInvestmentDetailPaymentEntity;

/**
 * 我的投资详情添加回款列表adapter
 *
 * @author pc
 */
public class MyInvestmentDetailPaymentAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyInvestmentDetailPaymentEntity> paymentList;

    public MyInvestmentDetailPaymentAdapter(Context context, List<MyInvestmentDetailPaymentEntity> list) {
        this.mContext = context;
        this.paymentList = list;
    }

    @Override
    public int getCount() {
        return paymentList.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.activity_my_investment_payment_item, null);
            holder.tv_payment_state = (TextView) convertView.findViewById(R.id.tv_payment_state);//还款状态
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);//还款时间
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);//还款金额
            holder.tv_investment_detail_payment_type = (TextView) convertView.findViewById(R.id.tv_investment_detail_payment_type);//还款类型
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
        if (paymentList.get(position).getState().equals("2")) {
            holder.tv_payment_state.setBackgroundResource(R.drawable.bg_ellipse_gray);
            holder.tv_payment_state.setText("未还款");// 2 未还款 3 已还款
        } else if (paymentList.get(position).getState().equals("3")) {
            holder.tv_payment_state.setBackgroundResource(R.drawable.bg_ellipse_blue);
            holder.tv_payment_state.setText("正常还款");// 2 未还款 3 已还款
        }
        holder.tv_date.setText(paymentList.get(position).getRepaymentDate());
        holder.tv_money.setText(decimalFormat.format(Double.parseDouble(paymentList.get(position).getAmount())));
        if (paymentList.get(position).getPrincipal().equals("2")) {//2利息  1本息
            holder.tv_investment_detail_payment_type.setText("利息");
        } else if (paymentList.get(position).getPrincipal().equals("1")) {
            holder.tv_investment_detail_payment_type.setText("本金");
        }

        return convertView;
    }

    public class ViewHolder {
        private TextView tv_payment_state, tv_date, tv_money, tv_investment_detail_payment_type;
    }
}
