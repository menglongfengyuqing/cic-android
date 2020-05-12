package com.ztmg.cicmorgan.investment.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.MessageNoticeEntity;
import com.ztmg.cicmorgan.investment.entity.ImgEntity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImgAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<ImgEntity> imgList;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	public ImgAdapter(Context context, List<ImgEntity> list) {
		this.mContext = context;
		this.imgList = list;
		
		mInflater=LayoutInflater.from(mContext);
		mImageLoader=ImageLoaderUtil.getImageLoader();
		mDisplayImageOptions=ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);
	} 

	@Override
	public int getCount() {
		return imgList.size();
	}

	@Override
	public Object getItem(int position) {
		return imgList.get(position);
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
			convertView = View.inflate(mContext, R.layout.item_horizontalllistview_item, null);
			holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImgEntity entity = (ImgEntity) getItem(position);
		mImageLoader.displayImage(entity.getImg(), holder.iv_img, mDisplayImageOptions);
		return convertView;
	}
	private class ViewHolder{
		ImageView iv_img;
	}

}
