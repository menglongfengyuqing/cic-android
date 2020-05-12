package com.ztmg.cicmorgan.more.adapter;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.more.entity.CoreEnterpriseEntity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CoreEnterpriseAdapter extends BaseAdapter{

	private Context mContext;
	private List<CoreEnterpriseEntity> coreEnterpriseList;


	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;

	public CoreEnterpriseAdapter(Context context, List<CoreEnterpriseEntity> list) {
		this.mContext = context;
		this.coreEnterpriseList = list;

		mInflater=LayoutInflater.from(mContext);
		mImageLoader=ImageLoaderUtil.getImageLoader();
		mDisplayImageOptions=ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);

	}

	@Override
	public int getCount() {
		return coreEnterpriseList.size();
	}

	@Override
	public Object getItem(int position) {
		return coreEnterpriseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_core_enterprise_list, null);
			holder = new ViewHolder();
			holder.iv_core_enterprise = (ImageView) convertView.findViewById(R.id.iv_core_enterprise);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CoreEnterpriseEntity entity = (CoreEnterpriseEntity) getItem(position);
		if(entity.getImg()!="more"){
			mImageLoader.displayImage(entity.getImg(), holder.iv_core_enterprise, mDisplayImageOptions);
		}else if(entity.getImg()=="more"){
			holder.iv_core_enterprise.setBackgroundResource(R.drawable.pic_mall_enterprise_moretocome);
		}

//		if(!TextUtils.isEmpty(entity.getRemark())){
//			holder.iv_core_enterprise.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					String path = entity.getRemark();
//					if(!TextUtils.isEmpty(path)&&!path.equals("null")&&path!=null){
//						Intent coreEnterpriseIntent = new Intent(mContext,AgreementActivity.class);
//						coreEnterpriseIntent.putExtra("path", path);
//						coreEnterpriseIntent.putExtra("title", entity.getName());
//						mContext.startActivity(coreEnterpriseIntent);
//					}
//				}
//			});
//		}

		return convertView;
	}
	public class ViewHolder{
		private ImageView iv_core_enterprise;
	}
}
