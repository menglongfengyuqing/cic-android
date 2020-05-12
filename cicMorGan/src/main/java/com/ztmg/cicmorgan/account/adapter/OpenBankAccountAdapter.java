package com.ztmg.cicmorgan.account.adapter;

import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.BankAccountEntity;
import com.ztmg.cicmorgan.account.entity.CardEntity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 添加开户行
 * @author pc
 *
 */
public class OpenBankAccountAdapter extends BaseAdapter{
	private Context mContext;
	private List<BankAccountEntity> bankAccountList;

	public OpenBankAccountAdapter(Context context, List<BankAccountEntity> list) {
		this.mContext = context;
		this.bankAccountList = list;
	} 

	@Override
	public int getCount() {
		return bankAccountList.size();
	}

	@Override
	public Object getItem(int position) {
		return bankAccountList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.activity_item_bank_account, null);
			holder = new ViewHolder();
			holder.iv_pic_bank = (ImageView) convertView.findViewById(R.id.iv_pic_bank);
			holder.tv_bank_name = (TextView) convertView.findViewById(R.id.tv_bank_name);
			holder.tv_bank_value = (TextView) convertView.findViewById(R.id.tv_bank_value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BankAccountEntity entity = (BankAccountEntity) getItem(position);
//		holder.tv_bank_name.setText(entity.);
		//限额：单笔20万，单笔20万
//		holder.tv_bank_value.setText(resid)
		return convertView;
	}
	
	public class ViewHolder{
		private ImageView iv_pic_bank;
		private TextView tv_bank_name,tv_bank_value;
	}
}
