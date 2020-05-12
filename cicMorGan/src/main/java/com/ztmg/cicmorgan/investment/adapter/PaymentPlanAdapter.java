package com.ztmg.cicmorgan.investment.adapter;

import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.adapter.InvestmentListAdapter.ViewHolder;
import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;
import com.ztmg.cicmorgan.investment.entity.PaymentPlanEntity;
import com.ztmg.cicmorgan.net.Urls;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 还款计划adapter
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
		holder.tv_tips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(mContext,AgreementActivity.class);
				riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
				riskTipFirstIntent.putExtra("title", "风险提示书");
				mContext.startActivity(riskTipFirstIntent);
			}
		});
		PaymentPlanEntity entity = (PaymentPlanEntity) getItem(position);
		holder.tv_payment_money.setText(entity.getAmount()+"元");
		holder.tv_payment_num.setText(entity.getRepaysort());
		holder.tv_payment_time.setText(entity.getRepaydate());
		return convertView;
	}

	public class ViewHolder{
		private TextView tv_payment_money,tv_payment_num,tv_payment_time,tv_tips;
	}
	public String date(String str){
		str = str.substring(0,str.length()-9);
		return str;
	}
}
