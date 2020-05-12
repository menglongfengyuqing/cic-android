package com.ztmg.cicmorgan.account.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.RequestFriendsAdapter.ViewHolder;
import com.ztmg.cicmorgan.account.entity.RequestFriendsEntity;
import com.ztmg.cicmorgan.account.entity.SystemMessageEntity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.net.Urls;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 系统消息
 * @author pc
 *
 */
public class SystemMessageAdapter extends BaseAdapter{

	private Context mContext;
	private List<SystemMessageEntity> systemMessageList;
	public SystemMessageAdapter(Context context, List<SystemMessageEntity> list) {
		this.mContext = context;
		this.systemMessageList = list;
	}

	@Override
	public int getCount() {
		return systemMessageList.size();
	}

	@Override
	public Object getItem(int position) {
		return systemMessageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_system_message, null);
			holder = new ViewHolder();
//			holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
//			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
//			holder.tv_get_date = (TextView) convertView.findViewById(R.id.tv_get_date);
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==systemMessageList.size()-1){
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
		SystemMessageEntity entity = (SystemMessageEntity) getItem(position);
		
		return convertView;
	}

	public class ViewHolder{
		private TextView tv_tips;
	}
	

}
