package com.ztmg.cicmorgan.account.adapter;

import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.AnsactionRecordsAdapter.ViewHolder;
import com.ztmg.cicmorgan.account.entity.AnsactionRecordsEntity;
import com.ztmg.cicmorgan.account.entity.CardEntity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 优惠券
 * @author pc
 *
 */
public class CardAdapter extends BaseAdapter{
	private Context mContext;
	private List<CardEntity> cardList;
	private String isUse;//"1"可使用"3"已过期"2"已使用
	private String valueType;//1抵用券2加息券

	public CardAdapter(Context context, List<CardEntity> list,String isUse,String type) {
		this.mContext = context;
		this.cardList = list;
		this.isUse = isUse;
		this.valueType = type;
	} 

	@Override
	public int getCount() {
		return cardList.size();
	}

	@Override
	public Object getItem(int position) {
		return cardList.get(position);
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
			convertView = View.inflate(mContext, R.layout.fragment_item_card, null);
			holder.ll_allbg = (LinearLayout) convertView.findViewById(R.id.ll_allbg);//大背景
			holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			holder.tv_value_voucher_money = (TextView) convertView.findViewById(R.id.tv_value_voucher_money);//优惠券金额
			holder.tv_range_availability = (TextView) convertView.findViewById(R.id.tv_range_availability);//优惠券使用的范围
			holder.tv_once_money = (TextView) convertView.findViewById(R.id.tv_once_money);//优惠券起投金额
			//			holder.tv_value_voucher_time = (TextView) convertView.findViewById(R.id.tv_value_voucher_time);
			//			holder.tv_value_voucher_due_time = (TextView) convertView.findViewById(R.id.tv_value_voucher_due_time);
			//			holder.tv_yuan = (TextView) convertView.findViewById(R.id.tv_yuan);
			//			holder.tv_from = (TextView) convertView.findViewById(R.id.tv_from);
			holder.tv_use_date = (TextView) convertView.findViewById(R.id.tv_use_date);//使用时间
			//			holder.tv_card_type = (TextView) convertView.findViewById(R.id.tv_card_type);
			holder.iv_voucher_state = (ImageView) convertView.findViewById(R.id.iv_voucher_state);//优惠券状态，已过期与已使用
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==cardList.size()-1){
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
		CardEntity entity = (CardEntity) getItem(position);
		if(isUse.equals("3")){//已过期
			if(entity.getType().equals("1")&&valueType.equals("1")){//抵用券已过期
				holder.ll_allbg.setBackgroundResource(R.drawable.bg_value_voucher_of_date);
				holder.iv_voucher_state.setBackgroundResource(R.drawable.pic_value_voucher_of_date);
				holder.tv_text.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				//				holder.tv_card_type.setTextColor(mContext.getResources().getColor(R.color.new_gray));
				holder.tv_value_voucher_money.setText("¥"+(int)Double.parseDouble(entity.getValue()));
				holder.tv_value_voucher_money.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				holder.tv_range_availability.setText(entity.getSpans());
				holder.tv_range_availability.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				holder.tv_once_money.setText("投资满"+(int)Double.parseDouble(entity.getLimitAmount())+"可用");
				holder.tv_once_money.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				String getDate = entity.getGetDate().substring(0, entity.getGetDate().length()-9);
				String OverdueDate = entity.getOverdueDate().substring(0, entity.getOverdueDate().length()-8);
				holder.tv_use_date.setText(getDate+"至"+OverdueDate);
			}
		}else if(isUse.equals("1")){//可使用
			if(entity.getType().equals("1")&&valueType.equals("1")){//抵用券可使用
				//				holder.tv_card_type.setTextColor(mContext.getResources().getColor(R.color.red2));
				holder.ll_allbg.setBackgroundResource(R.drawable.bg_no_blue_line);
				holder.iv_voucher_state.setVisibility(View.GONE);
				holder.tv_text.setTextColor(mContext.getResources().getColor(R.color.blue));
				holder.tv_value_voucher_money.setText("¥"+(int)Double.parseDouble(entity.getValue()));
				holder.tv_value_voucher_money.setTextColor(mContext.getResources().getColor(R.color.text_303030));
				holder.tv_range_availability.setText(entity.getSpans());
				holder.tv_range_availability.setTextColor(mContext.getResources().getColor(R.color.text_666666));
				holder.tv_once_money.setText("出借满"+(int)Double.parseDouble(entity.getLimitAmount())+"元可用");
				holder.tv_once_money.setTextColor(mContext.getResources().getColor(R.color.text_666666));
				String getDate = entity.getGetDate().substring(0, entity.getGetDate().length()-9);
				String OverdueDate = entity.getOverdueDate().substring(0, entity.getOverdueDate().length()-8);
				holder.tv_use_date.setText(getDate+"至"+OverdueDate);
			}
		}else if(isUse.equals("2")){//已使用

			if(entity.getType().equals("1")&&valueType.equals("1")){//抵用券已使用
				holder.ll_allbg.setBackgroundResource(R.drawable.bg_value_voucher_of_date);
				holder.iv_voucher_state.setBackgroundResource(R.drawable.pic_value_voucher_use_of);
				holder.tv_text.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				//				holder.tv_card_type.setTextColor(mContext.getResources().getColor(R.color.new_gray));
				holder.tv_value_voucher_money.setText("¥"+(int)Double.parseDouble(entity.getValue()));
				holder.tv_value_voucher_money.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				holder.tv_range_availability.setText(entity.getSpans());
				holder.tv_range_availability.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				holder.tv_once_money.setText("出借满"+(int)Double.parseDouble(entity.getLimitAmount())+"元可用");
				holder.tv_once_money.setTextColor(mContext.getResources().getColor(R.color.main_bellow_text_no));
				String getDate = entity.getGetDate().substring(0, entity.getGetDate().length()-9);
				String OverdueDate = entity.getOverdueDate().substring(0, entity.getOverdueDate().length()-8);
				holder.tv_use_date.setText(getDate+"至"+OverdueDate);
			}
		}

		return convertView;
	}

	public class ViewHolder{
		private TextView tv_value_voucher_money,tv_once_money,tv_tips,tv_use_date,tv_text,tv_range_availability;
		private ImageView iv_voucher_state;
		private LinearLayout ll_allbg;
	}
}
