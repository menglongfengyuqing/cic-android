package com.ztmg.cicmorgan.more.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.more.adapter.ActionCenterAdapter.ViewHolder;
import com.ztmg.cicmorgan.more.entity.ActionCenterEntity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;

public class ActionListAdapter extends BaseAdapter{
	private Context mContext;
	private List<ActionCenterEntity> actionList;


	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;

	public ActionListAdapter(Context context, List<ActionCenterEntity> list) {
		this.mContext = context;
		this.actionList = list;

		mInflater=LayoutInflater.from(mContext);
		mImageLoader=ImageLoaderUtil.getImageLoader();
		//pic_investment_detail
		mDisplayImageOptions=ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);

	}

	@Override
	public int getCount() {
		return actionList.size();
	}

	@Override
	public Object getItem(int position) {
		return actionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_action_list, null);
			holder = new ViewHolder();
			holder.iv_action = (ImageView) convertView.findViewById(R.id.iv_action);
			holder.txt_tips = (TextView) convertView.findViewById(R.id.txt_tips);
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==actionList.size()-1){
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
		ActionCenterEntity entity = (ActionCenterEntity) getItem(position);
		mImageLoader.displayImage(entity.getImgUrl(), holder.iv_action, mDisplayImageOptions);
		if(entity.getType().equals("0")){//0下线 1-上线
			holder.txt_tips.setText("已结束");
		}else if(entity.getType().equals("1")){
			holder.txt_tips.setText("进行中");
		}

		return convertView;
	}
	public class ViewHolder{
		private ImageView iv_action;
		private TextView txt_tips;
		private TextView tv_tips;
	}
}

