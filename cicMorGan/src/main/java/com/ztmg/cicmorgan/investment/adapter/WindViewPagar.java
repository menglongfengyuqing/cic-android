package com.ztmg.cicmorgan.investment.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.FullScreenImageActivity;
import com.ztmg.cicmorgan.activity.LookProfessionalQualificationActivity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class WindViewPagar extends PagerAdapter {
	private Context ctx;
	private List<String> imgList;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	public WindViewPagar(Context ctx, List<String> imgList) {
		this.ctx=ctx;
		this.imgList=imgList;
		mInflater=LayoutInflater.from(ctx);
		mImageLoader=ImageLoaderUtil.getImageLoader();
		mDisplayImageOptions=ImageLoaderUtil.getDisplayImageOptions(R.drawable.iv_scroll_default, false, false, false);
		
	}

	@Override
	public int getCount() {
		if(imgList!=null&&imgList.size()>0)
		{
			if(imgList.size()%2==1){
				return imgList.size()/2+1;
			}else{
				return imgList.size()/2;
			}
		}else{
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0==arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view=null;
		if(imgList.size()%2==1&& position==imgList.size()/2){
			view = mInflater.inflate(R.layout.item_viewpager_one, null);
			ImageView iv_one_img=(ImageView) view.findViewById(R.id.iv_one_img);
			iv_one_img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(ctx,FullScreenImageActivity.class);
					 List<String> imageUrls = new ArrayList<String>();
					for (int i = 0; i < imgList.size(); i++) {
						imageUrls.add(imgList.get(i));
					}
					intent.putExtra("currentPosition", imgList.size()-1);
					intent.putStringArrayListExtra("urls",
							(ArrayList<String>) imageUrls);
					intent.setClass(ctx,FullScreenImageActivity.class);
					ctx.startActivity(intent);
				}
			});
			mImageLoader.displayImage(imgList.get(imgList.size()-1), iv_one_img, mDisplayImageOptions);
			((ViewPager)container).addView(view);
		}else{
			view = mInflater.inflate(R.layout.item_vewpager, null);
			ImageView iv_leftpic=(ImageView) view.findViewById(R.id.iv_leftpic);
			ImageView iv_rightpic=(ImageView) view.findViewById(R.id.iv_rightpic);
			iv_leftpic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(ctx,FullScreenImageActivity.class);
					 List<String> imageUrls = new ArrayList<String>();
					for (int i = 0; i < imgList.size(); i++) {
						imageUrls.add(imgList.get(i));
					}
					intent.putExtra("currentPosition", position*2);
					intent.putStringArrayListExtra("urls",
							(ArrayList<String>) imageUrls);
					intent.setClass(ctx,FullScreenImageActivity.class);
					ctx.startActivity(intent);
				}
			});
			iv_rightpic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
						
						Intent intent=new Intent(ctx,FullScreenImageActivity.class);
						 List<String> imageUrls = new ArrayList<String>();
						for (int i = 0; i < imgList.size(); i++) {
							imageUrls.add(imgList.get(i));
						}
						intent.putExtra("currentPosition", position*2+1);
						intent.putStringArrayListExtra("urls",
								(ArrayList<String>) imageUrls);
						intent.setClass(ctx,FullScreenImageActivity.class);
						ctx.startActivity(intent);
				}
			});
			mImageLoader.displayImage(imgList.get(position*2), iv_leftpic, mDisplayImageOptions);
			mImageLoader.displayImage(imgList.get(position*2+1), iv_rightpic, mDisplayImageOptions);
			((ViewPager)container).addView(view);
		}
		
		
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		((ViewPager)container).removeView((View)object);
	}

}
