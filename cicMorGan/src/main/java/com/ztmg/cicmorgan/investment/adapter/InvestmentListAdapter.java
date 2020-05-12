package com.ztmg.cicmorgan.investment.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.entity.InvestmentEntity;
import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 投资列表
 * @author pc
 *
 */
public class InvestmentListAdapter extends BaseAdapter{

	private Context mContext;
	private List<InvestmentListEntity> investmentList;

	public InvestmentListAdapter(Context context, List<InvestmentListEntity> list) {
		this.mContext = context;
		this.investmentList = list;
	}

	@Override
	public int getCount() {
		return investmentList.size();
	}

	@Override
	public Object getItem(int position) {
		return investmentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.activity_item_investment, null);
			holder = new ViewHolder();
			holder.tv_investment_user = (TextView) convertView.findViewById(R.id.tv_investment_user);
			holder.tv_investment_money = (TextView) convertView.findViewById(R.id.tv_investment_money);
			holder.tv_investment_time = (TextView) convertView.findViewById(R.id.tv_investment_time);
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		if(position==investmentList.size()-1){
//			holder.tv_tips.setVisibility(View.VISIBLE);
//		}else{
//			holder.tv_tips.setVisibility(View.GONE);
//		}
		InvestmentListEntity entity = (InvestmentListEntity) getItem(position);
		holder.tv_investment_user.setText(entity.getName());
		DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
		String amountDot = decimalFormat.format(Double.parseDouble(entity.getAmount()));
		holder.tv_investment_money.setText(amountDot+"元");
		holder.tv_investment_time.setText(date(entity.getCreatedate()));
		return convertView;
	}

	public class ViewHolder{
		private TextView tv_investment_user,tv_investment_money,tv_investment_time,tv_tips;
	}
	public String date(String str){
		str = str.substring(0,str.length()-9);
		return str;
}
}
