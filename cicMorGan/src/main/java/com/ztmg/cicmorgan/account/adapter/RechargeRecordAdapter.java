package com.ztmg.cicmorgan.account.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.BindBankCardActivity;
import com.ztmg.cicmorgan.account.entity.AnsactionRecordsEntity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.LoginUserProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 充值记录
 * @author pc
 *
 */
public class RechargeRecordAdapter extends BaseAdapter{

	private Context mContext;
	private List<AnsactionRecordsEntity> ansactionRecordList;

	public RechargeRecordAdapter(Context context, List<AnsactionRecordsEntity> list) {
		this.mContext = context;
		this.ansactionRecordList = list;
	}

	@Override
	public int getCount() {
		return ansactionRecordList.size();
	}

	@Override
	public Object getItem(int position) {
		return ansactionRecordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.activity_item_recharge_records, null);
			holder = new ViewHolder();
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_remain_sum_money = (TextView) convertView.findViewById(R.id.tv_remain_sum_money);
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==ansactionRecordList.size()-1){
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
		AnsactionRecordsEntity entity = (AnsactionRecordsEntity) getItem(position);
		DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
		String amountDot = decimalFormat.format(Double.parseDouble(entity.getAmount()));
		String balancemoneyDot = decimalFormat.format(Double.parseDouble(entity.getBalancemoney()));
		holder.tv_date.setText(entity.getTranddate());
		holder.tv_money.setText(amountDot);
		holder.tv_remain_sum_money.setText(balancemoneyDot+"元");

		return convertView;
	}

	public class ViewHolder{
		private TextView tv_date,tv_money,tv_remain_sum_money,tv_tips;

	}
	// 没有绑定银行卡提示框
	private void dialog(){

		final Dialog mdialog = new Dialog(mContext, R.style.MyDialog);
		mdialog.setContentView(R.layout.dl_isbindbank);

		TextView tv_yes = (TextView) mdialog.findViewById(R.id.tv_yes);
		TextView tv_no = (TextView) mdialog.findViewById(R.id.tv_no);
		ImageView iv_dialog_close = (ImageView) mdialog.findViewById(R.id.iv_dialog_close);
		tv_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//跳转到绑定银行卡界面
				mContext.startActivity(new Intent(mContext,BindBankCardActivity.class));
				mdialog.dismiss();
			}
		});
		tv_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//不绑定银行卡，关闭弹框
				mdialog.dismiss();
			}
		});
		iv_dialog_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mdialog.dismiss();
			}
		});
		mdialog.show();
	}
}
