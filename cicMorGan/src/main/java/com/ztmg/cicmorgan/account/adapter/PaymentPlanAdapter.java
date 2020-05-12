package com.ztmg.cicmorgan.account.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.PaymentPlanEntity;
import com.ztmg.cicmorgan.investment.adapter.PaymentPlanAdapter.ViewHolder;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 回款计划
 * @author pc
 *
 */
public class PaymentPlanAdapter extends BaseAdapter{
	private Context mContext;
	private List<PaymentPlanEntity> paymentPlanList;

	public PaymentPlanAdapter(Context context, List<PaymentPlanEntity> list) {
		this.mContext = context;
		this.paymentPlanList = list;
	}
	@Override
	public int getCount() {
		return paymentPlanList.size();
	}

	@Override
	public Object getItem(int position) {
		return paymentPlanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.activity_payment_plan_item, null);
			holder = new ViewHolder();
			holder.tv_payment_money = (TextView) convertView.findViewById(R.id.tv_payment_money);
			holder.tv_payment_num = (TextView) convertView.findViewById(R.id.tv_payment_num);
			holder.tv_payment_time = (TextView) convertView.findViewById(R.id.tv_payment_time);
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==paymentPlanList.size()-1){
			holder.tv_tips.setVisibility(View.VISIBLE);
		}else{
			holder.tv_tips.setVisibility(View.GONE);
		}
		PaymentPlanEntity entity = (PaymentPlanEntity) getItem(position);
		holder.tv_payment_money.setText(entity.getAmount()+"元");
		String state = entity.getState();
		if(state.equals("2")){
			holder.tv_payment_num.setText("正在还款");
		}else if(state.equals("3")){
			holder.tv_payment_num.setText("已经还款");
		}
		holder.tv_payment_time.setText(entity.getDtime());
		return convertView;
	}

	public class ViewHolder{
		private TextView tv_payment_money,tv_payment_num,tv_payment_time,tv_tips;
	}
}
